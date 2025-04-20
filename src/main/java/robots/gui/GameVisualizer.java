package robots.gui;

import robots.profile.Profile;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

public class GameVisualizer extends JPanel {
    private final Timer m_timer = initTimer();
    private final MazeGenerator mazeGenerator;
    private final int robotSize;
    private volatile int robotGridX;
    private volatile int robotGridY;
    private volatile int targetGridX;
    private volatile int targetGridY;
    private volatile float animationProgress = 1.0f;
    private static final float animationSpeed = 0.25f;
    private volatile int currentDirection = -1;
    private volatile int pendingDirection = -1;
    private volatile boolean isStopped = false;
    private final List<Enemy> enemies;
    private volatile boolean gameOver = false;
    private Timer phaseTimer = new Timer("phase timer", true);
    private Enemy.Mode currentMode = Enemy.Mode.CHASE;
    private static final long CHASE_DURATION = 15000;
    private static final long CALM_DURATION = 5000;

    private static Timer initTimer() {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    public GameVisualizer() {
        mazeGenerator = new MazeGenerator(800, 600, 32);
        robotSize = mazeGenerator.getBlockSize() / 2;
        Point startPos = mazeGenerator.getRandomFreePosition();
        robotGridX = startPos.x / mazeGenerator.getBlockSize();
        robotGridY = startPos.y / mazeGenerator.getBlockSize();
        targetGridX = robotGridX;
        targetGridY = robotGridY;

        enemies = new ArrayList<>();
        initializeEnemies();

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
        }, 0, 50);

        scheduleNextPhase();
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameOver) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        restartGame();
                    }
                    return;
                }
                int newDirection = -1;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        newDirection = 3;
                        break;
                    case KeyEvent.VK_DOWN:
                        newDirection = 1;
                        break;
                    case KeyEvent.VK_LEFT:
                        newDirection = 2;
                        break;
                    case KeyEvent.VK_RIGHT:
                        newDirection = 0;
                        break;
                }
                if (newDirection != -1 && newDirection != pendingDirection) {
                    pendingDirection = newDirection;
                    if (animationProgress >= 1.0f) {
                        isStopped = false;
                    }
                }
            }
        });

        setDoubleBuffered(true);
        setBackground(Color.BLACK);
    }

    private void initializeEnemies() {
        int enemySize = robotSize - 4;
        for (int i = 0; i < 3; i++) {
            Point pos;
            int gridX, gridY;
            do {
                pos = mazeGenerator.getRandomFreePosition();
                gridX = pos.x / mazeGenerator.getBlockSize();
                gridY = pos.y / mazeGenerator.getBlockSize();
            } while (gridX == robotGridX && gridY == robotGridY);
            enemies.add(new Enemy(mazeGenerator, gridX, gridY, enemySize));
        }
    }

    private void restartGame() {
        Point startPos = mazeGenerator.getRandomFreePosition();
        robotGridX = startPos.x / mazeGenerator.getBlockSize();
        robotGridY = startPos.y / mazeGenerator.getBlockSize();
        targetGridX = robotGridX;
        targetGridY = robotGridY;
        animationProgress = 1.0f;
        currentDirection = -1;
        pendingDirection = -1;
        isStopped = false;
        enemies.clear();
        initializeEnemies();
        gameOver = false;
        currentMode = Enemy.Mode.CHASE;
        for (Enemy enemy : enemies) {
            enemy.setMode(currentMode);
        }
        scheduleNextPhase();
        requestFocusInWindow();
    }

    private void scheduleNextPhase() {
        phaseTimer.purge();
        phaseTimer.cancel();
        phaseTimer = new Timer("phase timer", true);
        long duration = (currentMode == Enemy.Mode.CHASE) ? CHASE_DURATION : CALM_DURATION;
        phaseTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                switchPhase();
            }
        }, duration);
    }

    private void switchPhase() {
        currentMode = (currentMode == Enemy.Mode.CHASE) ? Enemy.Mode.CALM : Enemy.Mode.CHASE;
        for (Enemy enemy : enemies) {
            enemy.setMode(currentMode);
        }
        scheduleNextPhase();
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    protected void onModelUpdateEvent() {
        if (gameOver) {
            return;
        }
        updatePlayer();
        updateEnemies();
        onRedrawEvent();
    }

    private void updatePlayer() {
        if (animationProgress < 1.0f) {
            animationProgress += animationSpeed;
            if (animationProgress >= 1.0f) {
                animationProgress = 1.0f;
                robotGridX = targetGridX;
                robotGridY = targetGridY;
                if (pendingDirection != -1 && pendingDirection != currentDirection) {
                    currentDirection = pendingDirection;
                    isStopped = false;
                }
            }
            return;
        }

        if (isStopped && pendingDirection == -1) {
            return;
        }

        boolean isOnHorizontalLine = mazeGenerator.isCellFree(robotGridX - 1, robotGridY) ||
                mazeGenerator.isCellFree(robotGridX + 1, robotGridY);
        boolean isOnVerticalLine = mazeGenerator.isCellFree(robotGridX, robotGridY - 1) ||
                mazeGenerator.isCellFree(robotGridX, robotGridY + 1);

        if (pendingDirection != -1) {
            if ((pendingDirection == 0 || pendingDirection == 2) && isOnHorizontalLine) {
                currentDirection = pendingDirection;
            } else if ((pendingDirection == 1 || pendingDirection == 3) && isOnVerticalLine) {
                currentDirection = pendingDirection;
            } else {
                currentDirection = pendingDirection;
            }
        }

        if (currentDirection != -1) {
            moveRobot();
        }
    }

    private void updateEnemies() {
        for (Enemy enemy : enemies) {
            enemy.update(robotGridX, robotGridY);
            if (enemy.collidesWithPlayer(robotGridX, robotGridY)) {
                gameOver = true;
                return;
            }
        }
    }

    protected void moveRobot() {
        int newGridX = robotGridX;
        int newGridY = robotGridY;

        switch (currentDirection) {
            case 0:
                newGridX += 1;
                break;
            case 1:
                newGridY += 1;
                break;
            case 2:
                newGridX -= 1;
                break;
            case 3:
                newGridY -= 1;
                break;
        }

        if (mazeGenerator.isCellFree(newGridX, newGridY)) {
            targetGridX = newGridX;
            targetGridY = newGridY;
            animationProgress = 0.0f;
            isStopped = false;
        } else {
            isStopped = true;
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        mazeGenerator.draw(g2d);
        drawRobot(g2d);
        for (Enemy enemy : enemies) {
            enemy.draw(g2d, mazeGenerator.getBlockSize());
        }
        if (gameOver) {
            Font gameOverFont = new Font("Arial", Font.BOLD, 36);
            g.setFont(gameOverFont);
            String gameOverText = "GAME OVER! Нажмите ПРОБЕЛ, для рестарта.";

            FontMetrics metrics = g.getFontMetrics(gameOverFont);
            int textWidth = metrics.stringWidth(gameOverText);
            int textHeight = metrics.getHeight();
            int textX = (900 - textWidth) / 2;
            int textY = 500;

            g.setColor(Color.BLACK);
            int padding = 10;
            g.fillRect(textX - padding, textY - textHeight + padding, textWidth + 2 * padding, textHeight);

            g.setColor(Color.RED);
            g.drawString(gameOverText, textX, textY);
        }
    }

    private void drawRobot(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        int blockSize = mazeGenerator.getBlockSize();
        float currentX = robotGridX + (targetGridX - robotGridX) * animationProgress;
        float currentY = robotGridY + (targetGridY - robotGridY) * animationProgress;
        int robotX = (int) (currentX * blockSize + (blockSize - robotSize) / 2);
        int robotY = (int) (currentY * blockSize + (blockSize - robotSize) / 2);
        g.fillOval(robotX, robotY, robotSize, robotSize);
        g.setColor(Color.BLACK);
        g.drawOval(robotX, robotY, robotSize, robotSize);
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public int getRobotGridX() {
        return robotGridX;
    }

    public int getRobotGridY() {
        return robotGridY;
    }

    public MazeGenerator getMazeGenerator() {
        return mazeGenerator;
    }

    public int getRobotSize() {
        return robotSize;
    }

    public void setRobotPosition(int gridX, int gridY) {
        this.robotGridX = gridX;
        this.robotGridY = gridY;
        this.targetGridX = gridX;
        this.targetGridY = gridY;
        this.animationProgress = 1.0f;
        repaint();
    }

    public void setEnemyPositions(List<Profile.MobPosition> positions) {
        this.enemies.clear();
        int enemySize = robotSize - 4;
        for (Profile.MobPosition pos : positions) {
            Enemy enemy = new Enemy(mazeGenerator, pos.getMobX(), pos.getMobY(), enemySize);
            enemy.setMode(currentMode);
            this.enemies.add(enemy);
        }
        repaint();
    }
}