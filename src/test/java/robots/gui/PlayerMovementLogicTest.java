package robots.gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;
import java.util.Locale;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerMovementLogicTest {

    private GameVisualizer gameVisualizer;
    private MazeGenerator mazeGenerator;
    private int blockSize = 32;

    @BeforeEach
    void setUp() throws Exception {
        mazeGenerator = Mockito.mock(MazeGenerator.class);
        when(mazeGenerator.getBlockSize()).thenReturn(blockSize);
        when(mazeGenerator.getRandomFreePosition()).thenReturn(new Point(32, 32));
        when(mazeGenerator.isCellFree(anyInt(), anyInt())).thenReturn(true);
        gameVisualizer = new GameVisualizer(Locale.getDefault());

        Field mazeField = GameVisualizer.class.getDeclaredField("mazeGenerator");
        mazeField.setAccessible(true);
        mazeField.set(gameVisualizer, mazeGenerator);
    }

    // Первые 4 теста для проверки взаимодействия с клавиатурой в разных направлениях
    @Test
    void testKeyPressRight() throws Exception {
        KeyEvent rightKey = new KeyEvent(gameVisualizer, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT, KeyEvent.CHAR_UNDEFINED);
        invokeKeyPressed(rightKey);

        Field pendingDirectionField = GameVisualizer.class.getDeclaredField("pendingDirection");
        pendingDirectionField.setAccessible(true);
        int pendingDirection = (int) pendingDirectionField.get(gameVisualizer);
        assertEquals(0, pendingDirection);
    }

    @Test
    void testKeyPressLeft() throws Exception {
        KeyEvent leftKey = new KeyEvent(gameVisualizer, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_LEFT, KeyEvent.CHAR_UNDEFINED);
        invokeKeyPressed(leftKey);

        Field pendingDirectionField = GameVisualizer.class.getDeclaredField("pendingDirection");
        pendingDirectionField.setAccessible(true);
        int pendingDirection = (int) pendingDirectionField.get(gameVisualizer);
        assertEquals(2, pendingDirection);
    }

    @Test
    void testKeyPressUp() throws Exception {
        KeyEvent upKey = new KeyEvent(gameVisualizer, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED);
        invokeKeyPressed(upKey);

        Field pendingDirectionField = GameVisualizer.class.getDeclaredField("pendingDirection");
        pendingDirectionField.setAccessible(true);
        int pendingDirection = (int) pendingDirectionField.get(gameVisualizer);
        assertEquals(3, pendingDirection);
    }

    @Test
    void testKeyPressDown() throws Exception {
        KeyEvent downKey = new KeyEvent(gameVisualizer, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED);
        invokeKeyPressed(downKey);

        Field pendingDirectionField = GameVisualizer.class.getDeclaredField("pendingDirection");
        pendingDirectionField.setAccessible(true);
        int pendingDirection = (int) pendingDirectionField.get(gameVisualizer);
        assertEquals(1, pendingDirection);
    }


    // Вторые 4 теста, для проверки updatePlayer, которые корректно направляет игрока
    @Test
    void testRobotMovesRightWhenAnimationComplete() throws Exception {
        setField("robotGridX", 1);
        setField("robotGridY", 1);
        setField("animationProgress", 1.0f);
        setField("pendingDirection", 0);
        setField("currentDirection", -1);
        when(mazeGenerator.isCellFree(2, 1)).thenReturn(true);

        invokeMethod("updatePlayer");
        assertEquals(2, getField("targetGridX"));
        assertEquals(0.0f, getField("animationProgress"));
    }

    @Test
    void testRobotMovesLeftWhenAnimationComplete() throws Exception {
        setField("robotGridX", 1);
        setField("robotGridY", 1);
        setField("targetGridX", 1);
        setField("targetGridY", 1);
        setField("animationProgress", 1.0f);
        setField("pendingDirection", 2);
        setField("currentDirection", -1);
        when(mazeGenerator.isCellFree(0, 1)).thenReturn(true);

        invokeMethod("updatePlayer");
        assertEquals(0, getField("targetGridX"));
        assertEquals(1, getField("targetGridY"));
        assertEquals(0.0f, getField("animationProgress"));
    }

    @Test
    void testRobotMovesUpWhenAnimationComplete() throws Exception {
        setField("robotGridX", 1);
        setField("robotGridY", 1);
        setField("targetGridX", 1);
        setField("targetGridY", 1);
        setField("animationProgress", 1.0f);
        setField("pendingDirection", 3);
        setField("currentDirection", -1);
        when(mazeGenerator.isCellFree(1, 0)).thenReturn(true);

        invokeMethod("updatePlayer");
        assertEquals(1, getField("targetGridX"));
        assertEquals(0, getField("targetGridY"));
        assertEquals(0.0f, getField("animationProgress"));
    }

    @Test
    void testRobotMovesDownWhenAnimationComplete() throws Exception {
        setField("robotGridX", 1);
        setField("robotGridY", 1);
        setField("targetGridX", 1);
        setField("targetGridY", 1);
        setField("animationProgress", 1.0f);
        setField("pendingDirection", 1);
        setField("currentDirection", -1);
        when(mazeGenerator.isCellFree(1, 2)).thenReturn(true);

        invokeMethod("updatePlayer");
        assertEquals(1, getField("targetGridX"));
        assertEquals(2, getField("targetGridY"));
        assertEquals(0.0f, getField("animationProgress"));
    }

    // Проверка, что игрок не продолжает движение, встречая стену
    @Test
    void testRobotStopsAtWall() throws Exception {
        setField("robotGridX", 1);
        setField("robotGridY", 1);
        setField("targetGridX", 1);
        setField("targetGridY", 1);
        setField("animationProgress", 1.0f);
        setField("pendingDirection", 0);
        setField("currentDirection", -1);
        when(mazeGenerator.isCellFree(2, 1)).thenReturn(false);

        invokeMethod("updatePlayer");
        assertEquals(1, getField("targetGridX"));
        assertEquals(true, getField("isStopped"));
    }

    // Проверка корректности плавности
    @Test
    void testAnimationProgressIncreases() throws Exception {
        setField("robotGridX", 1);
        setField("robotGridY", 1);
        setField("targetGridX", 2);
        setField("targetGridY", 1);
        setField("animationProgress", 0.5f);

        invokeMethod("updatePlayer");
        float newProgress = (float) getField("animationProgress");
        assertTrue(newProgress > 0.5f);
        assertTrue(newProgress <= 1.0f);
    }

    // Вспомогательные методы для инициализации приватных полей:
    private void invokeKeyPressed(KeyEvent event) throws Exception {
        for (KeyListener listener : gameVisualizer.getKeyListeners()) {
            listener.keyPressed(event);
        }
    }

    private void invokeMethod(String methodName) throws Exception {
        Method method = GameVisualizer.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(gameVisualizer);
    }

    private void setField(String fieldName, Object value) throws Exception {
        Field field = GameVisualizer.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(gameVisualizer, value);
    }

    private Object getField(String fieldName) throws Exception {
        Field field = GameVisualizer.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(gameVisualizer);
    }
}