package robots.gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.*;

public class NewGameLogicTest {
    private GameVisualizer visualizer;
    private static final double DELTA = 0.0001;

    @BeforeEach
    void setUp() throws Exception {
        visualizer = new GameVisualizer(Locale.getDefault());
        visualizer.setRobotPosition(1, 1);
        setField("currentDirection", 0);
        setField("pendingDirection", -1);
        setField("isStopped", false);
        setField("animationProgress", 1.0f);
    }

    // 1: Проверка изменения направления при нажатии стрелок
    @Test
    void testKeyPressChangesDirection() {
        simulateKeyPress(KeyEvent.VK_RIGHT);
        assertEquals(0, getField("pendingDirection"));
        setField("animationProgress", 1.0f);
        visualizer.updatePlayer();
        assertEquals(0, getField("currentDirection"));

        simulateKeyPress(KeyEvent.VK_DOWN);
        assertEquals(1, getField("pendingDirection"));
        setField("animationProgress", 1.0f);
        visualizer.updatePlayer();
        assertEquals(1, getField("currentDirection"));

        simulateKeyPress(KeyEvent.VK_LEFT);
        assertEquals(2, getField("pendingDirection"));
        setField("animationProgress", 1.0f);
        visualizer.updatePlayer();
        assertEquals(2, getField("currentDirection"));

        simulateKeyPress(KeyEvent.VK_UP);
        assertEquals(3, getField("pendingDirection"));
        setField("animationProgress", 1.0f);
        visualizer.updatePlayer();
        assertEquals(3, getField("currentDirection"));
    }

    // 2: Проверка совершения движения в каждом направлении
    @Test
    void testMovementInAllDirections() {
        visualizer.setRobotPosition(13, 15);

        // Вправо
        setField("currentDirection", 0);
        setField("isStopped", false);
        setField("animationProgress", 1.0f);
        int initialX = visualizer.getRobotGridX();
        int initialY = visualizer.getRobotGridY();
        visualizer.moveRobot();
        assertEquals(0.0f, (float) getField("animationProgress"), DELTA);
        assertEquals(initialX + 1, getField("targetGridX"));
        setField("animationProgress", 0.75f);
        visualizer.updatePlayer();
        assertEquals(initialX + 1, visualizer.getRobotGridX());
        assertEquals(initialY, visualizer.getRobotGridY());

        // Вниз
        visualizer.setRobotPosition(10, 10);
        setField("currentDirection", 1);
        setField("isStopped", false);
        setField("animationProgress", 1.0f);
        initialX = visualizer.getRobotGridX();
        initialY = visualizer.getRobotGridY();
        visualizer.moveRobot();
        assertEquals(0.0f, (float) getField("animationProgress"), DELTA);
        assertEquals(initialY + 1, getField("targetGridY"));
        setField("animationProgress", 0.75f);
        visualizer.updatePlayer();
        assertEquals(initialX, visualizer.getRobotGridX());
        assertEquals(initialY + 1, visualizer.getRobotGridY());

        // Влево
        visualizer.setRobotPosition(13, 15);
        setField("currentDirection", 2);
        setField("isStopped", false);
        setField("animationProgress", 1.0f);
        initialX = visualizer.getRobotGridX();
        initialY = visualizer.getRobotGridY();
        visualizer.moveRobot();
        assertEquals(0.0f, (float) getField("animationProgress"), DELTA);
        assertEquals(initialX - 1, getField("targetGridX"));
        setField("animationProgress", 0.75f);
        visualizer.updatePlayer();
        assertEquals(initialX - 1, visualizer.getRobotGridX());
        assertEquals(initialY, visualizer.getRobotGridY());

        // Вверх
        visualizer.setRobotPosition(13, 15);
        setField("currentDirection", 3);
        setField("isStopped", false);
        setField("animationProgress", 1.0f);
        initialX = visualizer.getRobotGridX();
        initialY = visualizer.getRobotGridY();
        visualizer.moveRobot();
        assertEquals(0.0f, (float) getField("animationProgress"), DELTA);
        assertEquals(initialY - 1, getField("targetGridY"));
        setField("animationProgress", 0.75f);
        visualizer.updatePlayer();
        assertEquals(initialX, visualizer.getRobotGridX());
        assertEquals(initialY - 1, visualizer.getRobotGridY());
    }

    // 3: Проверка остановки у левой границы
    @Test
    void testStopAtLeftBoundary() {
        visualizer.setRobotPosition(0, 1);
        setField("currentDirection", 2);
        setField("isStopped", false);
        setField("animationProgress", 1.0f);
        visualizer.moveRobot();
        assertTrue((boolean) getField("isStopped"));
        assertEquals(0, visualizer.getRobotGridX());
    }

    // 4: Проверка остановки у правой границы
    @Test
    void testStopAtRightBoundary() {
        visualizer.setRobotPosition(27, 1);
        setField("currentDirection", 0);
        setField("isStopped", false);
        setField("animationProgress", 1.0f);
        visualizer.moveRobot();
        assertTrue((boolean) getField("isStopped"));
        assertEquals(27, visualizer.getRobotGridX());
    }

    // 5: Проверка остановки у верхней границы
    @Test
    void testStopAtTopBoundary() {
        visualizer.setRobotPosition(1, 0);
        setField("currentDirection", 3);
        setField("isStopped", false);
        setField("animationProgress", 1.0f);
        visualizer.moveRobot();
        assertTrue((boolean) getField("isStopped"));
        assertEquals(0, visualizer.getRobotGridY());
    }

    // 6: Проверка остановки у нижней границы
    @Test
    void testStopAtBottomBoundary() {
        visualizer.setRobotPosition(1, 30);
        setField("currentDirection", 1);
        setField("isStopped", false);
        setField("animationProgress", 1.0f);
        visualizer.moveRobot();
        assertTrue((boolean) getField("isStopped"));
        assertEquals(30, visualizer.getRobotGridY());
    }

    // 7: Проверка возобновления движения после столкновения с границей
    @Test
    void testResumeMovementAfterDirectionChange() {
        // Игрок у правой границы
        visualizer.setRobotPosition(27, 1);
        setField("currentDirection", 0);
        setField("isStopped", false);
        setField("animationProgress", 1.0f);
        visualizer.moveRobot();
        assertTrue((boolean) getField("isStopped"), "Игрок должен остановиться у правой границы");

        // Поворачиваем влево
        simulateKeyPress(KeyEvent.VK_LEFT);
        assertEquals(2, getField("pendingDirection"));
        setField("animationProgress", 1.0f);
        visualizer.updatePlayer();
        assertEquals(2, getField("currentDirection"));
        assertFalse((boolean) getField("isStopped"), "Игрок должен возобновить движение");

        visualizer.moveRobot();
        setField("animationProgress", 0.75f);
        visualizer.updatePlayer();
        assertEquals(26, visualizer.getRobotGridX(), "Игрок должен переместиться влево");
    }


    // Доп. методы для имитации нажатия клавиш и доступа к приватным полям

    private void simulateKeyPress(int keyCode) {
        KeyEvent event = new KeyEvent(visualizer, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, keyCode, KeyEvent.CHAR_UNDEFINED);
        for (KeyListener listener : visualizer.getKeyListeners()) {
            listener.keyPressed(event);
        }
    }

    private void setField(String fieldName, Object value) {
        try {
            Field field = GameVisualizer.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(visualizer, value);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось установить поле " + fieldName, e);
        }
    }

    private Object getField(String fieldName) {
        try {
            Field field = GameVisualizer.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(visualizer);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось получить поле " + fieldName, e);
        }
    }
}