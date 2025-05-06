package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.command.Command;
import bg.sofia.uni.fmi.mjt.torrent.server.usersRepository.UserRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UnregisterCommand implements ServerCommand {
    private final Command command;
    private final UserRepository userRepository;

    public UnregisterCommand(Command command, UserRepository userRepository) {
        this.command = command;
        this.userRepository = userRepository;
    }

    @Override
    public String execute() {
        String[] args = command.arguments();
        if (args.length < 2) {
            return "Error: unregister requires a username and at least one file to remove.";
        }

        String username = args[0];
        Set<String> files = new HashSet<>(Arrays.asList(Arrays.copyOfRange(args, 1, args.length)));

        try {
            userRepository.unregisterFiles(username, files);
            return "Unregistered files for user " + username;
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        }
    }
}

