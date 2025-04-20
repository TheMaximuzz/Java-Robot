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

        // если восстанавливаем профиль
        if (restoreDialog.isRestoreSelected()) {
          ProfileSelectionDialog profileDialog = new ProfileSelectionDialog(profiles, selectedLocale);
          profileDialog.setVisible(true);

          // "играть"
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
      // если профилей нет, мы сразу запускаем игру, соответственно впоследствии они появятся
      startGame = true;
    }

    // запуск игры
    final Profile finalSelectedProfile = selectedProfile;
    final String finalProfileName = selectedProfileName;
    if (startGame) {
      SwingUtilities.invokeLater(() -> {
        MainApplicationFrame frame = new MainApplicationFrame(finalProfileName);

        // если выбрали профиль
        if (finalSelectedProfile != null) {
          frame.setBounds(finalSelectedProfile.getMainX(), finalSelectedProfile.getMainY(),
                  finalSelectedProfile.getMainWidth(), finalSelectedProfile.getMainHeight());
          if (finalSelectedProfile.isMainMaximized()) {
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
          }
          frame.getLogWindow().setLocation(finalSelectedProfile.getLogX(), finalSelectedProfile.getLogY());
          frame.getLogWindow().setSize(finalSelectedProfile.getLogWidth(), finalSelectedProfile.getLogHeight());
          frame.getLogWindow().setVisible(finalSelectedProfile.isLogVisible());
          try {
            frame.getLogWindow().setMaximum(finalSelectedProfile.isLogMaximized());
            frame.getLogWindow().setIcon(finalSelectedProfile.isLogIconified());
          } catch (Exception e) {
            e.printStackTrace();
          }
          frame.getGameWindow().setLocation(finalSelectedProfile.getGameX(), finalSelectedProfile.getGameY());
          frame.getGameWindow().setSize(finalSelectedProfile.getGameWidth(), finalSelectedProfile.getGameHeight());
          frame.getGameWindow().setVisible(finalSelectedProfile.isGameVisible());
          try {
            frame.getGameWindow().setMaximum(finalSelectedProfile.isGameMaximized());
            frame.getGameWindow().setIcon(finalSelectedProfile.isGameIconified());
          } catch (Exception e) {
            e.printStackTrace();
          }
          frame.getGameWindow().setPlayerPosition(finalSelectedProfile.getPlayerX(), finalSelectedProfile.getPlayerY());
          frame.getGameWindow().setMobPositions(finalSelectedProfile.getMobPositions());
        } else {
          frame.pack();
          frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        }
        frame.setVisible(true);
      });
    }
  }
}