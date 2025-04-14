package robots.gui;

import java.awt.*;
import java.util.List;

public class MazeCollisionManager {
    private final List<Wall> walls;
    private final int blockSize;

    public MazeCollisionManager(List<Wall> walls, int blockSize) {
        this.walls = walls;
        this.blockSize = blockSize;
    }

    public boolean canMoveTo(double x, double y, double robotWidth, double robotHeight) {
        Rectangle robotBounds = new Rectangle(
                (int)(x - robotWidth/2),
                (int)(y - robotHeight/2),
                (int)robotWidth,
                (int)robotHeight
        );

        for (Wall wall : walls) {
            if (robotBounds.intersects(wall.getBounds())) {
                return false;
            }
        }

        return x >= robotWidth/2 && y >= robotHeight/2 &&
                x <= (blockSize * 28 - robotWidth/2) &&
                y <= (blockSize * 31 - robotHeight/2);
    }

    public Point adjustPosition(double x, double y, double robotWidth, double robotHeight) {
        double newX = Math.max(robotWidth/2, Math.min(x, blockSize * 28 - robotWidth/2));
        double newY = Math.max(robotHeight/2, Math.min(y, blockSize * 31 - robotHeight/2));
        return new Point((int)newX, (int)newY);
    }
}