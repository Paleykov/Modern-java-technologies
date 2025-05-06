package bg.sofia.uni.fmi.mjt.torrent.client.peerData;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class PeerUpdaterThread implements Runnable {
    private static final String HOST = "localhost";
    private static final int PORT = 7777;

    private static final int UPDATE_INTERVAL_MS = 30_000;
    private static final Path MAPPING_FILE_PATH = Path.of("peers.txt");

    private volatile boolean running = true;

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        System.out.println("[PeerUpdaterThread] Started");
        while (running) {
            try {
                System.out.println("[PeerUpdaterThread] Fetching peer data...");
                String response = fetchUserAddressesFromServer();
                System.out.println("[PeerUpdaterThread] Response:\n" + response);
                writeToMappingFile(response);
                Thread.sleep(UPDATE_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("[PeerUpdaterThread] Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }



    private String fetchUserAddressesFromServer() throws IOException {
        try (
                Socket socket = new Socket(HOST, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
        ) {
            out.println("list-user-addresses");

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                if (line.equals("__END__")) break; // END DETECTED
                sb.append(line).append(System.lineSeparator());
            }

            return sb.toString().trim();
        }
    }

    private void writeToMappingFile(String data) {
        System.out.println("[PeerUpdaterThread] Writing to file...");
        System.out.println("[PeerUpdaterThread] Data to write:\n" + data);

        try (BufferedWriter writer = Files.newBufferedWriter(MAPPING_FILE_PATH, StandardCharsets.UTF_8)) {
            if (data == null || data.isBlank()) {
                writer.write("# No data received\n");
            } else {
                writer.write(data);
            }
            System.out.println("[PeerUpdaterThread] Write successful!");
        } catch (IOException e) {
            System.err.println("Error writing to peer mapping file: " + e.getMessage());
        }
    }

}
