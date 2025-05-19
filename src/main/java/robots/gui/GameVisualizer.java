package robots.gui;

import robots.log.Logger;
import robots.profile.Profile;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
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
    private volatile boolean gameWon = false;
    private Timer phaseTimer = new Timer("phase timer", true);
    private Enemy.Mode currentMode = Enemy.Mode.CHASE;
    private static final long CHASE_DURATION = 15000;
    private static final long CALM_DURATION = 5000;
    private ResourceBundle messages;
    private int score = 0;

    private static Timer initTimer() {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    public GameVisualizer(Locale locale) {
        try {
            messages = ResourceBundle.getBundle("messages", locale);
        } catch (MissingResourceException e) {
            messages = null;
            e.printStackTrace();
        }
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
                if (gameOver || gameWon) {
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
                    pendingDirection = newDirection; // Fixed: Assign newDirection to pendingDirection
                    if (animationProgress >= 1.0f) {
                        isStopped = false;
                    }
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setFocusable(true);
                requestFocusInWindow();
            }
        });

        setDoubleBuffered(true);
        setBackground(Color.BLACK);
    }

    private void initializeEnemies() {
        int enemySize = robotSize - 4;
        int centerX = 14;
        int centerY = 15;

        enemies.clear();
        for (int i = 0; i < 3; i++) {
            int gridX, gridY;
            switch (i) {
                case 0: gridX = centerX - 1; gridY = centerY; break;
                case 1: gridX = centerX; gridY = centerY - 2; break;
                case 2: gridX = centerX + 1; gridY = centerY; break;
                default: gridX = centerX; gridY = centerY;
            }
            enemies.add(new Enemy(mazeGenerator, gridX, gridY, enemySize, enemies));
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
        gameWon = false;
        score = 0;
        currentMode = Enemy.Mode.CHASE;
        for (Enemy enemy : enemies) {
            enemy.setMode(currentMode);
        }
        scheduleNextPhase();
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

    private void checkScoreCollection() {
        if (animationProgress < 1.0f) return;

        int blockSize = mazeGenerator.getBlockSize();
        Point playerCenter = new Point(
                robotGridX * blockSize + blockSize / 2,
                robotGridY * blockSize + blockSize / 2
        );

        List<Point> pointsToRemove = new ArrayList<>();
        for (Point point : mazeGenerator.getScorePoints()) {
            if (Math.abs(playerCenter.x - point.x) < blockSize / 2 &&
                    Math.abs(playerCenter.y - point.y) < blockSize / 2) {
                pointsToRemove.add(point);
                score += 10;
                Logger.debug("Score: " + score);
            }
        }

        for (Point point : pointsToRemove) {
            mazeGenerator.removeScorePoint(point);
        }

        if (mazeGenerator.getScorePoints().isEmpty()) {
            gameWon = true;
        }
    }

    protected void onModelUpdateEvent() {
        if (gameOver || gameWon) {
            return;
        }
        checkScoreCollection();
        updatePlayer();
        updateEnemies();
        onRedrawEvent();
    }

    protected void updatePlayer() {
        if (animationProgress < 1.0f) {
            animationProgress += animationSpeed;
            if (animationProgress >= 1.0f) {
                animationProgress = 1.0f;
                robotGridX = targetGridX;
                robotGridY = targetGridY;
                if (mazeGenerator.isPortal(robotGridX, robotGridY)) {
                    Point otherPortal = mazeGenerator.getOtherPortal(robotGridX, robotGridY);
                    robotGridX = otherPortal.x;
                    robotGridY = otherPortal.y;
                    targetGridX = robotGridX;
                    targetGridY = robotGridY;
                }
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

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Score: " + score, 20, 30);

        mazeGenerator.draw(g2d);
        drawRobot(g2d);
        for (Enemy enemy : enemies) {
            enemy.draw(g2d, mazeGenerator.getBlockSize());
        }
        if (gameOver) {
            Font gameOverFont = new Font("Arial", Font.BOLD, 36);
            g.setFont(gameOverFont);
            String gameOverText = messages.getString("gameOverMessage");

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
        } else if (gameWon) {
            Font winFont = new Font("Arial", Font.BOLD, 36);
            g.setFont(winFont);
            String winText = messages.getString("winMessage");

            FontMetrics metrics = g.getFontMetrics(winFont);
            int textWidth = metrics.stringWidth(winText);
            int textHeight = metrics.getHeight();
            int textX = (900 - textWidth) / 2;
            int textY = 500;

            g.setColor(Color.BLACK);
            int padding = 10;
            g.fillRect(textX - padding, textY - textHeight + padding, textWidth + 2 * padding, textHeight);

            g.setColor(Color.GREEN);
            g.drawString(winText, textX, textY);
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
            Enemy enemy = new Enemy(mazeGenerator, pos.getMobX(), pos.getMobY(), enemySize, enemies);
            enemy.setMode(currentMode);
            this.enemies.add(enemy);
        }
        repaint();
    }

    public void updateLanguage(ResourceBundle newMessages) {
        this.messages = newMessages;
        repaint();
    }
}