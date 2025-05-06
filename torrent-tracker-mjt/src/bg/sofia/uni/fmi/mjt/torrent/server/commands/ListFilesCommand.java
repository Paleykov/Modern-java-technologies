package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.server.usersRepository.UserRepository;

import java.util.Map;
import java.util.Set;

public class ListFilesCommand implements ServerCommand {
    private final UserRepository userRepository;

    public ListFilesCommand(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String execute() {
        Map<String, Set<String>> userFiles = userRepository.getAllAvailableFiles();

        if (userFiles.isEmpty()) {
            return "No files registered.";
        }

        StringBuilder result = new StringBuilder();
        for (var entry : userFiles.entrySet()) {
            String username = entry.getKey();
            for (String file : entry.getValue()) {
                result.append(username).append(" : ").append(file).append(System.lineSeparator());
            }
        }

        return result.toString().trim();
    }
}
