package robots.base;

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
        try {
            if (!isMaximum() && !isIcon() && getWidth() > 0 && getHeight() > 0) {
                setNormalBounds(new Rectangle(getX(), getY(), getWidth(), getHeight()));
            }
            setMaximum(maximized);
            if (!maximized) {
                Rectangle normalBounds = getNormalBounds();
                if (normalBounds != null && normalBounds.width > 0 && normalBounds.height > 0) {
                    setBounds(normalBounds);
                }
            }
            setVisible(true);
            toFront();
            requestFocus();
            revalidate();
            repaint();
            updateDesktopPane();
        } catch (Exception e) {
            System.err.println(getTitle() + ": Ошибка с настройками максимизации: " + e.getMessage());
        }
    }

    public void setIconified(boolean iconified) {
        try {
            if (!isIcon() && !isMaximum() && getWidth() > 0 && getHeight() > 0) {
                setNormalBounds(new Rectangle(getX(), getY(), getWidth(), getHeight()));
            }
            setIcon(iconified);
            if (!iconified) {
                Rectangle normalBounds = getNormalBounds();
                if (normalBounds != null && normalBounds.width > 0 && normalBounds.height > 0) {
                    setBounds(normalBounds);
                }
                setVisible(true);
                toFront();
                requestFocus();
            }
            revalidate();
            repaint();
            updateDesktopPane();
        } catch (Exception e) {
            System.err.println(getTitle() + ": Ошибка с настройками сворачивания: " + e.getMessage());
        }
    }

    public void restoreMaximizedState(int x, int y, int width, int height) {
        try {
            if (isIcon()) {
                setIconified(false);
            }
            setMaximized(true);
            setBounds(x, y, width, height);
            setVisible(true);
            toFront();
            requestFocus();
            revalidate();
            repaint();
            updateDesktopPane();
        } catch (Exception e) {
            System.err.println(getTitle() + ": Ошибка сохранения состояния: " + e.getMessage());
        }
    }

    public boolean isIcon() {
        try {
            return super.isIcon();
        } catch (Exception e) {
            System.err.println(getTitle() + ": Ошибка при проверке сворачивания: " + e.getMessage());
            return false;
        }
    }

    public boolean isMaximum() {
        try {
            return super.isMaximum();
        } catch (Exception e) {
            System.err.println(getTitle() + ": Ошибка при проверки максимизации: " + e.getMessage());
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
        }
    }
}