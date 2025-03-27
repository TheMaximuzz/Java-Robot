package robots.gui;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import robots.util.ConfirmCloseHelper;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class ConfirmCloseTest {

    private JInternalFrame mockFrame;
    private ConfirmCloseHelper closeHelper;

    @Before
    public void setUp() {
        mockFrame = Mockito.mock(JInternalFrame.class);
        closeHelper = new ConfirmCloseHelper();
    }

    /* ---Проверка функционала ConfirmCloseHelper--- */

    // тест закрытия окна при выборе "Да"
    @Test
    public void testInternalFrameClosing_YesOption() {
        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showConfirmDialog(
                    eq(mockFrame),
                    eq("Вы действительно хотите закрыть окно?"),
                    eq("Подтверждение закрытия"),
                    eq(JOptionPane.YES_NO_OPTION)
            )).thenReturn(JOptionPane.YES_OPTION);

            closeHelper.internalFrameClosing(new InternalFrameEvent(mockFrame, InternalFrameEvent.INTERNAL_FRAME_CLOSING));

            verify(mockFrame, times(1)).dispose();
        }
    }

    // тест не закрытия окна при выборе "Нет"
    @Test
    public void testInternalFrameClosing_NoOption() {
        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showConfirmDialog(
                    eq(mockFrame),
                    eq("Вы действительно хотите закрыть окно?"),
                    eq("Подтверждение закрытия"),
                    eq(JOptionPane.YES_NO_OPTION)
            )).thenReturn(JOptionPane.NO_OPTION);

            closeHelper.internalFrameClosing(new InternalFrameEvent(mockFrame, InternalFrameEvent.INTERNAL_FRAME_CLOSING));

            verify(mockFrame, never()).dispose();
        }
    }

    // тест возврата true при выборе "да"
    @Test
    public void testConfirmClose_YesOption() {
        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showConfirmDialog(
                    eq(mockFrame),
                    eq("Вы действительно хотите закрыть окно?"),
                    eq("Подтверждение закрытия"),
                    eq(JOptionPane.YES_NO_OPTION)
            )).thenReturn(JOptionPane.YES_OPTION);

            boolean result = closeHelper.confirmClose(mockFrame);

            assertTrue(result);
        }
    }

    // тест возврата false при выборе "нет"
    @Test
    public void testConfirmClose_NoOption() {
        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showConfirmDialog(
                    eq(mockFrame),
                    eq("Вы действительно хотите закрыть окно?"),
                    eq("Подтверждение закрытия"),
                    eq(JOptionPane.YES_NO_OPTION)
            )).thenReturn(JOptionPane.NO_OPTION);

            boolean result = closeHelper.confirmClose(mockFrame);

            assertFalse(result);
        }
    }

    /* ---Проверка взаимодействия ConfirmCloseHelper с кодом игры--- */

    // confirmClose(JFrame) при выборе "да"
    @Test
    public void testConfirmCloseJFrame_YesOption() {
        JFrame mockJFrame = Mockito.mock(JFrame.class);
        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showConfirmDialog(
                    eq(null),
                    eq("Вы действительно хотите закрыть приложение?"),
                    eq("Подтверждение выхода"),
                    eq(JOptionPane.YES_NO_OPTION)
            )).thenReturn(JOptionPane.YES_OPTION);

            boolean result = closeHelper.confirmClose(mockJFrame);

            assertTrue(result);
        }
    }

    // confirmClose(JFrame) при выборе "нет"
    @Test
    public void testConfirmCloseJFrame_NoOption() {
        JFrame mockJFrame = Mockito.mock(JFrame.class);
        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showConfirmDialog(
                    eq(null),
                    eq("Вы действительно хотите закрыть приложение?"),
                    eq("Подтверждение выхода"),
                    eq(JOptionPane.YES_NO_OPTION)
            )).thenReturn(JOptionPane.NO_OPTION);

            boolean result = closeHelper.confirmClose(mockJFrame);

            assertFalse(result);
        }
    }

    // showConfirmationDialog в GameWindow (при выборе "да")
    @Test
    public void testShowConfirmationDialog_GameWindowCustomText_YesOption() {
        JInternalFrame mockFrame = Mockito.mock(JInternalFrame.class);
        String customMessage = "Вы действительно хотите закрыть игровое поле?";
        String customTitle = "Подтверждение закрытия";

        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showConfirmDialog(
                    eq(mockFrame),
                    eq(customMessage),
                    eq(customTitle),
                    eq(JOptionPane.YES_NO_OPTION)
            )).thenReturn(JOptionPane.YES_OPTION);

            boolean result = closeHelper.showConfirmationDialog(mockFrame, customMessage, customTitle);

            assertTrue(result);
            mockedOptionPane.verify(() -> JOptionPane.showConfirmDialog(
                    eq(mockFrame),
                    eq(customMessage),
                    eq(customTitle),
                    eq(JOptionPane.YES_NO_OPTION)
            ), times(1));
        }
    }

    // showConfirmationDialog в GameWindow (при выборе "нет")
    @Test
    public void testShowConfirmationDialog_GameWindowCustomText_NoOption() {
        JInternalFrame mockFrame = Mockito.mock(JInternalFrame.class);
        String customMessage = "Вы действительно хотите закрыть игровое поле?";
        String customTitle = "Подтверждение закрытия";

        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showConfirmDialog(
                    eq(mockFrame),
                    eq(customMessage),
                    eq(customTitle),
                    eq(JOptionPane.YES_NO_OPTION)
            )).thenReturn(JOptionPane.NO_OPTION);

            boolean result = closeHelper.showConfirmationDialog(mockFrame, customMessage, customTitle);

            assertFalse(result);
            mockedOptionPane.verify(() -> JOptionPane.showConfirmDialog(
                    eq(mockFrame),
                    eq(customMessage),
                    eq(customTitle),
                    eq(JOptionPane.YES_NO_OPTION)
            ), times(1));
        }
    }

    // showConfirmationDialog в LogWindow (при выборе "да")
    @Test
    public void testShowConfirmationDialog_LogWindowCustomText_YesOption() {
        JInternalFrame mockFrame = Mockito.mock(JInternalFrame.class);
        String customMessage = "Вы действительно хотите закрыть окно логов?";
        String customTitle = "Подтверждение закрытия";

        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showConfirmDialog(
                    eq(mockFrame),
                    eq(customMessage),
                    eq(customTitle),
                    eq(JOptionPane.YES_NO_OPTION)
            )).thenReturn(JOptionPane.YES_OPTION);

            boolean result = closeHelper.showConfirmationDialog(mockFrame, customMessage, customTitle);

            assertTrue(result);
            mockedOptionPane.verify(() -> JOptionPane.showConfirmDialog(
                    eq(mockFrame),
                    eq(customMessage),
                    eq(customTitle),
                    eq(JOptionPane.YES_NO_OPTION)
            ), times(1));
        }
    }

    // showConfirmationDialog в LogWindow (при выборе "нет")
    @Test
    public void testShowConfirmationDialog_LogWindowCustomText_NoOption() {
        JInternalFrame mockFrame = Mockito.mock(JInternalFrame.class);
        String customMessage = "Вы действительно хотите закрыть окно логов?";
        String customTitle = "Подтверждение закрытия";

        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showConfirmDialog(
                    eq(mockFrame),
                    eq(customMessage),
                    eq(customTitle),
                    eq(JOptionPane.YES_NO_OPTION)
            )).thenReturn(JOptionPane.NO_OPTION);

            boolean result = closeHelper.showConfirmationDialog(mockFrame, customMessage, customTitle);

            assertFalse(result);
            mockedOptionPane.verify(() -> JOptionPane.showConfirmDialog(
                    eq(mockFrame),
                    eq(customMessage),
                    eq(customTitle),
                    eq(JOptionPane.YES_NO_OPTION)
            ), times(1));
        }
    }
}