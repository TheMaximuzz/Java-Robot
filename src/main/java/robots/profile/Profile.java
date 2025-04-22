package robots.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import robots.gui.MainApplicationFrame;
import robots.gui.LogWindow;
import robots.gui.GameWindow;

import javax.swing.*;
import java.awt.Frame;
import java.awt.Rectangle;

public class Profile implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("profileNumber")
    private int profileNumber;

    @JsonProperty("mainX")
    private int mainX;

    @JsonProperty("mainY")
    private int mainY;

    @JsonProperty("mainWidth")
    private int mainWidth;

    @JsonProperty("mainHeight")
    private int mainHeight;

    @JsonProperty("mainMaximized")
    private boolean mainMaximized;

    @JsonProperty("mainIconified")
    private boolean mainIconified;

    @JsonProperty("logNormalX")
    private int logNormalX;

    @JsonProperty("logNormalY")
    private int logNormalY;

    @JsonProperty("logNormalWidth")
    private int logNormalWidth;

    @JsonProperty("logNormalHeight")
    private int logNormalHeight;

    @JsonProperty("logMaximizedX")
    private int logMaximizedX;

    @JsonProperty("logMaximizedY")
    private int logMaximizedY;

    @JsonProperty("logMaximizedWidth")
    private int logMaximizedWidth;

    @JsonProperty("logMaximizedHeight")
    private int logMaximizedHeight;

    @JsonProperty("logVisible")
    private boolean logVisible;

    @JsonProperty("logMaximized")
    private boolean logMaximized;

    @JsonProperty("logIconified")
    private boolean logIconified;

    @JsonProperty("gameNormalX")
    private int gameNormalX;

    @JsonProperty("gameNormalY")
    private int gameNormalY;

    @JsonProperty("gameNormalWidth")
    private int gameNormalWidth;

    @JsonProperty("gameNormalHeight")
    private int gameNormalHeight;

    @JsonProperty("gameMaximizedX")
    private int gameMaximizedX;

    @JsonProperty("gameMaximizedY")
    private int gameMaximizedY;

    @JsonProperty("gameMaximizedWidth")
    private int gameMaximizedWidth;

    @JsonProperty("gameMaximizedHeight")
    private int gameMaximizedHeight;

    @JsonProperty("gameVisible")
    private boolean gameVisible;

    @JsonProperty("gameMaximized")
    private boolean gameMaximized;

    @JsonProperty("gameIconified")
    private boolean gameIconified;

    @JsonProperty("playerX")
    private int playerX;

    @JsonProperty("playerY")
    private int playerY;

    @JsonProperty("mobs")
    private List<MobPosition> mobs;

    @JsonProperty("locale")
    private String localeString;

    @JsonProperty("frontWindow")
    private String frontWindow;

    private transient Locale locale;

    public Profile() {
        this.mobs = new ArrayList<>();
        this.logNormalWidth = 300;
        this.logNormalHeight = 200;
        this.gameNormalWidth = 896;
        this.gameNormalHeight = 672;
        this.logMaximizedWidth = 1920;
        this.logMaximizedHeight = 1080;
        this.gameMaximizedWidth = 1920;
        this.gameMaximizedHeight = 1080;
        this.logVisible = true;
        this.gameVisible = true;
        this.frontWindow = "game";
    }

    public Profile(int profileNumber, MainApplicationFrame mainFrame, LogWindow logWindow, GameWindow gameWindow, Locale locale) {
        this.profileNumber = profileNumber;
        updateFromWindows(mainFrame, logWindow, gameWindow, locale);
    }

    public void updateFromWindows(MainApplicationFrame mainFrame, LogWindow logWindow, GameWindow gameWindow, Locale locale) {

        this.mainX = mainFrame.getX();
        this.mainY = mainFrame.getY();
        this.mainWidth = mainFrame.getWidth() > 0 ? mainFrame.getWidth() : 1936;
        this.mainHeight = mainFrame.getHeight() > 0 ? mainFrame.getHeight() : 1056;
        this.mainMaximized = (mainFrame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
        this.mainIconified = (mainFrame.getExtendedState() & Frame.ICONIFIED) == Frame.ICONIFIED;

        try {
            this.logMaximized = logWindow.isMaximum();
            this.logIconified = logWindow.isIcon();
            this.logVisible = logWindow.isVisible();

            Rectangle logNormalBounds = logWindow.getNormalBounds();
            if (logNormalBounds != null && logNormalBounds.width > 0 && logNormalBounds.height > 0) {
                this.logNormalX = logNormalBounds.x;
                this.logNormalY = logNormalBounds.y;
                this.logNormalWidth = logNormalBounds.width;
                this.logNormalHeight = logNormalBounds.height;
            } else if (!logWindow.isMaximum() && !logWindow.isIcon() && logWindow.getWidth() > 0 && logWindow.getHeight() > 0) {
                this.logNormalX = logWindow.getX();
                this.logNormalY = logWindow.getY();
                this.logNormalWidth = logWindow.getWidth();
                this.logNormalHeight = logWindow.getHeight();
            }

            if (this.logMaximized && logWindow.getWidth() > 0 && logWindow.getHeight() > 0) {
                this.logMaximizedX = logWindow.getX();
                this.logMaximizedY = logWindow.getY();
                this.logMaximizedWidth = logWindow.getWidth();
                this.logMaximizedHeight = logWindow.getHeight();
            }
        } catch (Exception e) {
            this.logMaximized = false;
            this.logIconified = false;
            System.err.println(e.getMessage());
        }

        try {
            this.gameMaximized = gameWindow.isMaximum();
            this.gameIconified = gameWindow.isIcon();
            this.gameVisible = logWindow.isVisible();

            Rectangle gameNormalBounds = gameWindow.getNormalBounds();
            if (gameNormalBounds != null && gameNormalBounds.width > 0 && gameNormalBounds.height > 0) {
                this.gameNormalX = gameNormalBounds.x;
                this.gameNormalY = gameNormalBounds.y;
                this.gameNormalWidth = gameNormalBounds.width;
                this.gameNormalHeight = gameNormalBounds.height;
            } else if (!gameWindow.isMaximum() && !gameWindow.isIcon() && gameWindow.getWidth() > 0 && gameWindow.getHeight() > 0) {
                this.gameNormalX = gameWindow.getX();
                this.gameNormalY = gameWindow.getY();
                this.gameNormalWidth = gameWindow.getWidth();
                this.gameNormalHeight = gameWindow.getHeight();
            }

            if (this.gameMaximized && gameWindow.getWidth() > 0 && gameWindow.getHeight() > 0) {
                this.gameMaximizedX = gameWindow.getX();
                this.gameMaximizedY = gameWindow.getY();
                this.gameMaximizedWidth = gameWindow.getWidth();
                this.gameMaximizedHeight = gameWindow.getHeight();
            }
        } catch (Exception e) {
            this.gameMaximized = false;
            this.gameIconified = false;
            System.err.println(e.getMessage());
        }

        this.playerX = gameWindow.getPlayerX();
        this.playerY = gameWindow.getPlayerY();
        this.mobs = gameWindow.getMobPositions();

        this.locale = locale;
        this.localeString = locale.getLanguage() + "_" + locale.getCountry();

        JDesktopPane desktop = mainFrame.getContentPane() instanceof JDesktopPane ? (JDesktopPane) mainFrame.getContentPane() : null;
        if (desktop != null) {
            JInternalFrame[] frames = desktop.getAllFrames();
            JInternalFrame frontFrame = null;
            for (JInternalFrame frame : frames) {
                if (frame.isVisible() && !frame.isIcon() && (frontFrame == null || frame.getLayer() > frontFrame.getLayer())) {
                    frontFrame = frame;
                }
            }
            if (frontFrame == logWindow) {
                this.frontWindow = "log";
            } else if (frontFrame == gameWindow) {
                this.frontWindow = "game";
            } else {
                this.frontWindow = "game";
            }
        }
    }

    public String getFrontWindow() { return frontWindow; }
    public int getProfileNumber() { return profileNumber; }
    public String getProfileName(Locale locale) {
        ResourceBundle messages = ResourceBundle.getBundle("messages", locale);
        return messages.getString("profileName") + "_" + profileNumber;
    }
    public int getMainX() { return mainX; }
    public int getMainY() { return mainY; }
    public int getMainWidth() { return mainWidth; }
    public int getMainHeight() { return mainHeight; }
    public boolean isMainMaximized() { return mainMaximized; }
    public boolean isMainIconified() { return mainIconified; }
    public int getLogNormalX() { return logNormalX; }
    public int getLogNormalY() { return logNormalY; }
    public int getLogNormalWidth() { return logNormalWidth; }
    public int getLogNormalHeight() { return logNormalHeight; }
    public int getLogMaximizedX() { return logMaximizedX; }
    public int getLogMaximizedY() { return logMaximizedY; }
    public int getLogMaximizedWidth() { return logMaximizedWidth; }
    public int getLogMaximizedHeight() { return logMaximizedHeight; }
    public boolean isLogVisible() { return logVisible; }
    public boolean isLogMaximized() { return logMaximized; }
    public boolean isLogIconified() { return logIconified; }
    public int getGameNormalX() { return gameNormalX; }
    public int getGameNormalY() { return gameNormalY; }
    public int getGameNormalWidth() { return gameNormalWidth; }
    public int getGameNormalHeight() { return gameNormalHeight; }
    public int getGameMaximizedX() { return gameMaximizedX; }
    public int getGameMaximizedY() { return gameMaximizedY; }
    public int getGameMaximizedWidth() { return gameMaximizedWidth; }
    public int getGameMaximizedHeight() { return gameMaximizedHeight; }
    public boolean isGameVisible() { return gameVisible; }
    public boolean isGameMaximized() { return gameMaximized; }
    public boolean isGameIconified() { return gameIconified; }
    public int getPlayerX() { return playerX; }
    public int getPlayerY() { return playerY; }
    public List<MobPosition> getMobPositions() { return mobs; }
    public Locale getLocale() {
        if (locale == null && localeString != null) {
            String[] parts = localeString.split("_");
            locale = parts.length == 2 ? new Locale(parts[0], parts[1]) : new Locale(parts[0]);
        }
        return locale;
    }

    public static class MobPosition {
        @JsonProperty("mobX")
        private int mobX;

        @JsonProperty("mobY")
        private int mobY;

        public MobPosition() {}

        public MobPosition(int mobX, int mobY) {
            this.mobX = mobX;
            this.mobY = mobY;
        }

        public int getMobX() { return mobX; }
        public int getMobY() { return mobY; }

        @Override
        public String toString() {
            return "(" + mobX + ", " + mobY + ")";
        }
    }
}