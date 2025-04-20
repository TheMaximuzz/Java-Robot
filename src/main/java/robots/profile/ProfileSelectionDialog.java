package robots.profile;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ProfileSelectionDialog extends JDialog {
    private ResourceBundle messages;
    private JComboBox<String> profileComboBox;
    private String selectedProfileName = null;
    private boolean playSelected = false;
    private boolean backSelected = false;
    private List<Profile> sortedProfiles;

    public ProfileSelectionDialog(List<Profile> profiles, Locale locale) {
        super((Frame) null, true);
        messages = ResourceBundle.getBundle("messages", locale);
        sortedProfiles = profiles.stream()
                .sorted(Comparator.comparingInt(Profile::getProfileNumber))
                .collect(Collectors.toList());

        initUI(locale);
    }

    private void initUI(Locale locale) {
        setTitle(messages.getString("profileDialogTitle"));
        setLayout(new BorderLayout(10, 10));

        // если крестик
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                backSelected = true;
            }
        });

        // панель со списком профилей
        JPanel centerPanel = new JPanel(new FlowLayout());
        JLabel messageLabel = new JLabel(messages.getString("profileDialogMessage"));
        centerPanel.add(messageLabel);
        profileComboBox = new JComboBox<>();
        for (Profile profile : sortedProfiles) {
            profileComboBox.addItem(profile.getProfileName(locale));
        }
        centerPanel.add(profileComboBox);
        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton playButton = new JButton(messages.getString("playButtonText"));
        playButton.addActionListener(e -> {
            selectedProfileName = (String) profileComboBox.getSelectedItem();
            playSelected = true;
            dispose();
        });
        JButton backButton = new JButton(messages.getString("backButtonText"));
        backButton.addActionListener(e -> {
            backSelected = true;
            dispose();
        });
        buttonPanel.add(playButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    public String getSelectedProfileName() {
        return selectedProfileName;
    }

    public boolean isPlaySelected() {
        return playSelected;
    }

    public boolean isBackSelected() {
        return backSelected;
    }
}