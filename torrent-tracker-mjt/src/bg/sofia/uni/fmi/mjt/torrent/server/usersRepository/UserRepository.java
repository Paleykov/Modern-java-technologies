package bg.sofia.uni.fmi.mjt.torrent.server.usersRepository;

import java.util.Map;
import java.util.Set;

public interface UserRepository {

    /**
     * Registers a user with their IP, port, and list of files.
     *
     * @param username the unique name of the user
     * @param ip the IP address of the user
     * @param port the port number of the user's mini server
     * @param files the set of absolute paths of files available for download
     */
    void registerUser(String username, String ip, int port, Set<String> files);

    /**
     * Unregisters specific files for a user.
     *
     * @param username the name of the user
     * @param files the set of files to be removed from availability
     */
    void unregisterFiles(String username, Set<String> files);

    /**
     * Removes a user completely from the repository (e.g. upon disconnect).
     *
     * @param username the name of the user
     */
    void removeUser(String username);

    /**
     * Retrieves all currently available files and the users that provide them.
     *
     * @return a map where each key is a username and the value is the set of file paths
     */
    Map<String, Set<String>> getAllAvailableFiles();

    /**
     * Gets the IP and port for a given user.
     *
     * @param username the name of the user
     * @return the IP and port as a string in the format "IP:port", or null if user doesn't exist
     */
    String getUserAddress(String username);

    /**
     * Checks if a user with the given name is already registered.
     *
     * @param username the name of the user
     * @return true if registered, false otherwise
     */
    boolean isUserRegistered(String username);

    /**
     * Create a map of usernames and adresses for all users.
     *
     * @return a map of username and address+port for each user
     */
    Map<String, String> getAllUserAddresses();

    void addFiles(String username, Set<String> newFiles);
}
