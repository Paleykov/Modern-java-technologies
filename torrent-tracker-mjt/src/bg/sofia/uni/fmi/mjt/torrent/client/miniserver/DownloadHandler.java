package bg.sofia.uni.fmi.mjt.torrent.client.miniserver;

import bg.sofia.uni.fmi.mjt.torrent.server.userInfo.UserInfo;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class DownloadHandler implements Runnable {
    private final Socket clientSocket;
    private final UserInfo localUserInfo;

    public DownloadHandler(Socket clientSocket, UserInfo localUserInfo) {
        this.clientSocket = clientSocket;
        this.localUserInfo = localUserInfo;
    }

    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream out = clientSocket.getOutputStream()
        ) {
            String requestedFilePath = reader.readLine();

            if (requestedFilePath == null || requestedFilePath.isBlank()) {
                sendError(out, "400 Bad Request: Empty file path");
                return;
            }

            Set<String> sharedFiles = localUserInfo.getSharedFiles();

            if (!sharedFiles.contains(requestedFilePath)) {
                sendError(out, "403 Forbidden: File not registered for sharing");
                return;
            }

            Path filePath = Path.of(requestedFilePath);

            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                sendError(out, "404 Not Found: File does not exist");
                return;
            }

            try (BufferedInputStream fileIn = new BufferedInputStream(Files.newInputStream(filePath))) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fileIn.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                out.flush();
            }

        } catch (IOException e) {
            System.err.println("DownloadHandler error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    private void sendError(OutputStream out, String message) throws IOException {
        out.write(("ERROR: " + message + System.lineSeparator()).getBytes());
        out.flush();
    }
}
