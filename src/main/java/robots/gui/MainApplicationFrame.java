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

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int inset = 50;
        int logWidth = 300;
        int blockSize = 32;
        int mazeWidth = 28 * blockSize; // 896
        int mazeHeight = 31 * blockSize; // 992

        // Устанавливаем размеры главного окна
        int frameWidth = mazeWidth + logWidth + inset * 2;
        int frameHeight = Math.max(mazeHeight, 800) + inset * 2;
        setBounds(inset, inset, Math.min(frameWidth, screenSize.width - inset * 2),
                Math.min(frameHeight, screenSize.height - inset * 2));

        setContentPane(desktopPane);

        logWindow = createLogWindow();
        logWindow.setLocation(0, 0);
        logWindow.setSize(logWidth, getHeight() - getInsets().top - getInsets().bottom);
        addWindow(logWindow);

        gameWindow = new GameWindow();
        gameWindow.setLocation(logWidth, 0);
        gameWindow.setSize(mazeWidth, mazeHeight);
        addWindow(gameWindow);

        menuBar = new ApplicationMenuBar(this);
        setJMenuBar(menuBar);
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
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
        return "mainWindowTitle";
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