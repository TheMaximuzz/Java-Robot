package robots.gui;

import robots.profile.*;
import robots.util.LocaleManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class RobotsProgram {
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
    } catch (Exception e) {
      e.printStackTrace();
    }

    Locale selectedLocale = LocaleManager.loadLastLocale();
    Locale.setDefault(selectedLocale);

    List<Profile> profiles = ProfileManager.loadProfiles();
    Profile selectedProfile = null;
    String selectedProfileName = null;
    boolean startGame = false;

    if (!profiles.isEmpty()) {
      while (!startGame) {
        RestoreConfirmationDialog restoreDialog = new RestoreConfirmationDialog(selectedLocale);
        restoreDialog.setVisible(true);

        if (restoreDialog.isRestoreSelected()) {
          ProfileSelectionDialog profileDialog = new ProfileSelectionDialog(profiles, selectedLocale);
          profileDialog.setVisible(true);

          if (profileDialog.isPlaySelected()) {
            selectedProfileName = profileDialog.getSelectedProfileName();
            String profileNamePrefix = ResourceBundle.getBundle("messages", selectedLocale).getString("profileName");
            int profileNumber = Integer.parseInt(selectedProfileName.replace(profileNamePrefix + "_", ""));
            selectedProfile = profiles.stream()
                    .filter(p -> p.getProfileNumber() == profileNumber)
                    .findFirst()
                    .orElse(null);
            selectedProfileName = profileNamePrefix + "_" + profileNumber;
            startGame = true;
          } else if (profileDialog.isBackSelected()) {
            continue;
          } else {
            System.exit(0);
          }
        } else {
          PlayWithoutSavingDialog playWithoutSavingDialog = new PlayWithoutSavingDialog(selectedLocale);
          playWithoutSavingDialog.setVisible(true);
          if (playWithoutSavingDialog.isPlayWithoutSaving()) {
            startGame = true;
          } else {
            continue;
          }
        }
      }
    } else {
      startGame = true;
    }

    final Profile finalSelectedProfile = selectedProfile;
    final String finalProfileName = selectedProfileName;
    SwingUtilities.invokeLater(() -> {
      MainApplicationFrame frame = new MainApplicationFrame(finalProfileName);

      if (finalSelectedProfile != null) {
        int mainWidth = finalSelectedProfile.getMainWidth() > 0 ? finalSelectedProfile.getMainWidth() : 1936;
        int mainHeight = finalSelectedProfile.getMainHeight() > 0 ? finalSelectedProfile.getMainHeight() : 1056;
        frame.setBounds(finalSelectedProfile.getMainX(), finalSelectedProfile.getMainY(), mainWidth, mainHeight);

        int state = Frame.NORMAL;
        if (finalSelectedProfile.isMainMaximized()) {
          state |= Frame.MAXIMIZED_BOTH;
        }
        if (finalSelectedProfile.isMainIconified()) {
          state |= Frame.ICONIFIED;
        }
        frame.setExtendedState(state);


        LogWindow logWindow = frame.getLogWindow();
        int logX = finalSelectedProfile.getLogNormalX();
        int logY = finalSelectedProfile.getLogNormalY();
        int logWidth = finalSelectedProfile.getLogNormalWidth() > 0 ? finalSelectedProfile.getLogNormalWidth() : 300;
        int logHeight = finalSelectedProfile.getLogNormalHeight() > 0 ? finalSelectedProfile.getLogNormalHeight() : 200;
        logWindow.setNormalBounds(new Rectangle(logX, logY, logWidth, logHeight));
        logWindow.setBounds(logX, logY, logWidth, logHeight);
        logWindow.setVisible(finalSelectedProfile.isLogVisible());

        GameWindow gameWindow = frame.getGameWindow();
        int gameX = finalSelectedProfile.getGameNormalX();
        int gameY = finalSelectedProfile.getGameNormalY();
        int gameWidth = finalSelectedProfile.getGameNormalWidth() > 0 ? finalSelectedProfile.getGameNormalWidth() : 896;
        int gameHeight = finalSelectedProfile.getGameNormalHeight() > 0 ? finalSelectedProfile.getGameNormalHeight() : 672;
        gameWindow.setNormalBounds(new Rectangle(gameX, gameY, gameWidth, gameHeight));
        gameWindow.setBounds(gameX, gameY, gameWidth, gameHeight);
        gameWindow.setVisible(finalSelectedProfile.isGameVisible());

        SwingUtilities.invokeLater(() -> {
          try {
            if (finalSelectedProfile.isGameMaximized() && !finalSelectedProfile.isGameIconified()) {
              gameWindow.setMaximized(true);
              int maxX = finalSelectedProfile.getGameMaximizedX();
              int maxY = finalSelectedProfile.getGameMaximizedY();
              int maxWidth = finalSelectedProfile.getGameMaximizedWidth() > 0 ? finalSelectedProfile.getGameMaximizedWidth() : 1920;
              int maxHeight = finalSelectedProfile.getGameMaximizedHeight() > 0 ? finalSelectedProfile.getGameMaximizedHeight() : 1080;
              gameWindow.setBounds(maxX, maxY, maxWidth, maxHeight);
            }
            gameWindow.setIconified(finalSelectedProfile.isGameIconified());
          } catch (Exception e) {
            System.err.println(e.getMessage());
          }

          try {
            if (finalSelectedProfile.isLogMaximized() && !finalSelectedProfile.isLogIconified()) {
              logWindow.setMaximized(true);
              int maxX = finalSelectedProfile.getLogMaximizedX();
              int maxY = finalSelectedProfile.getLogMaximizedY();
              int maxWidth = finalSelectedProfile.getLogMaximizedWidth() > 0 ? finalSelectedProfile.getLogMaximizedWidth() : 1920;
              int maxHeight = finalSelectedProfile.getLogMaximizedHeight() > 0 ? finalSelectedProfile.getLogMaximizedHeight() : 1080;
              logWindow.setBounds(maxX, maxY, maxWidth, maxHeight);
            }
            logWindow.setIconified(finalSelectedProfile.isLogIconified());
          } catch (Exception e) {
            System.err.println(e.getMessage());
          }
        });
      } else {
        frame.pack();
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
      }
      frame.setVisible(true);
    });
  }
}