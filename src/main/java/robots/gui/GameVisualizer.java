package robots.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

public class GameVisualizer extends JPanel {
    private final Timer m_timer = initTimer();
    private final MazeGenerator mazeGenerator;
    private final int robotSize;
    private volatile int robotGridX; // Координаты центра пакмена в ячейках
    private volatile int robotGridY;
    private volatile int targetGridX; // Целевые координаты для анимации
    private volatile int targetGridY;
    private volatile float animationProgress = 1.0f; // Прогресс анимации (0.0 - начало, 1.0 - конец)
    private static final float animationSpeed = 0.25f; // Ускоряем анимацию для плавности (было 0.15f)
    private volatile int currentDirection = -1; // -1: нет движения, 0: вправо, 1: вниз, 2: влево, 3: вверх
    private volatile int pendingDirection = -1; // Ожидаемое направление
    private volatile boolean isStopped = false;

    private static Timer initTimer() {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    public GameVisualizer() {
        mazeGenerator = new MazeGenerator(800, 600, 32);
        robotSize = mazeGenerator.getBlockSize() / 2; // 16

        // Устанавливаем начальную позицию пакмена
        Point startPos = mazeGenerator.getRandomFreePosition();
        robotGridX = startPos.x / mazeGenerator.getBlockSize();
        robotGridY = startPos.y / mazeGenerator.getBlockSize();
        targetGridX = robotGridX;
        targetGridY = robotGridY;

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
        }, 0, 50); // Ускоряем логику: 50 мс (~20 ячеек/с)

        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
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
                        isStopped = false; // Разрешаем движение, если анимация завершена
                    }
                }
            }
        });

        setDoubleBuffered(true);
        setBackground(Color.BLACK);
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    protected void onModelUpdateEvent() {
        if (animationProgress < 1.0f) {
            // Продолжаем анимацию перемещения
            animationProgress += animationSpeed;
            if (animationProgress >= 1.0f) {
                animationProgress = 1.0f;
                robotGridX = targetGridX;
                robotGridY = targetGridY;
                // Проверяем ожидаемое направление после завершения анимации
                if (pendingDirection != -1 && pendingDirection != currentDirection) {
                    currentDirection = pendingDirection;
                    isStopped = false;
                }
            }
            onRedrawEvent();
            return;
        }

        if (isStopped && pendingDirection == -1) {
            return;
        }

        // Проверяем текущую линию
        boolean isOnHorizontalLine = mazeGenerator.isCellFree(robotGridX - 1, robotGridY) ||
                mazeGenerator.isCellFree(robotGridX + 1, robotGridY);
        boolean isOnVerticalLine = mazeGenerator.isCellFree(robotGridX, robotGridY - 1) ||
                mazeGenerator.isCellFree(robotGridX, robotGridY + 1);

        // Если есть ожидаемое направление, пробуем его применить
        if (pendingDirection != -1) {
            // Если направление совпадает с линией, двигаемся сразу
            if ((pendingDirection == 0 || pendingDirection == 2) && isOnHorizontalLine) {
                currentDirection = pendingDirection;
            } else if ((pendingDirection == 1 || pendingDirection == 3) && isOnVerticalLine) {
                currentDirection = pendingDirection;
            } else {
                // Пробуем новое направление
                currentDirection = pendingDirection;
            }
        }

        // Если есть текущее направление, продолжаем движение
        if (currentDirection != -1) {
            moveRobot();
        }
    }

    protected void moveRobot() {
        int newGridX = robotGridX;
        int newGridY = robotGridY;

        switch (currentDirection) {
            case 0: // Вправо
                newGridX += 1;
                break;
            case 1: // Вниз
                newGridY += 1;
                break;
            case 2: // Влево
                newGridX -= 1;
                break;
            case 3: // Вверх
                newGridY -= 1;
                break;
        }

        if (mazeGenerator.isCellFree(newGridX, newGridY)) {
            targetGridX = newGridX;
            targetGridY = newGridY;
            animationProgress = 0.0f; // Начинаем анимацию
            isStopped = false;
        } else {
            isStopped = true;
            // Сохраняем pendingDirection для следующей попытки
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        // Рисуем лабиринт
        mazeGenerator.draw(g2d);

        // Рисуем пакмена
        drawRobot(g2d);
    }

    private void drawRobot(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        int blockSize = mazeGenerator.getBlockSize();
        // Интерполированная позиция для плавного движения
        float currentX = robotGridX + (targetGridX - robotGridX) * animationProgress;
        float currentY = robotGridY + (targetGridY - robotGridY) * animationProgress;
        int robotX = (int) (currentX * blockSize + (blockSize - robotSize) / 2);
        int robotY = (int) (currentY * blockSize + (blockSize - robotSize) / 2);
        g.fillOval(robotX, robotY, robotSize, robotSize);
        g.setColor(Color.BLACK);
        g.drawOval(robotX, robotY, robotSize, robotSize);
    }
}