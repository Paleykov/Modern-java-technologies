package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.command.Command;
import bg.sofia.uni.fmi.mjt.torrent.server.usersRepository.UserRepository;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RegisterCommand implements ServerCommand {
    private final Command command;
    private final SocketChannel clientSocket;
    private final UserRepository userRepository;

    public RegisterCommand(Command command, SocketChannel clientSocket, UserRepository userRepository) {
        this.command = command;
        this.clientSocket = clientSocket;
        this.userRepository = userRepository;
    }

    @Override
    public String execute() {
        String[] args = command.arguments();
        if (args.length < 4) {
            return "Error: register requires username, ip, port, and at least one file.";
        }

        String username = args[0];
        String ip = args[1];
        int port;
        try {
            port = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            return "Error: Invalid port number.";
        }

        Set<String> files = new HashSet<>(Arrays.asList(Arrays.copyOfRange(args, 3, args.length)));

        userRepository.registerUser(username, ip, port, files);
        return "Registered user " + username + " with " + files.size() + " files.";
    }

}