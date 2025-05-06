package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.command.Command;
import bg.sofia.uni.fmi.mjt.torrent.server.usersRepository.UserRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AddFilesCommand implements ServerCommand {
    private final Command command;
    private final UserRepository userRepository;

    public AddFilesCommand(Command command, UserRepository userRepository) {
        this.command = command;
        this.userRepository = userRepository;
    }

    @Override
    public String execute() {
        String[] args = command.arguments();
        if (args.length < 2) {
            return "Error: add-files requires a username and at least one file.";
        }

        String username = args[0];

        if (!userRepository.isUserRegistered(username)) {
            return "Error: User '" + username + "' is not registered.";
        }

        Set<String> newFiles = new HashSet<>(Arrays.asList(Arrays.copyOfRange(args, 1, args.length)));
        userRepository.addFiles(username, newFiles);

        return "Added " + newFiles.size() + " files to user '" + username + "'.";
    }
}
