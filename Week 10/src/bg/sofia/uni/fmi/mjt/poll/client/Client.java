package bg.sofia.uni.fmi.mjt.poll.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    private static final int BUFFER_SIZE = 1024;

    private final String host;
    private final int port;

    private SocketChannel socketChannel;
    private final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void connect() {
        try {
            socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
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

    public void run() throws IOException {
        try (Scanner scanner = new Scanner(System.in)) {
            connect();

            System.out.println("Connected to the poll server.");
            System.out.println("Type commands. Type 'disconnect' to exit.");

            while (true) {
                System.out.print("> ");
                String message = scanner.nextLine().trim();

                sendMessage(message);

                if ("disconnect".equalsIgnoreCase(message)) {
                    System.out.println("Disconnected from server.");
                    break;
                }

                String reply = receiveMessage();
                if (reply == null) {
                    System.out.println("Server closed the connection.");
                    break;
                }

                System.out.println("Server: " + reply);
            }

        } finally {
            disconnect();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter server host (e.g. localhost): ");
        String host = scanner.nextLine().trim();

        System.out.print("Enter server port: ");
        int port = Integer.parseInt(scanner.nextLine().trim());

        Client client = new Client(host, port);
        client.run();
    }
}
