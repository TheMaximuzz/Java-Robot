package robots.gui;

import java.awt.*;
import java.util.List;

public class MazeCollisionManager {
    private final MazeGenerator mazeGenerator;
    private final int blockSize;

    public MazeCollisionManager(MazeGenerator mazeGenerator, int blockSize) {
        this.mazeGenerator = mazeGenerator;
        this.blockSize = blockSize;
    }

    public boolean canMoveTo(double x, double y, double robotWidth, double robotHeight) {
        // Проверяем клетку по сетке
        int gridX = (int)(x / blockSize);
        int gridY = (int)(y / blockSize);
        return mazeGenerator.isCellFree(gridX, gridY);
    }

    public Point adjustPosition(double x, double y, double robotWidth, double robotHeight) {
        double newX = Math.max(robotWidth/2, Math.min(x, blockSize * 28 - robotWidth/2));
        double newY = Math.max(robotHeight/2, Math.min(y, blockSize * 31 - robotHeight/2));
        return new Point((int)newX, (int)newY);
    }
}