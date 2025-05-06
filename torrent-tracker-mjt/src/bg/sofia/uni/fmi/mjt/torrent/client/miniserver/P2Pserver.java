package bg.sofia.uni.fmi.mjt.torrent.client.miniserver;

import bg.sofia.uni.fmi.mjt.torrent.server.userInfo.UserInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class P2Pserver implements Runnable {
    private final int port;
    private final UserInfo localUserInfo;
    private final ExecutorService downloadPool = Executors.newFixedThreadPool(50);
    private volatile boolean running = true;

    public P2Pserver(int port, UserInfo localUserInfo) {
        this.port = port;
        this.localUserInfo = localUserInfo;
    }

    public void stop() {
        running = false;
        downloadPool.shutdown();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("P2P mini-server listening on port " + port);

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    downloadPool.submit(new DownloadHandler(clientSocket, localUserInfo));
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Mini-server error while accepting: " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Mini-server failed to start: " + e.getMessage());
        }
    }
}

