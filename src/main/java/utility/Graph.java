package utility;

import java.util.*;

public class Graph {
    private final int width;
    private final int height;
    private final boolean[][] grid; // Raster für die Bewegungsgrenzen

    public Graph(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new boolean[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = true; // Alle Positionen sind betretbar
            }
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public boolean isWalkable(int x, int y) {
        return isInBounds(x, y) && grid[y][x];
    }

    public List<int[]> dijkstra(int startX, int startY, int targetX, int targetY) {
        // Überprüfe die Grenzen der Start- und Zielkoordinaten
        if (!isInBounds(startX, startY) || !isInBounds(targetX, targetY)) {
            throw new IllegalArgumentException("Start- oder Zielkoordinaten sind außerhalb der Grenzen.");
        }

        int[][] distances = new int[height][width];
        boolean[][] visited = new boolean[height][width];

        // Initialisiere Entfernungen mit MAX_VALUE und setze Startposition
        for (int[] row : distances) Arrays.fill(row, Integer.MAX_VALUE);
        distances[startY][startX] = 0;

        // Prioritätswarteschlange für die Knoten
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(node -> node.distance));
        queue.add(new Node(startX, startY, 0));

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            int x = current.x, y = current.y;

            // Überprüfe, ob der Knoten bereits besucht wurde
            if (visited[y][x]) continue;
            visited[y][x] = true;

            // Überprüfe, ob das Ziel erreicht wurde
            if (x == targetX && y == targetY) {
                return reconstructPath(distances, startX, startY, targetX, targetY);
            }

            // Erkunde benachbarte Knoten
            for (int[] direction : new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}}) {
                int newX = x + direction[0];
                int newY = y + direction[1];

                // Überprüfe die Grenzen und ob das Feld begehbar ist
                if (isInBounds(newX, newY) && isWalkable(newX, newY) && !visited[newY][newX]) {
                    int newDist = distances[y][x] + 1;
                    if (newDist < distances[newY][newX]) {
                        distances[newY][newX] = newDist;
                        queue.add(new Node(newX, newY, newDist));
                    }
                }
            }
        }
        return Collections.emptyList(); // Kein Pfad gefunden
    }

    // Hilfsmethode zur Überprüfung, ob die Koordinaten innerhalb der Grenzen sind
    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private List<int[]> reconstructPath(int[][] distances, int startX, int startY, int targetX, int targetY) {
        List<int[]> path = new ArrayList<>();
        int currentX = targetX;
        int currentY = targetY;

        while (currentX != startX || currentY != startY) {
            path.add(new int[]{currentX, currentY});
            boolean found = false; // Flag um zu sehen, ob ein Nachfolger gefunden wurde

            for (int[] direction : new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}}) {
                int newX = currentX + direction[0];
                int newY = currentY + direction[1];
                if (isInBounds(newX, newY) && distances[newY][newX] < distances[currentY][currentX]) {
                    currentX = newX;
                    currentY = newY;
                    found = true;
                    break;
                }
            }

            // Wenn kein Nachfolger gefunden wurde, breche die Schleife ab, um eine Endlosschleife zu vermeiden
            if (!found) {
                break;
            }
        }

        Collections.reverse(path);
        return path;
    }

    private static class Node {
        int x, y, distance;
        Node(int x, int y, int distance) {
            this.x = x;
            this.y = y;
            this.distance = distance;
        }
    }
}
