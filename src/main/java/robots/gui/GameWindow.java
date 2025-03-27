package robots.gui;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends BaseInternalFrame {
    private final GameVisualizer m_visualizer;

    public GameWindow() {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    protected boolean confirmClose() {
        return closeHelper.showConfirmationDialog(this, "Вы действительно хотите закрыть окно игры?", "Подтверждение закрытия");
    }
}