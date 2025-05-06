package bg.sofia.uni.fmi.mjt.torrent.client.peerData;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class PeerDownloader {
    private static final int BUFFER_SIZE = 4096;

    public static void downloadFile(InetSocketAddress peerAddress, String remotePath, String localSavePath) throws IOException {
        try (
                Socket socket = new Socket(peerAddress.getAddress(), peerAddress.getPort());
                BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            out.println(remotePath);

            socket.setSoTimeout(2000);
            socket.getOutputStream().flush();

            socket.setSoTimeout(0);
            socket.setTcpNoDelay(true);

            socket.shutdownOutput();

            ByteArrayOutputStream headerBuffer = new ByteArrayOutputStream();
            int ch;
            while ((ch = in.read()) != -1 && ch != '\n') {
                headerBuffer.write(ch);
            }

            String headerLine = headerBuffer.toString().trim();
            if (headerLine.startsWith("ERROR:")) {
                throw new IOException("Peer returned error: " + headerLine);
            }

            try (BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(localSavePath))) {
                fileOut.write(headerBuffer.toByteArray());
                if (ch == '\n') fileOut.write('\n');

                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    fileOut.write(buffer, 0, bytesRead);
                }

                fileOut.flush();
            }

        } catch (IOException e) {
            throw new IOException("Failed to download file from " + peerAddress + ": " + e.getMessage(), e);
        }
    }

}

