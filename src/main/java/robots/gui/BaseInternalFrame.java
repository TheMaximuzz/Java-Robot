package robots.gui;

import robots.util.ConfirmCloseHelper;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.util.Locale;
import java.util.ResourceBundle;

public abstract class BaseInternalFrame extends JInternalFrame {
    protected final ConfirmCloseHelper closeHelper = new ConfirmCloseHelper();
    protected ResourceBundle messages;

    public BaseInternalFrame(boolean resizable, boolean closable, boolean maximizable, boolean iconifiable) {
        super("", resizable, closable, maximizable, iconifiable);
        this.messages = ResourceBundle.getBundle("messages", Locale.getDefault());
        setTitle(messages.getString(getTitleKey())); // Устанавливаем заголовок с помощью getTitleKey()
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

    protected abstract String getTitleKey(); // Абстрактный метод для получения ключа заголовка

    public void updateLanguage(ResourceBundle newMessages) {
        this.messages = newMessages;
        closeHelper.updateLanguage(messages);
        setTitle(messages.getString(getTitleKey())); // Обновляем заголовок при смене языка
        revalidate();
        repaint();
    }
}