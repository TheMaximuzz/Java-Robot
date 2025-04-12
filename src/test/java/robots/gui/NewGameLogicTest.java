package robots.gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static org.junit.jupiter.api.Assertions.*;

public class NewGameLogicTest {
    private GameVisualizer visualizer;
    private static final double DELTA = 0.0001;
    private static final double SPEED = 2.0;

    @BeforeEach
    void setUp() {
        visualizer = new GameVisualizer();
        visualizer.setSize(new Dimension(400, 400));
        visualizer.m_robotPositionX = 100;
        visualizer.m_robotPositionY = 100;
        visualizer.m_robotDirection = 0;
        visualizer.currentDirection = 0;
        visualizer.isStopped = false;
    }

    // 1: Проверка изменения направления при нажатии стрелок
    @Test
    void testKeyPressChangesDirection() {
        simulateKeyPress(KeyEvent.VK_RIGHT);
        assertEquals(0, visualizer.currentDirection, "Направление должно быть вправо");
        assertEquals(0, visualizer.m_robotDirection, DELTA, "Угол должен быть 0 радиан");

        simulateKeyPress(KeyEvent.VK_DOWN);
        assertEquals(1, visualizer.currentDirection, "Направление должно быть вниз");
        assertEquals(3 * Math.PI / 2, visualizer.m_robotDirection, DELTA, "Угол должен быть 3pi/2 радиан");

        simulateKeyPress(KeyEvent.VK_LEFT);
        assertEquals(2, visualizer.currentDirection, "Направление должно быть влево");
        assertEquals(Math.PI, visualizer.m_robotDirection, DELTA, "Угол должен быть pi радиан");

        simulateKeyPress(KeyEvent.VK_UP);
        assertEquals(3, visualizer.currentDirection, "Направление должно быть вверх");
        assertEquals(Math.PI / 2, visualizer.m_robotDirection, DELTA, "Угол должен быть pi/2 радиан");
    }

    // 2: Проверка совершения движения в каждом направлении
    @Test
    void testMovementInAllDirections() {
        // вправо
        visualizer.m_robotPositionX = 100;
        visualizer.m_robotPositionY = 100;
        visualizer.currentDirection = 0;
        visualizer.isStopped = false;
        visualizer.moveRobot();
        assertEquals(100 + SPEED, visualizer.m_robotPositionX, DELTA, "X не изменился при движении вправо");
        assertEquals(100, visualizer.m_robotPositionY, DELTA, "Y не должен меняться при движении вправо");

        // вниз
        visualizer.m_robotPositionX = 100;
        visualizer.m_robotPositionY = 100;
        visualizer.currentDirection = 1;
        visualizer.isStopped = false;
        visualizer.moveRobot();
        assertEquals(100, visualizer.m_robotPositionX, DELTA, "X не должен меняться при движении вниз");
        assertEquals(100 + SPEED, visualizer.m_robotPositionY, DELTA, "Y не изменился при движении вниз");

        // влево
        visualizer.m_robotPositionX = 100;
        visualizer.m_robotPositionY = 100;
        visualizer.currentDirection = 2;
        visualizer.isStopped = false;
        visualizer.moveRobot();
        assertEquals(100 - SPEED, visualizer.m_robotPositionX, DELTA, "X не изменился при движении влево");
        assertEquals(100, visualizer.m_robotPositionY, DELTA, "Y не должен меняться при движении влево");

        // вверх
        visualizer.m_robotPositionX = 100;
        visualizer.m_robotPositionY = 100;
        visualizer.currentDirection = 3;
        visualizer.isStopped = false;
        visualizer.moveRobot();
        assertEquals(100, visualizer.m_robotPositionX, DELTA, "X не должен меняться при движении вниз");
        assertEquals(100 - SPEED, visualizer.m_robotPositionY, DELTA, "Y не изменился при движении вверх");
    }

    // 3: Проверка остановки у левой границы
    @Test
    void testStopAtLeftBoundary() {
        visualizer.m_robotPositionX = 0;
        visualizer.currentDirection = 2;
        visualizer.isStopped = false;
        visualizer.moveRobot();
        assertTrue(visualizer.isStopped, "Червяк должен остановиться у левой границы");
        assertEquals(0, visualizer.m_robotPositionX, DELTA, "X не должен измениться");
    }

    // 4: Проверка остановки у правой границы
    @Test
    void testStopAtRightBoundary() {
        visualizer.m_robotPositionX = 400;
        visualizer.currentDirection = 0;
        visualizer.isStopped = false;
        visualizer.moveRobot();
        assertTrue(visualizer.isStopped, "Червяк должен остановиться у правой границы");
        assertEquals(400, visualizer.m_robotPositionX, DELTA, "X не должен измениться");
    }

    // 5: Проверка остановки у верхней границы
    @Test
    void testStopAtTopBoundary() {
        visualizer.m_robotPositionY = 0;
        visualizer.currentDirection = 3;
        visualizer.isStopped = false;
        visualizer.moveRobot();
        assertTrue(visualizer.isStopped, "Червяк должен остановиться у верхней границы");
        assertEquals(0, visualizer.m_robotPositionY, DELTA, "Y не должен измениться");
    }

    // 6: Проверка остановки у нижней границы
    @Test
    void testStopAtBottomBoundary() {
        visualizer.m_robotPositionY = 400;
        visualizer.currentDirection = 1;
        visualizer.isStopped = false;
        visualizer.moveRobot();
        assertTrue(visualizer.isStopped, "Червяк должен остановиться у нижней границы");
        assertEquals(400, visualizer.m_robotPositionY, DELTA, "Y не должен измениться");
    }

    // 7: Проверка возобновления движения после столкновения с границей
    @Test
    void testResumeMovementAfterDirectionChange() {
        visualizer.m_robotPositionX = 400;
        visualizer.currentDirection = 0; // вправо
        visualizer.isStopped = false;
        visualizer.moveRobot();
        assertTrue(visualizer.isStopped, "Червяк должен остановиться у правой границы");

        // поворачиваем влево
        simulateKeyPress(KeyEvent.VK_LEFT);
        assertFalse(visualizer.isStopped, "Червяк должен возобновить движение");
        assertEquals(2, visualizer.currentDirection, "Червяк должен повернуть влево");
        visualizer.moveRobot();
        assertEquals(400 - SPEED, visualizer.m_robotPositionX, DELTA, "Червяк должен двигаться влево");
    }

    // доп метод для имитации нажатия клавиши
    private void simulateKeyPress(int keyCode) {
        KeyEvent event = new KeyEvent(visualizer, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, keyCode, KeyEvent.CHAR_UNDEFINED);
        for (KeyListener listener : visualizer.getKeyListeners()) {
            listener.keyPressed(event);
        }
    }
}