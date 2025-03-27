package robots.gui;

import robots.util.ConfirmCloseHelper;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class BaseFrame extends JFrame {
    protected final ConfirmCloseHelper closeHelper = new ConfirmCloseHelper();

    public BaseFrame(String title) {
        super(title);
        initializeClosingBehaviorForFrame();
    }

    // поведение закрытия для JFrame
    private void initializeClosingBehaviorForFrame() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (confirmClose()) {
                    System.exit(0);
                }
            }
        });
    }

    protected abstract boolean confirmClose();
}