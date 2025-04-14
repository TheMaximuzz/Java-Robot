package robots.gui;

import java.awt.*;

public class Wall {
    private final int x;
    private final int y;
    private final int size;

    public Wall(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(new Color(0, 255, 255)); // Неоновый циан
        g2d.fillRoundRect(x, y, size, size, 10, 10); // Скругленные углы
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }
}