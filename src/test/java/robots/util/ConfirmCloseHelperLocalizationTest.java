package robots.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ConfirmCloseHelperLocalizationTest {

    private ConfirmCloseHelper closeHelper;
    private ResourceBundle mockBundleEn;
    private ResourceBundle mockBundleRu;
    private JInternalFrame mockFrame;

    @BeforeEach
    public void setUp() {
        // Мокируем ResourceBundle для английского языка
        mockBundleEn = Mockito.mock(ResourceBundle.class);
        when(mockBundleEn.getString("confirmCloseLogWindow")).thenReturn("Are you sure you want to close the log window?");
        when(mockBundleEn.getString("confirmCloseTitle")).thenReturn("Close Confirmation");
        when(mockBundleEn.getString("yesButtonText")).thenReturn("Yes");
        when(mockBundleEn.getString("noButtonText")).thenReturn("No");

        // Мокируем ResourceBundle для русского языка
        mockBundleRu = Mockito.mock(ResourceBundle.class);
        when(mockBundleRu.getString("confirmCloseLogWindow")).thenReturn("Вы действительно хотите закрыть окно логов?");
        when(mockBundleRu.getString("confirmCloseTitle")).thenReturn("Подтверждение закрытия");
        when(mockBundleRu.getString("yesButtonText")).thenReturn("Да");
        when(mockBundleRu.getString("noButtonText")).thenReturn("Нет");

        // Мокируем ResourceBundle.getBundle()
        try (MockedStatic<ResourceBundle> mockedResourceBundle = mockStatic(ResourceBundle.class)) {
            mockedResourceBundle.when(() -> ResourceBundle.getBundle("messages")).thenReturn(mockBundleEn);
            closeHelper = new ConfirmCloseHelper();
        }

        mockFrame = Mockito.mock(JInternalFrame.class);
    }

    @Test
    public void testDialogLocalization_English() {
        try (MockedStatic<JOptionPane> mockedOptionPane = mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showOptionDialog(
                    eq(mockFrame),
                    eq("Are you sure you want to close the log window?"),
                    eq("Close Confirmation"),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE),
                    eq(null),
                    eq(new Object[]{"Yes", "No"}),
                    eq("No")
            )).thenReturn(JOptionPane.YES_OPTION);

            boolean result = closeHelper.showConfirmationDialog(mockFrame, "Are you sure you want to close the log window?", "Close Confirmation");

            assertTrue(result);
            mockedOptionPane.verify(() -> JOptionPane.showOptionDialog(
                    eq(mockFrame),
                    eq("Are you sure you want to close the log window?"),
                    eq("Close Confirmation"),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE),
                    eq(null),
                    eq(new Object[]{"Yes", "No"}),
                    eq("No")
            ), times(1));
        }
    }

    @Test
    public void testDialogLocalization_SwitchToRussian() {
        // Переключаем язык на русский
        closeHelper.updateLanguage(mockBundleRu);

        try (MockedStatic<JOptionPane> mockedOptionPane = mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showOptionDialog(
                    eq(mockFrame),
                    eq("Вы действительно хотите закрыть окно логов?"),
                    eq("Подтверждение закрытия"),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE),
                    eq(null),
                    eq(new Object[]{"Да", "Нет"}),
                    eq("Нет")
            )).thenReturn(JOptionPane.YES_OPTION);

            boolean result = closeHelper.showConfirmationDialog(mockFrame, "Вы действительно хотите закрыть окно логов?", "Подтверждение закрытия");

            assertTrue(result);
            mockedOptionPane.verify(() -> JOptionPane.showOptionDialog(
                    eq(mockFrame),
                    eq("Вы действительно хотите закрыть окно логов?"),
                    eq("Подтверждение закрытия"),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE),
                    eq(null),
                    eq(new Object[]{"Да", "Нет"}),
                    eq("Нет")
            ), times(1));
        }
    }

    @Test
    public void testDialogLocalization_SwitchBackToEnglish() {
        // Переключаем на русский, затем обратно на английский
        closeHelper.updateLanguage(mockBundleRu);
        closeHelper.updateLanguage(mockBundleEn);

        try (MockedStatic<JOptionPane> mockedOptionPane = mockStatic(JOptionPane.class)) {
            mockedOptionPane.when(() -> JOptionPane.showOptionDialog(
                    eq(mockFrame),
                    eq("Are you sure you want to close the log window?"),
                    eq("Close Confirmation"),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE),
                    eq(null),
                    eq(new Object[]{"Yes", "No"}),
                    eq("No")
            )).thenReturn(JOptionPane.YES_OPTION);

            boolean result = closeHelper.showConfirmationDialog(mockFrame, "Are you sure you want to close the log window?", "Close Confirmation");

            assertTrue(result);
            mockedOptionPane.verify(() -> JOptionPane.showOptionDialog(
                    eq(mockFrame),
                    eq("Are you sure you want to close the log window?"),
                    eq("Close Confirmation"),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE),
                    eq(null),
                    eq(new Object[]{"Yes", "No"}),
                    eq("No")
            ), times(1));
        }
    }
}