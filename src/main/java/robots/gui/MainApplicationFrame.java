package robots.gui;

import robots.base.BaseFrame;
import robots.log.Logger;
import robots.profile.Profile;
import robots.profile.ProfileManager;
import robots.util.LocaleManager;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
    private Profile currentProfile;

    public MainApplicationFrame(String profileName) {
        super();
        this.currentProfileName = profileName;
        Logger.appStarted();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int inset = 50;
        int logWidth = 300;
        int blockSize = 32;
        int mazeWidth = 28 * blockSize;
        int mazeHeight = 32 * blockSize;

        int frameWidth = mazeWidth + logWidth + inset * 2;
        int frameHeight = Math.max(mazeHeight, 800) + inset * 2;
        setBounds(inset, inset, Math.min(frameWidth, screenSize.width - inset * 2),
                Math.min(frameHeight, screenSize.height - inset * 2));

        setContentPane(desktopPane);

        logWindow = createLogWindow();
        logWindow.setLocation(0, 0);
        logWindow.setSize(logWidth, getHeight() - getInsets().top - getInsets().bottom);
        logWindow.setNormalBounds(new Rectangle(0, 0, logWidth, getHeight() - getInsets().top - getInsets().bottom));
        logWindow.setVisible(false);
        addWindow(logWindow);

        logWindow.addPropertyChangeListener("maximum", evt -> {
            if (Boolean.TRUE.equals(evt.getNewValue())) {
                logWindow.moveToFront();
                desktopPane.setLayer(logWindow, JDesktopPane.DEFAULT_LAYER + 1);
                desktopPane.setLayer(gameWindow, JDesktopPane.DEFAULT_LAYER);
                updateProfile();
            }
        });

        gameWindow = new GameWindow(Locale.getDefault());
        gameWindow.setLocation(logWidth, 0);
        gameWindow.setSize(mazeWidth, mazeHeight);
        gameWindow.setNormalBounds(new Rectangle(logWidth, 0, mazeWidth, mazeHeight));
        gameWindow.setVisible(false);
        addWindow(gameWindow);

        gameWindow.addPropertyChangeListener("maximum", evt -> {
            if (Boolean.TRUE.equals(evt.getNewValue())) {
                gameWindow.moveToFront();
                desktopPane.setLayer(gameWindow, JDesktopPane.DEFAULT_LAYER + 1);
                desktopPane.setLayer(logWindow, JDesktopPane.DEFAULT_LAYER);
                updateProfile();
            }
        });

        menuBar = new ApplicationMenuBar(this);
        setJMenuBar(menuBar);

        if (profileName != null) {
            List<Profile> profiles = ProfileManager.loadProfiles();
            int profileNumber = Integer.parseInt(profileName.split("_")[1]);
            currentProfile = profiles.stream()
                    .filter(p -> p.getProfileNumber() == profileNumber)
                    .findFirst()
                    .orElse(null);
            if (currentProfile != null) {
                applyProfile();
            }
        }

        SwingUtilities.invokeLater(() -> {
            desktopPane.revalidate();
            desktopPane.repaint();
            logWindow.setVisible(currentProfile != null ? currentProfile.isLogVisible() : true);
            gameWindow.setVisible(currentProfile != null ? currentProfile.isGameVisible() : true);
        });

        logWindow.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (!logWindow.isMaximum() && !logWindow.isIcon() && logWindow.getWidth() > 0 && logWindow.getHeight() > 0) {
                    logWindow.setNormalBounds(new Rectangle(logWindow.getX(), logWindow.getY(), logWindow.getWidth(), logWindow.getHeight()));
                    updateProfile();
                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                if (!logWindow.isMaximum() && !logWindow.isIcon() && logWindow.getWidth() > 0 && logWindow.getHeight() > 0) {
                    logWindow.setNormalBounds(new Rectangle(logWindow.getX(), logWindow.getY(), logWindow.getWidth(), logWindow.getHeight()));
                    updateProfile();
                }
            }
        });

        logWindow.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            @Override
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent e) {
                updateProfile();
            }

            @Override
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent e) {
                if (currentProfile != null && currentProfile.shouldLogMaximizeOnDeiconify()) {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            logWindow.restoreMaximizedState(
                                    currentProfile.getLogMaximizedX(),
                                    currentProfile.getLogMaximizedY(),
                                    currentProfile.getLogMaximizedWidth(),
                                    currentProfile.getLogMaximizedHeight()
                            );
                        } catch (Exception ex) {
                            System.err.println("LogWindow - Ошибка: " + ex.getMessage());
                        }
                    });
                }
                updateProfile();
            }

            @Override
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent e) {
                if (logWindow.isMaximum()) {
                    updateProfile();
                }
            }

            @Override
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent e) {
                // Убрали updateProfile
            }
        });

        gameWindow.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (!gameWindow.isMaximum() && !gameWindow.isIcon() && gameWindow.getWidth() > 0 && gameWindow.getHeight() > 0) {
                    gameWindow.setNormalBounds(new Rectangle(gameWindow.getX(), gameWindow.getY(), gameWindow.getWidth(), gameWindow.getHeight()));
                    updateProfile();
                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                if (!gameWindow.isMaximum() && !gameWindow.isIcon() && gameWindow.getWidth() > 0 && gameWindow.getHeight() > 0) {
                    gameWindow.setNormalBounds(new Rectangle(gameWindow.getX(), gameWindow.getY(), gameWindow.getWidth(), gameWindow.getHeight()));
                    updateProfile();
                }
            }
        });

        gameWindow.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            @Override
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent e) {
                updateProfile();
            }

            @Override
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent e) {
                if (currentProfile != null && currentProfile.shouldGameMaximizeOnDeiconify()) {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            gameWindow.restoreMaximizedState(
                                    currentProfile.getGameMaximizedX(),
                                    currentProfile.getGameMaximizedY(),
                                    currentProfile.getGameMaximizedWidth(),
                                    currentProfile.getGameMaximizedHeight()
                            );
                        } catch (Exception ex) {
                            System.err.println("GameWindow - Ошибка: " + ex.getMessage());
                        }
                    });
                }
                updateProfile();
            }

            @Override
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent e) {
                if (gameWindow.isMaximum()) {
                    updateProfile();
                }
            }

            @Override
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent e) {
                // Убрали updateProfile
            }
        });
    }

    private void applyProfile() {
        if (currentProfile == null) return;
        boolean logInFront = "log".equals(currentProfile.getFrontWindow());
        try {
            // Установка LogWindow
            logWindow.setNormalBounds(new Rectangle(
                    currentProfile.getLogNormalX(),
                    currentProfile.getLogNormalY(),
                    currentProfile.getLogNormalWidth(),
                    currentProfile.getLogNormalHeight()
            ));
            if (!currentProfile.isLogIconified()) {
                if (currentProfile.isLogMaximized()) {
                    logWindow.restoreMaximizedState(
                            currentProfile.getLogMaximizedX(),
                            currentProfile.getLogMaximizedY(),
                            currentProfile.getLogMaximizedWidth(),
                            currentProfile.getLogMaximizedHeight()
                    );
                } else {
                    logWindow.setMaximized(false);
                    logWindow.setBounds(
                            currentProfile.getLogNormalX(),
                            currentProfile.getLogNormalY(),
                            currentProfile.getLogNormalWidth(),
                            currentProfile.getLogNormalHeight()
                    );
                }
            }

            logWindow.setVisible(currentProfile.isLogVisible());
            logWindow.setIconified(currentProfile.isLogIconified());
        } catch (Exception e) {
            System.err.println("LogWindow - Ошибка в applyProfile(): " + e.getMessage());
        }

        try {
            gameWindow.setNormalBounds(new Rectangle(
                    currentProfile.getGameNormalX(),
                    currentProfile.getGameNormalY(),
                    currentProfile.getGameNormalWidth(),
                    currentProfile.getGameNormalHeight()
            ));
            if (!currentProfile.isGameIconified()) {
                if (currentProfile.isGameMaximized()) {
                    gameWindow.restoreMaximizedState(
                            currentProfile.getGameMaximizedX(),
                            currentProfile.getGameMaximizedY(),
                            currentProfile.getGameMaximizedWidth(),
                            currentProfile.getGameMaximizedHeight()
                    );
                } else {
                    gameWindow.setMaximized(false);
                    gameWindow.setBounds(
                            currentProfile.getGameNormalX(),
                            currentProfile.getGameNormalY(),
                            currentProfile.getGameNormalWidth(),
                            currentProfile.getGameNormalHeight()
                    );
                }
            }

            gameWindow.setVisible(currentProfile.isGameVisible());
            gameWindow.setIconified(currentProfile.isGameIconified());
        } catch (Exception e) {
            System.err.println("GameWindow - Ошибка в applyProfile(): " + e.getMessage());
        }

        if (logInFront) {
            desktopPane.setLayer(logWindow, JDesktopPane.DEFAULT_LAYER + 1);
            desktopPane.setLayer(gameWindow, JDesktopPane.DEFAULT_LAYER);
        } else {
            desktopPane.setLayer(gameWindow, JDesktopPane.DEFAULT_LAYER + 1);
            desktopPane.setLayer(logWindow, JDesktopPane.DEFAULT_LAYER);
        }

        gameWindow.setPlayerPosition(currentProfile.getPlayerX(), currentProfile.getPlayerY());
        gameWindow.setMobPositions(currentProfile.getMobPositions());
    }

    private void updateProfile() {
        if (currentProfile != null) {
            currentProfile.updateFromWindows(this, logWindow, gameWindow, Locale.getDefault());
        }
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        Logger.logWorking();
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
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
            currentProfile = new Profile(profileNumber, this, logWindow, gameWindow, Locale.getDefault());
            profiles.add(currentProfile);
        } else {
            int newProfileNumber = profiles.stream()
                    .mapToInt(Profile::getProfileNumber)
                    .max()
                    .orElse(0) + 1;
            currentProfile = new Profile(newProfileNumber, this, logWindow, gameWindow, Locale.getDefault());
            profiles.add(currentProfile);
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
        logWindow.updateLanguageIfVisible(messages);
        gameWindow.updateLanguageIfVisible(messages);
    }

    public LogWindow getLogWindow() {
        return logWindow;
    }

    public GameWindow getGameWindow() {
        return gameWindow;
    }
}