package robots.profile;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlayWithoutSavingDialog extends JDialog {
    private ResourceBundle messages;
    private boolean playWithoutSaving = false;

    public PlayWithoutSavingDialog(Locale locale) {
        super((Frame) null, true);
        messages = ResourceBundle.getBundle("messages", locale);
        initUI();
    }

    private void initUI() {
        setTitle(messages.getString("playWithoutSavingDialogTitle"));
        setLayout(new BorderLayout(10, 10));

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                playWithoutSaving = false;
            }
        });

        JLabel messageLabel = new JLabel(messages.getString("playWithoutSavingDialogMessage"));
        add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton yesButton = new JButton(messages.getString("yesButtonText"));
        yesButton.addActionListener(e -> {
            playWithoutSaving = true;
            dispose();
        });
        JButton noButton = new JButton(messages.getString("noButtonText"));
        noButton.addActionListener(e -> {
            playWithoutSaving = false;
            dispose();
        });
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    public boolean isPlayWithoutSaving() {
        return playWithoutSaving;
    }
}