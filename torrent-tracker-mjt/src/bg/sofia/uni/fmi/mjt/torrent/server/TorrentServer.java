package bg.sofia.uni.fmi.mjt.torrent.server;

import bg.sofia.uni.fmi.mjt.torrent.command.Command;
import bg.sofia.uni.fmi.mjt.torrent.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.*;
import bg.sofia.uni.fmi.mjt.torrent.server.userInfo.UserInfo;
import bg.sofia.uni.fmi.mjt.torrent.server.usersRepository.InMemoryUserRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.usersRepository.UserRepository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class TorrentServer {
    private static final int BUFFER_SIZE = 1024;
    private static final String HOST = "localhost";
    private static final String PORT = "7777";

    private UserRepository users;

    private boolean isServerWorking;

    private ByteBuffer buffer;
    private Selector selector;


    public TorrentServer(InMemoryUserRepository users){
        this.users = users;
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(HOST, Integer.parseInt(this.PORT)));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private String getClientInput(SocketChannel clientChannel) throws IOException {
        buffer.clear();

        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            clientChannel.close();
            return null;
        }

        buffer.flip();

        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    private void writeClientOutput(SocketChannel clientChannel, String output) throws IOException {
        buffer.clear();
        buffer.put((output + System.lineSeparator()).getBytes(StandardCharsets.UTF_8)); // ✅ newline added here
        buffer.flip();
        clientChannel.write(buffer);
    }


    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    private ServerCommand resolveCommand(Command command, SocketChannel clientSocket) {
        return switch (command.command()) {
            case "register" -> new RegisterCommand(command, clientSocket, users);
            case "unregister" -> new UnregisterCommand(command, users);
            case "list-files" -> new ListFilesCommand(users);
            case "list-user-addresses" -> new ListUserAddressesCommand(users);
            case "add-files" -> new AddFilesCommand(command, users);
            default -> () -> "Unknown command: " + command.command();
        };
    }


    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, selector);
            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
            isServerWorking = true;
            while (isServerWorking) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }

                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();

                        if (key.isReadable()) {
                            SocketChannel clientChannel = (SocketChannel) key.channel();
                            String clientInput = getClientInput(clientChannel);
                            System.out.println(clientInput);

                            if (clientInput == null) {
                                continue;
                            }

                            Command command = CommandCreator.newCommand(clientInput.trim());
                            ServerCommand serverCommand = resolveCommand(command, clientChannel);
                            String response = serverCommand.execute();
                            writeClientOutput(clientChannel, response);

                        } else if (key.isAcceptable()) {
                            accept(selector, key);
                        }

                        keyIterator.remove();
                    }
                } catch (IOException e) {
                    System.out.println("Error occurred while processing client request: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("failed to start server", e);
        }
    }

    public void stop() {
        this.isServerWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Torrent server set up on localhost:7777");

        TorrentServer server = new TorrentServer(new InMemoryUserRepository());

        System.out.println("Type 'start' to start the server, 'stop' to shut it down.");

        boolean started = false;
        while (true) {
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "start" -> {
                    if (!started) {
                        new Thread(server::start).start();
                        started = true;
                        System.out.println("Server started on port 7777");
                    } else {
                        System.out.println("Server is already running.");
                    }
                }
                case "stop" -> {
                    if (started) {
                        server.stop();
                        System.out.println("Server stopped.");
                    } else {
                        System.out.println("Server is not running.");
                    }
                    return;
                }
                default -> System.out.println("Unknown command. Use 'start' or 'stop'.");
            }
        }
    }
}