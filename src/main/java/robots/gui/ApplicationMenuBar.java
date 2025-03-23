package robots.gui;

import javax.swing.*;
import java.awt.event.KeyEvent;
import robots.util.ConfirmCloseHelper;
import robots.log.Logger;

public class ApplicationMenuBar extends JMenuBar {


    private final ConfirmCloseHelper closeHelper = new ConfirmCloseHelper();
    private final JFrame parentFrame;
    public ApplicationMenuBar(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        initMenuBar();
    }

    private void initMenuBar() {

        JMenu gameMenu = new JMenu("Игра");
        gameMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem exitItem = new JMenuItem("Выйти");
        exitItem.setMnemonic(KeyEvent.VK_X);
        exitItem.addActionListener((event) -> exitApplication());
        gameMenu.add(exitItem);

        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> setLookAndFeel(UIManager.getSystemLookAndFeelClassName()));
        lookAndFeelMenu.add(systemLookAndFeel);

        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()));
        lookAndFeelMenu.add(crossplatformLookAndFeel);

        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> Logger.debug("Новая строка"));
        testMenu.add(addLogMessageItem);

        add(lookAndFeelMenu);
        add(testMenu);
        add(gameMenu);
    }

    private void exitApplication() {
        if (closeHelper.confirmClose(parentFrame)) {
            System.exit(0);
        }
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
        }
    }
}