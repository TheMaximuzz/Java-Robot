package robots.gui;

import robots.log.Logger;
import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainApplicationFrame extends BaseFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private ApplicationMenuBar menuBar;
    private LogWindow logWindow;
    private GameWindow gameWindow;

    public MainApplicationFrame() {
        super();
        Logger.appStarted();

        int inset = 100;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);

        setContentPane(desktopPane);

        logWindow = createLogWindow();
        addWindow(logWindow);

        gameWindow = new GameWindow();
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);

        menuBar = new ApplicationMenuBar(this);
        setJMenuBar(menuBar);
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.logWorking();
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    @Override
    protected boolean confirmClose() {
        return closeHelper.confirmClose(this);
    }

    @Override
    protected String getTitleKey() {
        return "mainWindowTitle"; // Ключ для заголовка главного окна
    }

    public void exitApplication() {
        if (confirmClose()) {
            System.exit(0);
        }
    }

    public void changeLanguage(Locale locale) {
        Locale.setDefault(locale);
        messages = ResourceBundle.getBundle("messages", locale);
        Logger.updateLanguage(messages);
        updateUI();
    }

    private void updateUI() {
        super.updateLanguage(messages);
        menuBar.updateLanguage(messages);
        logWindow.updateLanguage(messages);
        gameWindow.updateLanguage(messages);
    }
}