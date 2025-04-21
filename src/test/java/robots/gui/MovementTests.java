package robots.gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robots.gui.Enemy;
import robots.gui.GameVisualizer;
import robots.gui.MazeGenerator;

import java.awt.Point;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class MovementTests {
    private GameVisualizer gameVisualizer;
    private MazeGenerator mazeGenerator;
    private List<Enemy> enemies;

    @BeforeEach
    public void setUp() throws Exception {
        // Инициализация GameVisualizer с локалью по умолчанию
        gameVisualizer = new GameVisualizer(Locale.getDefault());

        // Получение mazeGenerator через рефлексию, так как он приватный
        Field mazeField = GameVisualizer.class.getDeclaredField("mazeGenerator");
        mazeField.setAccessible(true);
        mazeGenerator = (MazeGenerator) mazeField.get(gameVisualizer);

        // Получение списка врагов
        Field enemiesField = GameVisualizer.class.getDeclaredField("enemies");
        enemiesField.setAccessible(true);
        enemies = (List<Enemy>) enemiesField.get(gameVisualizer);
    }

    @Test
    public void testPlayerMovementWithinBounds() throws Exception {
        // Тест: Проверяет, что игрок может двигаться в свободную клетку
        // Устанавливаем игрока в клетку (1, 1), где справа свободно (2, 1)
        gameVisualizer.setRobotPosition(1, 1);
        int initialX = gameVisualizer.getRobotGridX();
        int initialY = gameVisualizer.getRobotGridY();

        // Проверяем, что начальная позиция корректна
        assertEquals(1, initialX, "Начальная X-координата игрока должна быть 1");
        assertEquals(1, initialY, "Начальная Y-координата игрока должна быть 1");

        // Проверяем, что клетка справа свободна
        assertTrue(mazeGenerator.isCellFree(initialX + 1, initialY),
                "Клетка (" + (initialX + 1) + ", " + initialY + ") должна быть свободной");

        // Устанавливаем направление вправо (0)
        setPrivateField(gameVisualizer, "pendingDirection", 0);
        setPrivateField(gameVisualizer, "animationProgress", 1.0f);

        // Вызываем обновление игрока несколько раз, чтобы анимация завершилась
        for (int i = 0; i < 5; i++) { // 5 вызовов достаточно, чтобы animationProgress достигло 1.0f
            invokePrivateMethod(gameVisualizer, "updatePlayer");
        }

        // Проверяем, что игрок переместился вправо
        assertEquals(initialX + 1, gameVisualizer.getRobotGridX(),
                "Игрок должен переместиться вправо на одну клетку");
        assertEquals(initialY, gameVisualizer.getRobotGridY(),
                "Y-координата игрока не должна измениться");
    }

    @Test
    public void testPlayerCannotMoveThroughWalls() throws Exception {
        // Тест: Проверяет, что игрок не может пройти через стену
        // Устанавливаем игрока рядом со стеной (например, в позицию, где справа стена)
        gameVisualizer.setRobotPosition(1, 1); // Позиция рядом со стеной в ASCII-лабиринте

        // Пытаемся двигаться вправо, где стена
        setPrivateField(gameVisualizer, "pendingDirection", 0);
        setPrivateField(gameVisualizer, "animationProgress", 1.0f);

        invokePrivateMethod(gameVisualizer, "updatePlayer");

        // Игрок не должен переместиться
        assertEquals(1, gameVisualizer.getRobotGridX());
        assertEquals(1, gameVisualizer.getRobotGridY());
    }

    @Test
    public void testPlayerCannotMoveOutOfBounds() throws Exception {
        // Тест: Проверяет, что игрок не может выйти за границы карты
        // Устанавливаем игрока на край карты (например, x=27, y=1)
        gameVisualizer.setRobotPosition(27, 1);

        // Пытаемся двигаться вправо за пределы карты
        setPrivateField(gameVisualizer, "pendingDirection", 0);
        setPrivateField(gameVisualizer, "animationProgress", 1.0f);

        invokePrivateMethod(gameVisualizer, "updatePlayer");

        // Игрок не должен выйти за пределы (x=27 — максимум)
        assertEquals(27, gameVisualizer.getRobotGridX());
        assertEquals(1, gameVisualizer.getRobotGridY());
    }


    @Test
    public void testEnemyMovementInCalmMode() {
        // Тест: Проверяет, что враг в режиме CALM движется случайным образом
        Enemy enemy = enemies.get(0);
        enemy.setMode(Enemy.Mode.CALM);

        int initialEnemyX = enemy.getGridX();
        int initialEnemyY = enemy.getGridY();

        // Многократное обновление для увеличения шанса случайного движения
        for (int i = 0; i < 10; i++) {
            enemy.update(gameVisualizer.getRobotGridX(), gameVisualizer.getRobotGridY());
        }

        // Проверяем, что враг хотя бы раз переместился
        assertTrue(enemy.getGridX() != initialEnemyX || enemy.getGridY() != initialEnemyY,
                "Враг не переместился в режиме CALM!");
    }

    @Test
    public void testEnemyCannotMoveThroughWalls() {
        // Тест: Проверяет, что враг не проходит через стены
        Enemy enemy = enemies.get(0);
        enemy.setMode(Enemy.Mode.CHASE);

        // Устанавливаем игрока так, чтобы путь к нему был заблокирован стеной
        gameVisualizer.setRobotPosition(1, 3); // Игрок за стеной
        enemy.update(1, 1); // Враг в (1,1)

        // Проверяем, что враг не прошел через стену
        assertFalse(enemy.getGridX() == 1 && enemy.getGridY() == 3,
                "Враг прошел через стену!");
    }


    @Test
    public void testMazeGeneratorCellFree() {
        // Тест: Проверяет корректность метода isCellFree в MazeGenerator
        // Проверяем свободную клетку (например, 1,1 в ASCII-лабиринте)
        assertTrue(mazeGenerator.isCellFree(1, 1), "Клетка (1,1) должна быть свободной!");

        // Проверяем занятую клетку (например, 0,0 — стена)
        assertFalse(mazeGenerator.isCellFree(0, 0), "Клетка (0,0) должна быть стеной!");

        // Проверяем выход за границы
        assertFalse(mazeGenerator.isCellFree(28, 1), "Клетка за границами не должна быть свободной!");
        assertFalse(mazeGenerator.isCellFree(-1, 1), "Отрицательная координата не должна быть свободной!");
    }

    // Вспомогательные методы для доступа к приватным полям и методам
    private void setPrivateField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    private void invokePrivateMethod(Object obj, String methodName) throws Exception {
        Method method = obj.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(obj);
    }
}