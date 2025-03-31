package robots.gui;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import robots.util.ConfirmCloseHelper;
import java.util.ResourceBundle;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class ConfirmCloseTest {

    private JInternalFrame mockFrame;
    private ConfirmCloseHelper closeHelper;
    private ResourceBundle messages;

    @Before
    public void setUp() {
        mockFrame = Mockito.mock(JInternalFrame.class);
        closeHelper = new ConfirmCloseHelper();
        messages = ResourceBundle.getBundle("messages"); // Загружаем ResourceBundle для тестов
    }

    /* ---Проверка функционала ConfirmCloseHelper--- */

    // Тест закрытия окна при выборе "Да"
    @Test
    public void testInternalFrameClosing_YesOption() {
        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showOptionDialog(
                    eq(mockFrame),
                    eq(messages.getString("confirmCloseLogWindow")),
                    eq(messages.getString("confirmCloseTitle")),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE),
                    eq(null),
                    any(Object[].class),
                    any()
            )).thenReturn(JOptionPane.YES_OPTION);

            closeHelper.internalFrameClosing(new InternalFrameEvent(mockFrame, InternalFrameEvent.INTERNAL_FRAME_CLOSING));

            verify(mockFrame, times(1)).dispose();
        }
    }

    // Тест не закрытия окна при выборе "Нет"
    @Test
    public void testInternalFrameClosing_NoOption() {
        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showOptionDialog(
                    eq(mockFrame),
                    eq(messages.getString("confirmCloseLogWindow")),
                    eq(messages.getString("confirmCloseTitle")),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE),
                    eq(null),
                    any(Object[].class),
                    any()
            )).thenReturn(JOptionPane.NO_OPTION);

            closeHelper.internalFrameClosing(new InternalFrameEvent(mockFrame, InternalFrameEvent.INTERNAL_FRAME_CLOSING));

            verify(mockFrame, never()).dispose();
        }
    }

    // Тест возврата true при выборе "Да"
    @Test
    public void testConfirmClose_YesOption() {
        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showOptionDialog(
                    eq(mockFrame),
                    eq(messages.getString("confirmCloseLogWindow")),
                    eq(messages.getString("confirmCloseTitle")),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE),
                    eq(null),
                    any(Object[].class),
                    any()
            )).thenReturn(JOptionPane.YES_OPTION);

            boolean result = closeHelper.confirmClose(mockFrame);

            assertTrue(result);
        }
    }

    // Тест возврата false при выборе "Нет"
    @Test
    public void testConfirmClose_NoOption() {
        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showOptionDialog(
                    eq(mockFrame),
                    eq(messages.getString("confirmCloseLogWindow")),
                    eq(messages.getString("confirmCloseTitle")),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE),
                    eq(null),
                    any(Object[].class),
                    any()
            )).thenReturn(JOptionPane.NO_OPTION);

            boolean result = closeHelper.confirmClose(mockFrame);

            assertFalse(result);
        }
    }

    /* ---Проверка взаимодействия ConfirmCloseHelper с кодом игры--- */

    // confirmClose(JFrame) при выборе "Да"
    @Test
    public void testConfirmCloseJFrame_YesOption() {
        JFrame mockJFrame = Mockito.mock(JFrame.class);
        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showOptionDialog(
                    eq(null),
                    eq(messages.getString("confirmCloseApp")),
                    eq(messages.getString("confirmCloseTitle")),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE),
                    eq(null),
                    any(Object[].class),
                    any()
            )).thenReturn(JOptionPane.YES_OPTION);

            boolean result = closeHelper.confirmClose(mockJFrame);

            assertTrue(result);
        }
    }

    // confirmClose(JFrame) при выборе "Нет"
    @Test
    public void testConfirmCloseJFrame_NoOption() {
        JFrame mockJFrame = Mockito.mock(JFrame.class);
        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showOptionDialog(
                    eq(null),
                    eq(messages.getString("confirmCloseApp")),
                    eq(messages.getString("confirmCloseTitle")),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE),
                    eq(null),
                    any(Object[].class),
                    any()
            )).thenReturn(JOptionPane.NO_OPTION);

            boolean result = closeHelper.confirmClose(mockJFrame);

            assertFalse(result);
        }
    }

    // showConfirmationDialog в GameWindow (при выборе "Да")
    @Test
    public void testShowConfirmationDialog_GameWindowCustomText_YesOption() {
        JInternalFrame mockFrame = Mockito.mock(JInternalFrame.class);
        String customMessage = messages.getString("confirmCloseGameWindow");
        String customTitle = messages.getString("confirmCloseTitle");

        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showOptionDialog(
                    eq(mockFrame),
                    eq(customMessage),
                    eq(customTitle),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE),
                    eq(null),
                    any(Object[].class),
                    any()
            )).thenReturn(JOptionPane.YES_OPTION);

            boolean result = closeHelper.showConfirmationDialog(mockFrame, customMessage, customTitle);

            assertTrue(result);
            mockedOptionPane.verify(() -> JOptionPane.showOptionDialog(
                    eq(mockFrame),
                    eq(customMessage),
                    eq(customTitle),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE),
                    eq(null),
                    any(Object[].class),
                    any()
            ), times(1));
        }
    }

    // showConfirmationDialog в GameWindow (при выборе "Нет")
    @Test
    public void testShowConfirmationDialog_GameWindowCustomText_NoOption() {
        JInternalFrame mockFrame = Mockito.mock(JInternalFrame.class);
        String customMessage = messages.getString("confirmCloseGameWindow");
        String customTitle = messages.getString("confirmCloseTitle");

        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showOptionDialog(
                    eq(mockFrame),
                    eq(customMessage),
                    eq(customTitle),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE),
                    eq(null),
                    any(Object[].class),
                    any()
            )).thenReturn(JOptionPane.NO_OPTION);

            boolean result = closeHelper.showConfirmationDialog(mockFrame, customMessage, customTitle);

            assertFalse(result);
            mockedOptionPane.verify(() -> JOptionPane.showOptionDialog(
                    eq(mockFrame),
                    eq(customMessage),
                    eq(customTitle),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE),
                    eq(null),
                    any(Object[].class),
                    any()
            ), times(1));
        }
    }

    // showConfirmationDialog в LogWindow (при выборе "Да")
    @Test
    public void testShowConfirmationDialog_LogWindowCustomText_YesOption() {
        JInternalFrame mockFrame = Mockito.mock(JInternalFrame.class);
        String customMessage = messages.getString("confirmCloseLogWindow");
        String customTitle = messages.getString("confirmCloseTitle");

        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showOptionDialog(
                    eq(mockFrame),
                    eq(customMessage),
                    eq(customTitle),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE),
                    eq(null),
                    any(Object[].class),
                    any()
            )).thenReturn(JOptionPane.YES_OPTION);

            boolean result = closeHelper.showConfirmationDialog(mockFrame, customMessage, customTitle);

            assertTrue(result);
            mockedOptionPane.verify(() -> JOptionPane.showOptionDialog(
                    eq(mockFrame),
                    eq(customMessage),
                    eq(customTitle),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE),
                    eq(null),
                    any(Object[].class),
                    any()
            ), times(1));
        }
    }

    // showConfirmationDialog в LogWindow (при выборе "Нет")
    @Test
    public void testShowConfirmationDialog_LogWindowCustomText_NoOption() {
        JInternalFrame mockFrame = Mockito.mock(JInternalFrame.class);
        String customMessage = messages.getString("confirmCloseLogWindow");
        String customTitle = messages.getString("confirmCloseTitle");

        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showOptionDialog(
                    eq(mockFrame),
                    eq(customMessage),
                    eq(customTitle),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE),
                    eq(null),
                    any(Object[].class),
                    any()
            )).thenReturn(JOptionPane.NO_OPTION);

            boolean result = closeHelper.showConfirmationDialog(mockFrame, customMessage, customTitle);

            assertFalse(result);
            mockedOptionPane.verify(() -> JOptionPane.showOptionDialog(
                    eq(mockFrame),
                    eq(customMessage),
                    eq(customTitle),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE),
                    eq(null),
                    any(Object[].class),
                    any()
            ), times(1));
        }
    }
}