package bg.sofia.uni.fmi.mjt.poll.commands;

import bg.sofia.uni.fmi.mjt.poll.server.model.Poll;
import bg.sofia.uni.fmi.mjt.poll.server.repository.PollRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandExecutor {
    private final String commandName;
    private final String[] args;

    public CommandExecutor(String rawCommand) {
        Command parsedCommand = CommandCreator.newCommand(rawCommand);
        this.commandName = parsedCommand.command();
        this.args = parsedCommand.arguments();
    }

    public String execute(PollRepository pollRepository) {
        return switch (commandName) {
            case "create-poll" -> createPoll(pollRepository);
            case "list-polls" -> listPolls(pollRepository);
            case "submit-vote" -> submitVote(pollRepository);
            default -> error("Unknown command: " + commandName);
        };
    }

    public String createPoll(PollRepository pollRepository) {
        if (args.length < 3) {
            return error("Usage: create-poll <question> <option-1> <option-2> [... <option-N>]");
        }

        String question = args[0];
        Map<String, AtomicInteger> options = new ConcurrentHashMap<>();

        for (int i = 1; i < args.length; i++) {
            options.put(args[i], new AtomicInteger(0));
        }

        Poll newPoll = new Poll(question, options);
        int id = pollRepository.addPoll(newPoll);

        return String.format("{\"status\":\"OK\",\"message\":\"Poll %d created successfully.\"}", id);
    }

    public String listPolls(PollRepository pollRepository) {
        Map<Integer, Poll> allPolls = pollRepository.getAllPolls();
        if (allPolls.isEmpty()) {
            return error("No active polls available.");
        }

        StringBuilder sb = new StringBuilder("{\"status\":\"OK\",\"polls\":{");

        for (Map.Entry<Integer, Poll> entry : allPolls.entrySet()) {
            int pollId = entry.getKey();
            Poll poll = entry.getValue();

            sb.append("\"").append(pollId).append("\":{");
            sb.append("\"question\":\"").append(poll.name()).append("\",");
            sb.append("\"options\":{");

            boolean firstOption = true;
            for (Map.Entry<String, AtomicInteger> option : poll.options().entrySet()) {
                if (!firstOption) {
                    sb.append(",");
                }
                sb.append("\"").append(option.getKey()).append("\":").append(option.getValue().get());
                firstOption = false;
            }

            sb.append("}},");
        }

        sb.setLength(sb.length() - 1); // remove last comma
        sb.append("}}");

        return sb.toString();
    }

    public String submitVote(PollRepository pollRepository) {
        if (args.length != 2) {
            return error("Usage: submit-vote <poll-id> <option>");
        }

        int pollId;
        try {
            pollId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return error("Poll ID must be an integer.");
        }

        String option = args[1];

        Poll poll = pollRepository.getPoll(pollId);
        if (poll == null) {
            return error("Poll with ID " + pollId + " does not exist.");
        }

        AtomicInteger votes = poll.options().get(option);
        if (votes == null) {
            return error("Invalid option. Option " + option + " does not exist.");
        }

        votes.incrementAndGet();

        return String.format("{\"status\":\"OK\",\"message\":\"Vote submitted successfully for option: %s\"}", option);
    }

    private String error(String message) {
        return "{\"status\":\"ERROR\",\"message\":\"" + message + "\"}";
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }
}

