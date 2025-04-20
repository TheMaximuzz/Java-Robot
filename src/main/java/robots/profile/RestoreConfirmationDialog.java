package robots.profile;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class RestoreConfirmationDialog extends JDialog {
    private ResourceBundle messages;
    private boolean restoreSelected = false;
    private boolean closedWithCross = false;

    public RestoreConfirmationDialog(Locale locale) {
        super((Frame) null, true);
        messages = ResourceBundle.getBundle("messages", locale);
        initUI();
    }

    private void initUI() {
        setTitle(messages.getString("restoreDialogTitle"));
        setLayout(new BorderLayout(10, 10));

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                closedWithCross = true;
            }
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (closedWithCross) {
                    System.exit(0);
                }
            }
        });

        JLabel messageLabel = new JLabel(messages.getString("restoreDialogMessage"));
        add(messageLabel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton yesButton = new JButton(messages.getString("yesButtonText"));
        yesButton.addActionListener(e -> {
            restoreSelected = true;
            dispose();
        });
        JButton noButton = new JButton(messages.getString("noButtonText"));
        noButton.addActionListener(e -> {
            restoreSelected = false;
            dispose();
        });
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    public boolean isRestoreSelected() {
        return restoreSelected;
    }
}