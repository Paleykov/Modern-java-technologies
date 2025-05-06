package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.torrent.client.miniserver.P2Pserver;
import bg.sofia.uni.fmi.mjt.torrent.client.peerData.PeerAddressResolver;
import bg.sofia.uni.fmi.mjt.torrent.client.peerData.PeerDownloader;
import bg.sofia.uni.fmi.mjt.torrent.client.peerData.PeerUpdaterThread;
import bg.sofia.uni.fmi.mjt.torrent.command.Command;
import bg.sofia.uni.fmi.mjt.torrent.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.torrent.server.userInfo.UserInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Set;

public class Client {
    private static final String HOST = "localhost";
    private static final String PORT = "7777";

    private final String username;
    private final UserInfo user;
    private static final int BUFFER_SIZE = 1024;

    private SocketChannel socketChannel;
    private final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    private P2Pserver miniServer;
    private Thread miniServerThread;

    private PeerUpdaterThread updater;
    private Thread updaterThread;

    public Client(String username, UserInfo user) {
        this.username = username;
        this.user = user;
    }


    private void connect() {
        try {
            socketChannel = SocketChannel.open(new InetSocketAddress(HOST, Integer.parseInt(PORT)));
        } catch (IOException e) {
            throw new RuntimeException("Could not connect to server", e);
        }
    }

    private void sendMessage(String message) throws IOException {
        buffer.clear();
        buffer.put(message.getBytes(StandardCharsets.UTF_8));
        buffer.flip();
        socketChannel.write(buffer);
    }

    private String receiveMessage() throws IOException {
        buffer.clear();
        int bytesRead = socketChannel.read(buffer);

        if (bytesRead == -1) {
            return null;
        }

        buffer.flip();
        byte[] replyBytes = new byte[buffer.remaining()];
        buffer.get(replyBytes);
        return new String(replyBytes, StandardCharsets.UTF_8);
    }

    private void disconnect() {
        try {
            if (socketChannel != null && socketChannel.isOpen()) {
                socketChannel.close();
            }
        } catch (IOException e) {
            System.err.println("Failed to close socket: " + e.getMessage());
        }
    }

    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            // 1. Register the user first
            connect();
            sendMessage(buildRegisterCommand());
            String registrationResponse = receiveMessage();
            System.out.println("Server: " + registrationResponse);
            disconnect();

            // 2. Start the mini-server for incoming downloads
            miniServer = new P2Pserver(user.getPort(), user);
            miniServerThread = new Thread(miniServer);
            miniServerThread.start();
            System.out.println("[Client] P2P mini-server started on port " + user.getPort());

            // 3. Start the updater thread to fetch peer mappings
            updater = new PeerUpdaterThread();
            updaterThread = new Thread(updater);
            updaterThread.start();
            System.out.println("[Client] PeerUpdaterThread started");

            // 4. Enter the interactive command loop
            connect(); // persistent connection for commands
            System.out.println("Connected to the main server.");
            System.out.println("Type commands. Type 'disconnect' to exit.");

            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();
                Command command = CommandCreator.newCommand(input);

                if (command.command().equalsIgnoreCase("disconnect")) {
                    sendMessage(input);
                    System.out.println("Disconnected from server.");
                    break;
                }

                if (command.command().equalsIgnoreCase("download")) {
                    handleDownloadCommand(command);
                    continue;
                }

                sendMessage(input);
                String reply = receiveMessage();

                if (reply == null) {
                    System.out.println("Server closed the connection.");
                    break;
                }

                System.out.println("Server: " + reply);
            }

        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            disconnect();
            if (miniServer != null) miniServer.stop();
            if (updater != null) updater.stop();
        }
    }


    private void handleDownloadCommand(Command command) {
        String[] args = command.arguments();
        if (args.length != 3) {
            System.out.println("Usage: download <user> <remote-path> <save-path>");
            return;
        }

        String targetUser = args[0];
        String remotePath = args[1];
        String localPath = args[2];

        try {
            InetSocketAddress peerAddress = PeerAddressResolver.resolve(targetUser);
            PeerDownloader.downloadFile(peerAddress, remotePath, localPath);
            System.out.println("File downloaded to " + localPath);

            // Register the newly downloaded file with correct IP and P2P port
            connect();
            sendMessage("register " + username + " " + user.getIp() + " " + user.getPort() + " " + localPath);
            String response = receiveMessage();
            System.out.println("Server: " + response);
            disconnect();

        } catch (Exception e) {
            System.out.println("Download failed: " + e.getMessage());
        }
    }

    private String buildRegisterCommand() {
        StringBuilder sb = new StringBuilder();
        sb.append("register ")
                .append(username).append(" ")
                .append(user.getIp()).append(" ")
                .append(user.getPort()).append(" ");

        for (String file : user.getSharedFiles()) {
            sb.append(file).append(" ");
        }

        return sb.toString().trim();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your username: ");
        String username = scanner.nextLine().trim();

        int port;
        while (true) {
            System.out.print("Enter your mini-server port: ");
            String portInput = scanner.nextLine().trim();
            try {
                port = Integer.parseInt(portInput);
                if (port < 1024 || port > 65535) {
                    System.out.println("Port must be between 1024 and 65535.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid port. Please enter a valid number.");
            }
        }

        Set<String> sharedFiles;
        while (true) {
            System.out.print("Enter full paths to files to share (comma-separated): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("You must share at least one file to participate.");
                continue;
            }

            sharedFiles = Set.of(input.split("\\s*,\\s*"));
            break;
        }

        // Localhost IP, can be adjusted if needed
        UserInfo localUser = new UserInfo("127.0.0.1", port, sharedFiles);
        Client client = new Client(username, localUser);
        client.run();
    }
}
