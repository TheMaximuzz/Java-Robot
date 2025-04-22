package robots.gui;

import robots.util.ConfirmCloseHelper;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.util.Locale;
import java.util.ResourceBundle;
import java.awt.Rectangle;

public abstract class BaseInternalFrame extends JInternalFrame {
    protected final ConfirmCloseHelper closeHelper = new ConfirmCloseHelper();
    protected ResourceBundle messages;

    public BaseInternalFrame(boolean resizable, boolean closable, boolean maximizable, boolean iconifiable) {
        super("", resizable, closable, maximizable, iconifiable);
        this.messages = ResourceBundle.getBundle("messages", Locale.getDefault());
        setTitle(messages.getString(getTitleKey()));
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

    protected abstract String getTitleKey();

    public void updateLanguage(ResourceBundle newMessages) {
        this.messages = newMessages;
        closeHelper.updateLanguage(messages);
        setTitle(messages.getString(getTitleKey()));
        revalidate();
        repaint();
    }

    public void updateLanguageIfVisible(ResourceBundle newMessages) {
        if (isVisible()) {
            updateLanguage(newMessages);
        }
    }

    public void setMaximized(boolean maximized) {
        System.out.println("Setting maximized state for " + getTitle() + ": " + maximized);
        try {
            // Сохраняем нормальные размеры перед максимизацией
            if (!isMaximum() && !isIcon() && getWidth() > 0 && getHeight() > 0) {
                setNormalBounds(new Rectangle(getX(), getY(), getWidth(), getHeight()));
                System.out.println("Saved normal bounds before maximize: x=" + getX() + ", y=" + getY() +
                        ", width=" + getWidth() + ", height=" + getHeight());
            }
            setMaximum(maximized);
            if (!maximized) {
                // Восстанавливаем нормальные размеры
                Rectangle normalBounds = getNormalBounds();
                if (normalBounds != null && normalBounds.width > 0 && normalBounds.height > 0) {
                    setBounds(normalBounds);
                    System.out.println("Restored normal bounds after unmaximize: x=" + normalBounds.x +
                            ", y=" + normalBounds.y + ", width=" + normalBounds.width + ", height=" + normalBounds.height);
                }
            }
            setVisible(true);
            toFront();
            requestFocus();
            revalidate();
            repaint();
            updateDesktopPane();
        } catch (Exception e) {
            System.err.println("Error setting maximized state for " + getTitle() + ": " + e.getMessage());
        }
    }

    public void setIconified(boolean iconified) {
        System.out.println("Setting iconified state for " + getTitle() + ": " + iconified);
        try {
            // Сохраняем нормальные размеры перед иконификацией
            if (!isIcon() && !isMaximum() && getWidth() > 0 && getHeight() > 0) {
                setNormalBounds(new Rectangle(getX(), getY(), getWidth(), getHeight()));
                System.out.println("Saved normal bounds before iconify: x=" + getX() + ", y=" + getY() +
                        ", width=" + getWidth() + ", height=" + getHeight());
            }
            setIcon(iconified);
            if (!iconified) {
                // Восстанавливаем нормальные размеры
                Rectangle normalBounds = getNormalBounds();
                if (normalBounds != null && normalBounds.width > 0 && normalBounds.height > 0) {
                    setBounds(normalBounds);
                    System.out.println("Restored normal bounds after deiconify: x=" + normalBounds.x +
                            ", y=" + normalBounds.y + ", width=" + normalBounds.width + ", height=" + normalBounds.height);
                }
                setVisible(true);
                toFront();
                requestFocus();
            }
            revalidate();
            repaint();
            updateDesktopPane();
        } catch (Exception e) {
            System.err.println("Error setting iconified state for " + getTitle() + ": " + e.getMessage());
        }
    }

    public boolean isIcon() {
        try {
            return super.isIcon();
        } catch (Exception e) {
            System.err.println("Error checking icon state for " + getTitle() + ": " + e.getMessage());
            return false;
        }
    }

    public boolean isMaximum() {
        try {
            return super.isMaximum();
        } catch (Exception e) {
            System.err.println("Error checking maximum state for " + getTitle() + ": " + e.getMessage());
            return false;
        }
    }

    public void setNormalBounds(Rectangle bounds) {
        super.setNormalBounds(bounds);
    }

    public Rectangle getNormalBounds() {
        return super.getNormalBounds();
    }

    private void updateDesktopPane() {
        if (getParent() instanceof JDesktopPane) {
            JDesktopPane desktopPane = (JDesktopPane) getParent();
            desktopPane.revalidate();
            desktopPane.repaint();
            System.out.println("Updated JDesktopPane for " + getTitle());
        }
    }
}