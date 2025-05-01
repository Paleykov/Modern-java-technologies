package bg.sofia.uni.fmi.mjt.poll.server;

import bg.sofia.uni.fmi.mjt.poll.commands.CommandExecutor;
import bg.sofia.uni.fmi.mjt.poll.server.repository.InMemoryPollRepository;
import bg.sofia.uni.fmi.mjt.poll.server.repository.PollRepository;

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
import java.util.Scanner;

public class PollServer {
    private static final int BUFFER_SIZE = 1024;
    private static final String HOST = "localhost";

    private PollRepository pollRepository;
    private final int port;

    private boolean isServerWorking;

    private ByteBuffer buffer;
    private Selector selector;


    public PollServer(int port, PollRepository pollRepository){
        this.port = port;
        this.pollRepository = pollRepository;
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(HOST, this.port));
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
        buffer.put(output.getBytes());
        buffer.flip();

        clientChannel.write(buffer);
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
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

                            CommandExecutor executor = new CommandExecutor(clientInput);
                            String response = executor.execute(pollRepository);

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

        System.out.print("Enter port to start the server on: ");
        int port;

        while (true) {
            String input = scanner.nextLine();
            try {
                port = Integer.parseInt(input);
                if (port < 1024 || port > 65535) {
                    System.out.print("Please enter a valid port (1024–65535): ");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }

        PollServer server = new PollServer(port, new InMemoryPollRepository());

        System.out.println("Type 'start' to start the server, 'stop' to shut it down.");

        boolean started = false;
        while (true) {
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "start" -> {
                    if (!started) {
                        new Thread(server::start).start();
                        started = true;
                        System.out.println("Server started on port " + port);
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
