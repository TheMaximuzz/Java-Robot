//package robots.gui;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import java.awt.Dimension;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class GameVisualizerTest {
//    private GameVisualizer gameVisualizer;
//
//    @BeforeEach
//    void setUp() {
//        gameVisualizer = new GameVisualizer();
//        gameVisualizer.setSize(new Dimension(300, 300)); // Устанавливаем размер области движения
//    }
//
//    @Test
//    void testMoveRobotForward() {
//        double initialX = gameVisualizer.m_robotPositionX;
//        double initialY = gameVisualizer.m_robotPositionY;
//        double initialDirection = gameVisualizer.m_robotDirection;
//
//        gameVisualizer.moveRobot(0.1, 0, 10); // Движение вперёд без поворота
//
//        assertEquals(initialDirection, gameVisualizer.m_robotDirection, 1e-6);
//        assertTrue(gameVisualizer.m_robotPositionX > initialX || gameVisualizer.m_robotPositionY > initialY);
//    }
//
//    @Test
//    void testMoveRobotRotation() {
//        double initialDirection = gameVisualizer.m_robotDirection;
//
//        gameVisualizer.moveRobot(0, 0.001, 10); // Только поворот без движения
//
//        assertNotEquals(initialDirection, gameVisualizer.m_robotDirection);
//    }
//
//    @Test
//    void testMoveRobotBoundaryLimits() {
//        gameVisualizer.moveRobot(10, 0, 1000); // Движение вперёд, выход за границы
//
//        assertTrue(gameVisualizer.m_robotPositionX >= 0 && gameVisualizer.m_robotPositionX <= 300);
//        assertTrue(gameVisualizer.m_robotPositionY >= 0 && gameVisualizer.m_robotPositionY <= 300);
//    }
//
//    @Test
//    void testMoveRobotNoMovement() {
//        double initialX = gameVisualizer.m_robotPositionX;
//        double initialY = gameVisualizer.m_robotPositionY;
//        double initialDirection = gameVisualizer.m_robotDirection;
//
//        gameVisualizer.moveRobot(0, 0, 10); // Нет движения
//
//        assertEquals(initialX, gameVisualizer.m_robotPositionX);
//        assertEquals(initialY, gameVisualizer.m_robotPositionY);
//        assertEquals(initialDirection, gameVisualizer.m_robotDirection);
//    }
//}
