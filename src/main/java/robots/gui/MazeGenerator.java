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
    private final Point[] portals; // Массив для хранения двух порталов

    public MazeGenerator(int width, int height, int blockSize) {
        this.blockSize = blockSize;
        this.mazeWidth = 28; // Фиксированная ширина по ASCII-схеме
        this.mazeHeight = 31; // Фиксированная высота по ASCII-схеме
        this.walls = new ArrayList<>();
        this.freeSpaces = new ArrayList<>();
        this.portals = new Point[2]; // Два портала
        generateMaze();
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
                "0         X      X         0", // Порталы на позициях (0, 14) и (27, 14)
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
                    freeSpaces.add(new Point(x * blockSize + blockSize/2, y * blockSize + blockSize/2));
                    if (cell == '0') {
                        portals[portalIndex] = new Point(x, y);
                        portalIndex++;
                    }
                }
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

    public void draw(Graphics2D g2d) {
        g2d.setColor(new Color(0, 255, 255)); // Неоновый циан для стен
        for (Wall wall : walls) {
            wall.draw(g2d);
        }
        // Отображение порталов желтым цветом
        g2d.setColor(Color.YELLOW);
        for (Point portal : portals) {
            int drawX = portal.x * blockSize;
            int drawY = portal.y * blockSize;
            g2d.fillRect(drawX, drawY, blockSize, blockSize);
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