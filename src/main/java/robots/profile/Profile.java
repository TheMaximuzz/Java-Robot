package robots.profile;

import java.awt.*;
import java.io.Serializable;
import java.util.Locale;
import robots.gui.MainApplicationFrame;
import robots.gui.LogWindow;
import robots.gui.GameWindow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.List;

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

    @JsonProperty("logX")
    private int logX;

    @JsonProperty("logY")
    private int logY;

    @JsonProperty("logWidth")
    private int logWidth;

    @JsonProperty("logHeight")
    private int logHeight;

    @JsonProperty("logVisible")
    private boolean logVisible;

    @JsonProperty("logMaximized")
    private boolean logMaximized;

    @JsonProperty("logIconified")
    private boolean logIconified;

    @JsonProperty("gameX")
    private int gameX;

    @JsonProperty("gameY")
    private int gameY;

    @JsonProperty("gameWidth")
    private int gameWidth;

    @JsonProperty("gameHeight")
    private int gameHeight;

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

    @JsonIgnore
    private Locale locale;

    @JsonProperty("locale")
    private String localeString;

    public Profile() {
        this.mobs = new ArrayList<>();
    }

    public Profile(int profileNumber, MainApplicationFrame mainFrame, LogWindow logWindow, GameWindow gameWindow, Locale locale) {
        this.profileNumber = profileNumber;
        this.mainX = mainFrame.getX();
        this.mainY = mainFrame.getY();
        this.mainWidth = mainFrame.getWidth();
        this.mainHeight = mainFrame.getHeight();
        this.mainMaximized = (mainFrame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
        this.logX = logWindow.getX();
        this.logY = logWindow.getY();
        this.logWidth = logWindow.getWidth();
        this.logHeight = logWindow.getHeight();
        this.logVisible = logWindow.isVisible();
        this.logMaximized = logWindow.isMaximum();
        this.logIconified = logWindow.isIcon();
        this.gameX = gameWindow.getX();
        this.gameY = gameWindow.getY();
        this.gameWidth = gameWindow.getWidth();
        this.gameHeight = gameWindow.getHeight();
        this.gameVisible = gameWindow.isVisible();
        this.gameMaximized = gameWindow.isMaximum();
        this.gameIconified = gameWindow.isIcon();
        this.playerX = gameWindow.getPlayerX();
        this.playerY = gameWindow.getPlayerY();
        this.mobs = gameWindow.getMobPositions();
        this.locale = locale;
        this.localeString = locale.getLanguage() + "_" + locale.getCountry();
    }

    public int getProfileNumber() {
        return profileNumber;
    }

    public String getProfileName(Locale locale) {
        ResourceBundle messages = ResourceBundle.getBundle("messages", locale);
        return messages.getString("profileName") + "_" + profileNumber;
    }

    public int getMainX() {
        return mainX;
    }

    public int getMainY() {
        return mainY;
    }

    public int getMainWidth() {
        return mainWidth;
    }

    public int getMainHeight() {
        return mainHeight;
    }

    public boolean isMainMaximized() {
        return mainMaximized;
    }

    public int getLogX() {
        return logX;
    }

    public int getLogY() {
        return logY;
    }

    public int getLogWidth() {
        return logWidth;
    }

    public int getLogHeight() {
        return logHeight;
    }

    public boolean isLogVisible() {
        return logVisible;
    }

    public boolean isLogMaximized() {
        return logMaximized;
    }

    public boolean isLogIconified() {
        return logIconified;
    }

    public int getGameX() {
        return gameX;
    }

    public int getGameY() {
        return gameY;
    }

    public int getGameWidth() {
        return gameWidth;
    }

    public int getGameHeight() {
        return gameHeight;
    }

    public boolean isGameVisible() {
        return gameVisible;
    }

    public boolean isGameMaximized() {
        return gameMaximized;
    }

    public boolean isGameIconified() {
        return gameIconified;
    }

    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public List<MobPosition> getMobPositions() {
        return mobs;
    }

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

        public MobPosition() {
        }

        public MobPosition(int mobX, int mobY) {
            this.mobX = mobX;
            this.mobY = mobY;
        }

        public int getMobX() {
            return mobX;
        }

        public int getMobY() {
            return mobY;
        }

        @Override
        public String toString() {
            return "(" + mobX + ", " + mobY + ")";
        }
    }
}