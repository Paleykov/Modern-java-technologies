package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.server.usersRepository.UserRepository;

import java.util.Map;

public class ListUserAddressesCommand implements ServerCommand {

    private final UserRepository userRepository;

    public ListUserAddressesCommand(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String execute() {
        Map<String, String> all = userRepository.getAllUserAddresses();

        if (all.isEmpty()) {
            return "No users registered.\n__END__";
        }

        StringBuilder sb = new StringBuilder();
        all.forEach((user, addr) -> sb.append(user).append(" – ").append(addr).append("\n"));
        sb.append("__END__"); // <-- MARK END

        return sb.toString();
    }

}
