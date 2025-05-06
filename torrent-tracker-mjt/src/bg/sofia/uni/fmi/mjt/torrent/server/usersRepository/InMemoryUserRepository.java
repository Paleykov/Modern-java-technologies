package bg.sofia.uni.fmi.mjt.torrent.server.usersRepository;

import bg.sofia.uni.fmi.mjt.torrent.server.userInfo.UserInfo;

import javax.print.attribute.standard.RequestingUserName;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository{
    private final Map<String, UserInfo> users = new ConcurrentHashMap<>();

    @Override
    public synchronized void registerUser(String username, String ip, int port, Set<String> files) {
        users.compute(username, (key, existingUser) -> {
            if (existingUser == null) {
                return new UserInfo(ip, port, files);
            } else {
//                existingUser.setIp(ip);
//                existingUser.setPort(port);
                existingUser.getSharedFiles().addAll(files);
                return existingUser;
            }
        });
    }

    @Override
    public synchronized void unregisterFiles(String username, Set<String> files) {
        UserInfo userInfo = users.get(username);
        if (userInfo == null) {
            throw new IllegalArgumentException("User '" + username + "' not found.");
        }

        userInfo.getSharedFiles().removeAll(files);
    }

    @Override
    public void removeUser(String username) {
        if (users.remove(username) == null) {
            throw new IllegalArgumentException("User '" + username + "' not found.");
        }
    }

    @Override
    public synchronized Map<String, Set<String>> getAllAvailableFiles() {
        Map<String, Set<String>> result = new HashMap<>();

        for (Map.Entry<String, UserInfo> entry : users.entrySet()) {
            result.put(entry.getKey(), new HashSet<>(entry.getValue().getSharedFiles()));
        }

        return result;
    }

    @Override
    public synchronized String getUserAddress(String username) {
        UserInfo userInfo = users.get(username);
        if (userInfo == null) {
            return null;
        }

        return userInfo.getIp() + ":" + userInfo.getPort();
    }

    @Override
    public boolean isUserRegistered(String username) {
        return users.containsKey(username);
    }

    @Override
    public synchronized Map<String, String> getAllUserAddresses() {
        Map<String, String> result = new HashMap<>();

        for (Map.Entry<String, UserInfo> entry : users.entrySet()) {
            String username = entry.getKey();
            UserInfo info = entry.getValue();
            String address = info.getIp() + ":" + info.getPort();

            System.out.println("[UserRepository] " + username + " → " + address);

            result.put(username, address);
        }

        return result;
    }

    @Override
    public synchronized void addFiles(String username, Set<String> files) {
        UserInfo userInfo = users.get(username);
        if (userInfo != null) {
            userInfo.getSharedFiles().addAll(files);
        }
    }


}
