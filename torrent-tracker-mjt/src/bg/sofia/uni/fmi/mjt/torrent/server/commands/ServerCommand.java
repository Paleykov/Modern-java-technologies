package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import java.io.IOException;

public interface ServerCommand {
    String execute() throws IOException;
}