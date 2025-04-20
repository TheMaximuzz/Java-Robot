package robots.gui;

import robots.log.Logger;
import robots.profile.Profile;
import robots.profile.ProfileManager;
import robots.util.LocaleManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainApplicationFrame extends BaseFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private ApplicationMenuBar menuBar;
    private LogWindow logWindow;
    private GameWindow gameWindow;
    private String currentProfileName;

    public MainApplicationFrame(String profileName) {
        super();
        this.currentProfileName = profileName;
        Logger.appStarted();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int inset = 50;
        int logWidth = 300;
        int blockSize = 32;
        int mazeWidth = 28 * blockSize;
        int mazeHeight = 31 * blockSize;

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

    @Override
    protected void onExit() {
        exitApplication();
    }

    public void exitApplication() {
        LocaleManager.saveLastLocale(Locale.getDefault());

        List<Profile> profiles = ProfileManager.loadProfiles();
        if (currentProfileName != null) {
            int profileNumber = Integer.parseInt(currentProfileName.split("_")[1]);
            profiles.removeIf(p -> p.getProfileNumber() == profileNumber);
            Profile updatedProfile = new Profile(profileNumber, this, logWindow, gameWindow, Locale.getDefault());
            profiles.add(updatedProfile);
        } else {
            int newProfileNumber = profiles.stream()
                    .mapToInt(Profile::getProfileNumber)
                    .max()
                    .orElse(0) + 1;
            Profile newProfile = new Profile(newProfileNumber, this, logWindow, gameWindow, Locale.getDefault());
            profiles.add(newProfile);
        }
        ProfileManager.saveProfiles(profiles);
        System.exit(0);
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

    public LogWindow getLogWindow() {
        return logWindow;
    }

    public GameWindow getGameWindow() {
        return gameWindow;
    }
}