package robots.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MazeGenerator {
    private final List<Wall> walls;
    private final List<Point> freeSpaces;
    private final int blockSize;
    private final int mazeWidth;
    private final int mazeHeight;
    private final Point[] portals;
    private final List<Point> scorePoints;
    private final int scorePointSize = 4;

    public MazeGenerator(int width, int height, int blockSize) {
        this.blockSize = blockSize;
        this.mazeWidth = 28;
        this.mazeHeight = 31;
        this.walls = new ArrayList<>();
        this.freeSpaces = new ArrayList<>();
        this.portals = new Point[2];
        this.scorePoints = new ArrayList<>();
        generateMaze();
        generateScorePoints();
    }

    private void generateMaze() {
        String[] asciiMaze = {
                "XXXXXXXXXXXXXXXXXXXXXXXXXXXX",
                "X            XX            X",
                "X XXXX XXXXX XX XXXXX XXXX X",
                "X XXXX XXXXX XX XXXXX XXXX X",
                "X XXXX XXXXX XX XXXXX XXXX X",
                "X                          X",
                "X XXXX XX XXXXXXXX XX XXXX X",
                "X XXXX XX XXXXXXXX XX XXXX X",
                "X      XX    XX    XX      X",
                "XXXXXX XXXXX XX XXXXX XXXXXX",
                "XXXXXX XXXXX XX XXXXX XXXXXX",
                "XXXXXX XX          XX XXXXXX",
                "XXXXXX XX XXX  XXX XX XXXXXX",
                "XXXXXX XX X      X XX XXXXXX",
                "0         X      X         0",
                "XXXXXX XX X      X XX XXXXXX",
                "XXXXXX XX XXXXXXXX XX XXXXXX",
                "XXXXXX XX          XX XXXXXX",
                "XXXXXX XX XXXXXXXX XX XXXXXX",
                "XXXXXX XX XXXXXXXX XX XXXXXX",
                "X            XX            X",
                "X XXXX XXXXX XX XXXXX XXXX X",
                "X XXXX XXXXX XX XXXXX XXXX X",
                "X   XX             XX      X",
                "XXX XX XX XXXXXXXX XX XX XXX",
                "XXX XX XX XXXXXXXX XX XX XXX",
                "X      XX    XX    XX      X",
                "X XXXXXXXXXX XX XXXXXXXXXX X",
                "X XXXXXXXXXX XX XXXXXXXXXX X",
                "X                          X",
                "XXXXXXXXXXXXXXXXXXXXXXXXXXXX"
        };

        int portalIndex = 0;
        for (int y = 0; y < asciiMaze.length; y++) {
            String row = asciiMaze[y];
            for (int x = 0; x < row.length(); x++) {
                char cell = row.charAt(x);
                if (cell == 'X') {
                    walls.add(new Wall(x * blockSize, y * blockSize, blockSize));
                } else if (cell == ' ' || cell == 'P' || cell == 'G' || cell == '0') {
                    freeSpaces.add(new Point(x * blockSize + blockSize / 2, y * blockSize + blockSize / 2));
                    if (cell == '0') {
                        portals[portalIndex] = new Point(x, y);
                        portalIndex++;
                    }
                }
            }
        }
    }

    private void generateScorePoints() {
        scorePoints.clear();
        for (Point freeSpace : freeSpaces) {
            int gridX = freeSpace.x / blockSize;
            int gridY = freeSpace.y / blockSize;
            // Skip portals and enemy starting positions
            if (!isPortal(gridX, gridY) &&
                    !(gridX == 14 && gridY == 15) && // Center
                    !(gridX == 13 && gridY == 15) && // Around center
                    !(gridX == 15 && gridY == 15) &&
                    !(gridX == 14 && gridY == 13) &&
                    !(gridX == 14 && gridY == 16)) {
                scorePoints.add(freeSpace);
            }
        }
    }

    public boolean isCellFree(int gridX, int gridY) {
        if (gridX < 0 || gridX >= mazeWidth || gridY < 0 || gridY >= mazeHeight) {
            return false;
        }
        String[] asciiMaze = {
                "XXXXXXXXXXXXXXXXXXXXXXXXXXXX",
                "X            XX            X",
                "X XXXX XXXXX XX XXXXX XXXX X",
                "X XXXX XXXXX XX XXXXX XXXX X",
                "X XXXX XXXXX XX XXXXX XXXX X",
                "X                          X",
                "X XXXX XX XXXXXXXX XX XXXX X",
                "X XXXX XX XXXXXXXX XX XXXX X",
                "X      XX    XX    XX      X",
                "XXXXXX XXXXX XX XXXXX XXXXXX",
                "XXXXXX XXXXX XX XXXXX XXXXXX",
                "XXXXXX XX          XX XXXXXX",
                "XXXXXX XX XXX  XXX XX XXXXXX",
                "XXXXXX XX X      X XX XXXXXX",
                "0         X      X         0",
                "XXXXXX XX X      X XX XXXXXX",
                "XXXXXX XX XXXXXXXX XX XXXXXX",
                "XXXXXX XX          XX XXXXXX",
                "XXXXXX XX XXXXXXXX XX XXXXXX",
                "XXXXXX XX XXXXXXXX XX XXXXXX",
                "X            XX            X",
                "X XXXX XXXXX XX XXXXX XXXX X",
                "X XXXX XXXXX XX XXXXX XXXX X",
                "X   XX             XX      X",
                "XXX XX XX XXXXXXXX XX XX XXX",
                "XXX XX XX XXXXXXXX XX XX XXX",
                "X      XX    XX    XX      X",
                "X XXXXXXXXXX XX XXXXXXXXXX X",
                "X XXXXXXXXXX XX XXXXXXXXXX X",
                "X                          X",
                "XXXXXXXXXXXXXXXXXXXXXXXXXXXX"
        };
        return asciiMaze[gridY].charAt(gridX) != 'X';
    }

    public boolean isPortal(int gridX, int gridY) {
        for (Point portal : portals) {
            if (portal.x == gridX && portal.y == gridY) {
                return true;
            }
        }
        return false;
    }

    public Point getOtherPortal(int gridX, int gridY) {
        for (int i = 0; i < portals.length; i++) {
            if (portals[i].x == gridX && portals[i].y == gridY) {
                return portals[(i + 1) % portals.length];
            }
        }
        return null;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public Point getRandomFreePosition() {
        Random rand = new Random();
        if (freeSpaces.isEmpty()) {
            return new Point(blockSize / 2, blockSize / 2);
        }
        return freeSpaces.get(rand.nextInt(freeSpaces.size()));
    }

    public List<Point> getScorePoints() {
        return scorePoints;
    }

    public void removeScorePoint(Point point) {
        scorePoints.remove(point);
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(new Color(0, 255, 255));
        for (Wall wall : walls) {
            wall.draw(g2d);
        }
        g2d.setColor(Color.YELLOW);
        for (Point portal : portals) {
            int drawX = portal.x * blockSize;
            int drawY = portal.y * blockSize;
            g2d.fillRect(drawX, drawY, blockSize, blockSize);
        }
        g2d.setColor(Color.WHITE);
        for (Point point : scorePoints) {
            int centerX = point.x;
            int centerY = point.y;
            g2d.fillOval(centerX - scorePointSize / 2, centerY - scorePointSize / 2, scorePointSize, scorePointSize);
        }
    }

    public int getBlockSize() {
        return blockSize;
    }

    public int getMazeWidth() {
        return mazeWidth * blockSize;
    }

    public int getMazeHeight() {
        return mazeHeight * blockSize;
    }
}