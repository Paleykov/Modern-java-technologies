package bg.sofia.uni.fmi.mjt.glovo.controlcenter.map;

import java.util.ArrayDeque;
import java.util.Queue;

public class PathFinder {
    private final MapEntity[][] layout;

    public PathFinder(MapEntity[][] layout) {
        this.layout = layout;
    }

    public int findDistance(Location from, Location to) {
        int rows = layout.length;
        int cols = layout[0].length;

        boolean[][] visited = new boolean[rows][cols];
        Queue<BFSNode> queue = new ArrayDeque<>();

        queue.add(new BFSNode(from, 0));
        visited[from.x()][from.y()] = true;

        int[][] directions = {
                {0, 1}, {1, 0}, {0, -1}, {-1, 0}
        };

        while (!queue.isEmpty()) {
            BFSNode current = queue.poll();
            Location loc = current.location;

            if (loc.equals(to)) {
                return current.distance;
            }

            for (int[] dir : directions) {
                int newX = loc.x() + dir[0];
                int newY = loc.y() + dir[1];

                if (isValidPosition(newX, newY, visited)) {
                    visited[newX][newY] = true;
                    queue.add(new BFSNode(new Location(newX, newY), current.distance + 1));
                }
            }
        }

        return -1;
    }

    private boolean isValidPosition(int x, int y, boolean[][] visited) {
        return x >= 0 && x < layout.length &&
                y >= 0 && y < layout[0].length &&
                !visited[x][y] &&
                layout[x][y].type() != MapEntityType.WALL;
    }

    private static class BFSNode {
        Location location;
        int distance;

        BFSNode(Location location, int distance) {
            this.location = location;
            this.distance = distance;
        }
    }
}
