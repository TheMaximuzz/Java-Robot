package robots.gui;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Locale;
import java.util.ResourceBundle;
import robots.log.Logger;

public class ApplicationMenuBar extends JMenuBar {
    private final MainApplicationFrame parentFrame;
    private ResourceBundle messages;

    public ApplicationMenuBar(MainApplicationFrame parentFrame) {
        this.parentFrame = parentFrame;
        messages = ResourceBundle.getBundle("messages", Locale.getDefault());
        initMenuBar();
    }

    private void initMenuBar() {
        // Меню "Игра"
        JMenu gameMenu = new JMenu(messages.getString("gameMenu"));
        gameMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem exitItem = new JMenuItem(messages.getString("exitItem"));
        exitItem.setMnemonic(KeyEvent.VK_X);
        exitItem.addActionListener((event) -> parentFrame.exitApplication());
        gameMenu.add(exitItem);

        // Меню "Режим отображения"
        JMenu lookAndFeelMenu = new JMenu(messages.getString("lookAndFeelMenu"));
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                messages.getString("lookAndFeelDescription"));

        JMenuItem systemLookAndFeel = new JMenuItem(messages.getString("systemLookAndFeel"), KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> setLookAndFeel(UIManager.getSystemLookAndFeelClassName()));
        lookAndFeelMenu.add(systemLookAndFeel);

        JMenuItem crossplatformLookAndFeel = new JMenuItem(messages.getString("crossPlatformLookAndFeel"), KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()));
        lookAndFeelMenu.add(crossplatformLookAndFeel);

        // Меню "Тесты"
        JMenu testMenu = new JMenu(messages.getString("testMenu"));
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                messages.getString("testMenuDescription"));

        JMenuItem addLogMessageItem = new JMenuItem(messages.getString("addLogMessageItem"), KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> Logger.debug("newLogMessage"));
        testMenu.add(addLogMessageItem);

        // Меню "Язык"
        JMenu languageMenu = new JMenu(messages.getString("languageMenu"));
        languageMenu.setMnemonic(KeyEvent.VK_L);

        JMenuItem russianLanguage = new JMenuItem(messages.getString("russianLanguage"));
        russianLanguage.addActionListener((event) -> parentFrame.changeLanguage(new Locale("ru", "RU")));
        languageMenu.add(russianLanguage);

        JMenuItem englishLanguage = new JMenuItem(messages.getString("englishLanguage"));
        englishLanguage.addActionListener((event) -> parentFrame.changeLanguage(new Locale("en", "US")));
        languageMenu.add(englishLanguage);

        add(lookAndFeelMenu);
        add(testMenu);
        add(gameMenu);
        add(languageMenu);
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
        }
    }

    public void updateLanguage(ResourceBundle newMessages) {
        this.messages = newMessages;
        removeAll();
        initMenuBar();
        revalidate();
        repaint();
    }
}