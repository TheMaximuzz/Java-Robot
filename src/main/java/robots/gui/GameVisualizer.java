package robots.gui;

import robots.log.Logger;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

public class GameVisualizer extends JPanel {
    private final Timer m_timer = initTimer();

    private static Timer initTimer() {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    protected volatile double m_robotPositionX = 100;
    protected volatile double m_robotPositionY = 100;
    protected volatile double m_robotDirection = 0;

    protected volatile int currentDirection = 0;
    protected volatile boolean isStopped = false;
    private static final double speed = 2.0;

    public GameVisualizer() {

        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);

        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onModelUpdateEvent();
            }
        }, 0, 10);

        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int newDirection = currentDirection;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        newDirection = 3;
                        m_robotDirection = Math.PI / 2;
                        break;
                    case KeyEvent.VK_DOWN:
                        newDirection = 1;
                        m_robotDirection = 3 * Math.PI / 2;
                        break;
                    case KeyEvent.VK_LEFT:
                        newDirection = 2;
                        m_robotDirection = Math.PI;
                        break;
                    case KeyEvent.VK_RIGHT:
                        newDirection = 0;
                        m_robotDirection = 0;
                        break;
                }
                if (newDirection != currentDirection) {
                    currentDirection = newDirection;
                    if (canMoveInDirection()) {
                        isStopped = false;
                    }
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                //System.out.println("GameVisualizer resized to: " + getWidth() + "x" + getHeight());
            }
        });

        setDoubleBuffered(true);
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    protected void onModelUpdateEvent() {
        if (!isStopped) {
            moveRobot();
        }
    }

    protected void moveRobot() {
        double newX = m_robotPositionX;
        double newY = m_robotPositionY;

        switch (currentDirection) {
            case 0: // Вправо
                newX += speed;
                break;
            case 1: // Вниз
                newY += speed;
                break;
            case 2: // Влево
                newX -= speed;
                break;
            case 3: // Вверх
                newY -= speed;
                break;
        }

        boolean hitBoundary = false;

        int currentWidth = getWidth();
        int currentHeight = getHeight();

        if (newX < 0) {
            newX = 0;
            hitBoundary = true;
        }
        if (newY < 0) {
            newY = 0;
            hitBoundary = true;
        }
        if (newX > currentWidth) {
            newX = currentWidth;
            hitBoundary = true;
        }
        if (newY > currentHeight) {
            newY = currentHeight;
            hitBoundary = true;
        }

        if (hitBoundary) {
            isStopped = true;
        }

        m_robotPositionX = newX;
        m_robotPositionY = newY;
    }

    private boolean canMoveInDirection() {
        int currentWidth = getWidth();
        int currentHeight = getHeight();

        switch (currentDirection) {
            case 0: // Вправо
                return m_robotPositionX < currentWidth;
            case 1: // Вниз
                return m_robotPositionY < currentHeight;
            case 2: // Влево
                return m_robotPositionX > 0;
            case 3: // Вверх
                return m_robotPositionY > 0;
            default:
                return false;
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, round(m_robotPositionX), round(m_robotPositionY), m_robotDirection);
    }

    private static int round(double value) {
        return (int) (value + 0.5);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        int robotCenterX = round(m_robotPositionX);
        int robotCenterY = round(m_robotPositionY);
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);
    }
}