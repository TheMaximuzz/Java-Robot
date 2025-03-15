package robots.util;

import javax.swing.JOptionPane;

public class ConfirmCloseHelper {

    public static boolean confirmClose() {
        int result = JOptionPane.showConfirmDialog(
                null,
                "Вы уверены, что хотите закрыть это окно?", // сообщение
                "Подтверждение закрытия", // заголовок окна
                JOptionPane.YES_NO_OPTION
        );

        return result == JOptionPane.YES_OPTION;
    }
}