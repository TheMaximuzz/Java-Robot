package robots.util;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import java.util.ResourceBundle;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ConfirmCloseHelperTest {

    @Test
    public void testConfirmClose_YesOption() {
        // Мокаем ResourceBundle
        ResourceBundle mockBundle = Mockito.mock(ResourceBundle.class);
        when(mockBundle.getString("confirmExit")).thenReturn("Вы уверены, что хотите закрыть это окно?");
        when(mockBundle.getString("confirmClose")).thenReturn("Подтверждение закрытия");

        // Мокаем JOptionPane
        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showConfirmDialog(
                    any(JComponent.class), // Любой JComponent
                    eq("Вы уверены, что хотите закрыть это окно?"), // Сообщение
                    eq("Подтверждение закрытия"), // Заголовок
                    eq(JOptionPane.YES_NO_OPTION) // Тип опций
            )).thenReturn(JOptionPane.YES_OPTION); // Пользователь выбирает "Да"

            // Создаём ConfirmCloseHelper
            ConfirmCloseHelper closeHelper = new ConfirmCloseHelper(mockBundle);

            // Вызываем метод confirmClose
            boolean result = closeHelper.confirmClose(new JPanel());

            // Проверяем, что метод вернул true
            assertTrue(result);
        }
    }

    @Test
    public void testConfirmClose_NoOption() {
        // Мокаем ResourceBundle
        ResourceBundle mockBundle = Mockito.mock(ResourceBundle.class);
        when(mockBundle.getString("confirmExit")).thenReturn("Вы уверены, что хотите закрыть это окно?");
        when(mockBundle.getString("confirmClose")).thenReturn("Подтверждение закрытия");

        // Мокаем JOptionPane
        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showConfirmDialog(
                    any(JComponent.class), // Любой JComponent
                    eq("Вы уверены, что хотите закрыть это окно?"), // Сообщение
                    eq("Подтверждение закрытия"), // Заголовок
                    eq(JOptionPane.YES_NO_OPTION) // Тип опций
            )).thenReturn(JOptionPane.NO_OPTION); // Пользователь выбирает "Нет"

            // Создаём ConfirmCloseHelper
            ConfirmCloseHelper closeHelper = new ConfirmCloseHelper(mockBundle);

            // Вызываем метод confirmClose
            boolean result = closeHelper.confirmClose(new JPanel());

            // Проверяем, что метод вернул false
            assertFalse(result);
        }
    }
}