package robots.gui;

import java.awt.*;
import java.util.*;

public class Enemy {
    private int gridX;
    private int gridY;
    private int targetGridX;
    private int targetGridY;
    private float animationProgress = 1.0f;
    private final float animationSpeed = 0.2f;
    private final int size;
    private final MazeGenerator mazeGenerator;
    private java.util.List<Point> pathToPlayer;
    private Mode mode = Mode.CHASE;

    public enum Mode {
        CHASE,
        CALM
    }

    public Enemy(MazeGenerator mazeGenerator, int startGridX, int startGridY, int size) {
        this.mazeGenerator = mazeGenerator;
        this.gridX = startGridX;
        this.gridY = startGridY;
        this.targetGridX = startGridX;
        this.targetGridY = startGridY;
        this.size = size;
        this.pathToPlayer = new ArrayList<>();
    }

    public void setMode(Mode newMode) {
        this.mode = newMode;
    }

    public void update(int playerGridX, int playerGridY) {
        if (animationProgress < 1.0f) {
            animationProgress += animationSpeed;
            if (animationProgress >= 1.0f) {
                animationProgress = 1.0f;
                gridX = targetGridX;
                gridY = targetGridY;
            }
            return;
        }

        if (mode == Mode.CHASE) {
            pathToPlayer = findPathToPlayer(playerGridX, playerGridY);
            if (pathToPlayer.size() > 1) {
                Point nextStep = pathToPlayer.get(1);
                targetGridX = nextStep.x;
                targetGridY = nextStep.y;
                animationProgress = 0.0f;
            }
        } else if (mode == Mode.CALM) {
            moveRandomly();
        }
    }

    private void moveRandomly() {
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        java.util.List<int[]> shuffledDirections = Arrays.asList(directions);
        Collections.shuffle(shuffledDirections, new Random());

        for (int[] dir : shuffledDirections) {
            int newGridX = gridX + dir[0];
            int newGridY = gridY + dir[1];
            if (mazeGenerator.isCellFree(newGridX, newGridY)) {
                targetGridX = newGridX;
                targetGridY = newGridY;
                animationProgress = 0.0f;
                break;
            }
        }
    }

    private java.util.List<Point> findPathToPlayer(int playerGridX, int playerGridY) {
        Queue<Point> queue = new LinkedList<>();
        Map<Point, Point> parent = new HashMap<>();
        Set<Point> visited = new HashSet<>();

        Point start = new Point(gridX, gridY);
        Point goal = new Point(playerGridX, playerGridY);

        queue.add(start);
        visited.add(start);
        parent.put(start, null);

        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            if (current.x == goal.x && current.y == goal.y) {
                java.util.List<Point> path = new ArrayList<>();
                Point node = current;
                while (node != null) {
                    path.add(node);
                    node = parent.get(node);
                }
                Collections.reverse(path);
                return path;
            }

            for (int[] dir : directions) {
                int newX = current.x + dir[0];
                int newY = current.y + dir[1];
                Point neighbor = new Point(newX, newY);

                if (!visited.contains(neighbor) && mazeGenerator.isCellFree(newX, newY)) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                }
            }
        }

        return Collections.singletonList(new Point(gridX, gridY));
    }

    public boolean collidesWithPlayer(int playerGridX, int playerGridY) {
        return gridX == playerGridX && gridY == playerGridY;
    }

    public void draw(Graphics2D g, int blockSize) {
        g.setColor(mode == Mode.CHASE ? Color.RED : Color.BLUE);
        float currentX = gridX + (targetGridX - gridX) * animationProgress;
        float currentY = gridY + (targetGridY - gridY) * animationProgress;
        int drawX = (int) (currentX * blockSize + (blockSize - size) / 2);
        int drawY = (int) (currentY * blockSize + (blockSize - size) / 2);
        g.fillOval(drawX, drawY, size, size);
        g.setColor(Color.BLACK);
        g.drawOval(drawX, drawY, size, size);
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public int getSize() {
        return size;
    }

    public Mode getMode() {
        return mode;
    }
}