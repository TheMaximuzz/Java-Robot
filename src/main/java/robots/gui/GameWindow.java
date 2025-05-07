package robots.gui;

import robots.base.BaseInternalFrame;
import robots.profile.Profile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class GameWindow extends BaseInternalFrame {
    private final GameVisualizer m_visualizer;

    public GameWindow(Locale locale) {
        super(true, true, true, true);
        m_visualizer = new GameVisualizer(locale);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        setResizable(true);
    }

    @Override
    protected boolean confirmClose() {
        return closeHelper.showConfirmationDialog(this,
                messages.getString("confirmCloseGameWindow"),
                messages.getString("confirmCloseTitle"));
    }

    @Override
    protected String getTitleKey() {
        return "gameWindowTitle";
    }

    @Override
    public void updateLanguage(ResourceBundle newMessages) {
        super.updateLanguage(newMessages);
        m_visualizer.updateLanguage(newMessages);
    }

    public int getPlayerX() {
        return m_visualizer.getRobotGridX();
    }

    public int getPlayerY() {
        return m_visualizer.getRobotGridY();
    }

    public List<Profile.MobPosition> getMobPositions() {
        List<Profile.MobPosition> positions = new ArrayList<>();
        for (Enemy enemy : m_visualizer.getEnemies()) {
            positions.add(new Profile.MobPosition(enemy.getGridX(), enemy.getGridY()));
        }
        return positions;
    }

    public void setPlayerPosition(int x, int y) {
        m_visualizer.setRobotPosition(x, y);
    }

    public void setMobPositions(List<Profile.MobPosition> positions) {
        m_visualizer.setEnemyPositions(positions);
    }
}