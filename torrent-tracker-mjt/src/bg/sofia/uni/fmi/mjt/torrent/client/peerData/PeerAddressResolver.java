package bg.sofia.uni.fmi.mjt.torrent.client.peerData;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class PeerAddressResolver {
    private static final Path PEER_MAPPING_PATH = Path.of("peers.txt");

    public static InetSocketAddress resolve(String username) throws IOException {
        if (!Files.exists(PEER_MAPPING_PATH)) {
            throw new IOException("Mapping file peers.txt not found.");
        }

        try (BufferedReader reader = Files.newBufferedReader(PEER_MAPPING_PATH, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(username + " – ")) {
                    String[] parts = line.split(" – ", 2);
                    if (parts.length < 2) continue;

                    String addressPart = parts[1].trim();
                    String[] addressParts = addressPart.split(":", 2);
                    if (addressParts.length != 2) continue;

                    String ip = addressParts[0];
                    int port = Integer.parseInt(addressParts[1]);

                    return new InetSocketAddress(ip, port);
                }
            }
        }

        throw new IOException("Could not find peer address for user: " + username);
    }
}
