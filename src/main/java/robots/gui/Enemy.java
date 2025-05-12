package robots.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Enemy {
    private int gridX;
    private int gridY;
    private int targetGridX;
    private int targetGridY;
    private float animationProgress = 1.0f;
    private final float animationSpeed = 0.2f;
    private final int size;
    private final MazeGenerator mazeGenerator;
    private List<Point> pathToPlayer;
    private Mode mode = Mode.CHASE;
    private boolean isInRandomPhase = true;
    private long randomPhaseEndTime;
    private static final int CENTER_X = 14;
    private static final int CENTER_Y = 15;
    private final List<Enemy> allEnemies;

    public enum Mode {
        CHASE,
        CALM
    }

    public Enemy(MazeGenerator mazeGenerator, int startGridX, int startGridY, int size, List<Enemy> allEnemies) {
        this.mazeGenerator = mazeGenerator;
        this.gridX = startGridX;
        this.gridY = startGridY;
        this.targetGridX = startGridX;
        this.targetGridY = startGridY;
        this.size = size;
        this.pathToPlayer = new ArrayList<>();
        this.randomPhaseEndTime = System.currentTimeMillis() + 2000 + (int)(Math.random() * 1000);
        this.allEnemies = allEnemies;
    }

    public void setMode(Mode newMode) {
        this.mode = newMode;
    }

    public void update(int playerGridX, int playerGridY) {
        if (isInRandomPhase && System.currentTimeMillis() > randomPhaseEndTime) {
            isInRandomPhase = false;
        }

        if (animationProgress < 1.0f) {
            animationProgress += animationSpeed;
            if (animationProgress >= 1.0f) {
                animationProgress = 1.0f;
                gridX = targetGridX;
                gridY = targetGridY;
                // Проверка на портале и телепортация
                if (mazeGenerator.isPortal(gridX, gridY)) {
                    Point otherPortal = mazeGenerator.getOtherPortal(gridX, gridY);
                    gridX = otherPortal.x;
                    gridY = otherPortal.y;
                    targetGridX = gridX;
                    targetGridY = gridY;
                }
            }
            return;
        }

        if (isInRandomPhase) {
            moveRandomly(true);
        } else if (mode == Mode.CHASE) {
            pathToPlayer = findPathToPlayer(playerGridX, playerGridY);
            if (pathToPlayer.size() > 1) {
                Point nextStep = pathToPlayer.get(1);
                if (isPositionFree(nextStep.x, nextStep.y)) {
                    targetGridX = nextStep.x;
                    targetGridY = nextStep.y;
                    animationProgress = 0.0f;
                }
            }
        } else if (mode == Mode.CALM) {
            moveRandomly(false);
        }
    }

    private boolean isPositionFree(int x, int y) {
        for (Enemy other : allEnemies) {
            if (other != this) {
                if ((other.gridX == x && other.gridY == y) ||
                        (other.targetGridX == x && other.targetGridY == y && other.animationProgress < 1.0f)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void moveRandomly(boolean preferOutwardMovement) {
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        List<int[]> possibleDirections = new ArrayList<>();

        for (int[] dir : directions) {
            int newGridX = gridX + dir[0];
            int newGridY = gridY + dir[1];
            if (mazeGenerator.isCellFree(newGridX, newGridY) && isPositionFree(newGridX, newGridY)) {
                possibleDirections.add(dir);
            }
        }

        if (!possibleDirections.isEmpty()) {
            if (preferOutwardMovement) {
                possibleDirections.sort((a, b) -> {
                    double distA = distanceFromCenter(gridX + a[0], gridY + a[1]);
                    double distB = distanceFromCenter(gridX + b[0], gridY + b[1]);
                    return Double.compare(distB, distA);
                });
            } else {
                Collections.shuffle(possibleDirections);
            }

            int[] dir = possibleDirections.get(0);
            targetGridX = gridX + dir[0];
            targetGridY = gridY + dir[1];
            animationProgress = 0.0f;
        }
    }

    private double distanceFromCenter(int x, int y) {
        return Math.sqrt(Math.pow(x - CENTER_X, 2) + Math.pow(y - CENTER_Y, 2));
    }

    private List<Point> findPathToPlayer(int playerGridX, int playerGridY) {
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
                List<Point> path = new ArrayList<>();
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