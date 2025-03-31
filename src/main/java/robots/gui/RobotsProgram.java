package robots.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class RobotsProgram {
  public static void main(String[] args) {
    try {
      // Устанавливаем стиль оформления
      UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Устанавливаем локаль по умолчанию (язык системы)
    Locale.setDefault(Locale.getDefault());

    SwingUtilities.invokeLater(() -> {
      MainApplicationFrame frame = new MainApplicationFrame();
      frame.pack();
      frame.setVisible(true);
      frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    });
  }
}