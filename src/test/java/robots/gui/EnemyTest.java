package robots.gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.Point;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class EnemyTest {
    private GameVisualizer gameVisualizer;

    @BeforeEach
    void setUp() {
        gameVisualizer = new GameVisualizer(Locale.getDefault());
    }

    // 1
    @Test
    void testEnemyCount() {
        List<Enemy> enemies = gameVisualizer.getEnemies();
        assertEquals(3, enemies.size(), "Создаётся неверное количество врагов");
    }

    // 2: Враги не пересекаются друг с другом
    @Test
    void testEnemiesNotOverlapWithEachOther() {
        List<Enemy> enemies = gameVisualizer.getEnemies();
        Set<Point> enemyPositions = new HashSet<>();
        for (Enemy enemy : enemies) {
            int gridX = enemy.getGridX();
            int gridY = enemy.getGridY();
            Point position = new Point(gridX, gridY);
            assertTrue(enemyPositions.add(position), "Враги не должны находиться в одной клетке: " + position);
        }
    }

    // 3: Враги не появляются в клетке игрока
    @Test
    void testEnemiesNotOverlapWithPlayer() {
        int playerGridX = gameVisualizer.getRobotGridX();
        int playerGridY = gameVisualizer.getRobotGridY();
        List<Enemy> enemies = gameVisualizer.getEnemies();

        for (Enemy enemy : enemies) {
            int enemyGridX = enemy.getGridX();
            int enemyGridY = enemy.getGridY();
            assertFalse(enemyGridX == playerGridX && enemyGridY == playerGridY, "Враг не должен появляться в клетке игрока");
        }
    }

    // 4: Враги появляются именно в пустых клетках
    @Test
    void testEnemiesInFreeCells() {
        MazeGenerator mazeGenerator = gameVisualizer.getMazeGenerator();
        List<Enemy> enemies = gameVisualizer.getEnemies();

        for (Enemy enemy : enemies) {
            int enemyGridX = enemy.getGridX();
            int enemyGridY = enemy.getGridY();
            assertTrue(mazeGenerator.isCellFree(enemyGridX, enemyGridY), "Враг появился НЕ в свободной клетке");
        }
    }

    // 5: Корректный размер врагов
    @Test
    void testEnemySize() {
        int expectedEnemySize = gameVisualizer.getRobotSize() - 4;
        List<Enemy> enemies = gameVisualizer.getEnemies();

        for (Enemy enemy : enemies) {
            int enemySize = enemy.getSize();
            assertEquals(expectedEnemySize, enemySize, "Неверный размер для врага");
        }
    }

    // 6: Проверка изменения фаз
    @Test
    void testPhaseSwitching() throws InterruptedException {
        List<Enemy> enemies = gameVisualizer.getEnemies();
        for (Enemy enemy : enemies) {
            assertEquals(Enemy.Mode.CHASE, enemy.getMode(), "Начальная фаза врагов должна быть CHASE");
        }

        Thread.sleep(15000 + 500);
        for (Enemy enemy : enemies) {
            assertEquals(Enemy.Mode.CALM, enemy.getMode(), "После 15 секунд фаза должна смениться на CALM");
        }

        Thread.sleep(5000 + 500);
        for (Enemy enemy : enemies) {
            assertEquals(Enemy.Mode.CHASE, enemy.getMode(), "После 5 секунд CALM фаза должна вернуться на CHASE");
        }
    }

    // 7: Проверка столкновения врагов с игроком
    @Test
    void testCollisionWithPlayer() {
        List<Enemy> enemies = gameVisualizer.getEnemies();
        int playerGridX = gameVisualizer.getRobotGridX();
        int playerGridY = gameVisualizer.getRobotGridY();

        for (Enemy enemy : enemies) {
            assertFalse(enemy.collidesWithPlayer(playerGridX, playerGridY), "Изначально враги не должны сталкиваться с игроком");
        }

        Enemy enemyToCollide = enemies.get(0);
        try {
            java.lang.reflect.Field gridXField = Enemy.class.getDeclaredField("gridX");
            java.lang.reflect.Field gridYField = Enemy.class.getDeclaredField("gridY");
            gridXField.setAccessible(true);
            gridYField.setAccessible(true);
            gridXField.set(enemyToCollide, playerGridX);
            gridYField.set(enemyToCollide, playerGridY);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось изменить координаты врага", e);
        }

        assertTrue(enemyToCollide.collidesWithPlayer(playerGridX, playerGridY), "Враг не столкнулся с игроком, а должен был.");

        // Остальные враги не должны сталкиваться
        for (int i = 1; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            assertFalse(enemy.collidesWithPlayer(playerGridX, playerGridY), "Остальные враги не должны сталкиваться с игроком");
        }
    }

}