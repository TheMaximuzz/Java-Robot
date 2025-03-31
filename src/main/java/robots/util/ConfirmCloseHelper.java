package robots.util;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.util.ResourceBundle;

public class ConfirmCloseHelper implements InternalFrameListener {
    private ResourceBundle messages;

    public ConfirmCloseHelper() {
        messages = ResourceBundle.getBundle("messages");
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent e) {
        JInternalFrame frame = (JInternalFrame) e.getSource();
        if (confirmClose(frame)) {
            frame.dispose();
        }
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent e) {}
    @Override
    public void internalFrameOpened(InternalFrameEvent e) {}
    @Override
    public void internalFrameIconified(InternalFrameEvent e) {}
    @Override
    public void internalFrameDeiconified(InternalFrameEvent e) {}
    @Override
    public void internalFrameActivated(InternalFrameEvent e) {}
    @Override
    public void internalFrameDeactivated(InternalFrameEvent e) {}

    public boolean confirmClose(JComponent parentComponent) {
        return showConfirmationDialog(parentComponent, messages.getString("confirmCloseLogWindow"), messages.getString("confirmCloseTitle"));
    }

    public boolean confirmClose(JFrame parentFrame) {
        return showConfirmationDialog(parentFrame, messages.getString("confirmCloseApp"), messages.getString("confirmCloseTitle"));
    }

    public boolean showConfirmationDialog(Object parent, String message, String title) {
        Object[] options = {
                messages.getString("yesButtonText"),
                messages.getString("noButtonText")
        };

        int option = JOptionPane.showOptionDialog(
                parent instanceof JComponent ? (JComponent) parent : null,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]
        );

        return option == JOptionPane.YES_OPTION;
    }

    public void updateLanguage(ResourceBundle newMessages) {
        this.messages = newMessages;
    }
}