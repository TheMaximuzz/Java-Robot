package robots.gui;

import robots.util.ConfirmCloseHelper;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.util.ResourceBundle;

public abstract class BaseInternalFrame extends JInternalFrame {
    protected final ConfirmCloseHelper closeHelper = new ConfirmCloseHelper();

    public BaseInternalFrame(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable) {
        super(title, resizable, closable, maximizable, iconifiable);
        initializeClosingBehavior();
    }

    private void initializeClosingBehavior() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                if (confirmClose()) {
                    dispose();
                }
            }
        });
    }

    protected abstract boolean confirmClose();

    public void updateLanguage(ResourceBundle messages) {
        closeHelper.updateLanguage(messages);
    }
}