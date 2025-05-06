package bg.sofia.uni.fmi.mjt.torrent.server.userInfo;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserInfo {
    private final String ip;
    private final int port;
    private final Set<String> sharedFiles;

    public UserInfo(String ip, int port, Set<String> sharedFiles) {
        this.ip = ip;
        this.port = port;
        this.sharedFiles = ConcurrentHashMap.newKeySet();
        this.sharedFiles.addAll(sharedFiles);
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    public Set<String> getSharedFiles() {
        return this.sharedFiles;
    }
}

