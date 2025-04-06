package robots.gui;

import robots.util.ConfirmCloseHelper;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.ResourceBundle;

public abstract class BaseFrame extends JFrame {
    protected final ConfirmCloseHelper closeHelper = new ConfirmCloseHelper();
    protected ResourceBundle messages;

    public BaseFrame() {
        super("");
        this.messages = ResourceBundle.getBundle("messages", Locale.getDefault());
        setTitle(messages.getString(getTitleKey())); // Устанавливаем заголовок с помощью getTitleKey()
        initializeClosingBehaviorForFrame();
    }

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

    protected abstract String getTitleKey(); // Абстрактный метод для получения ключа заголовка

    public void updateLanguage(ResourceBundle newMessages) {
        this.messages = newMessages;
        closeHelper.updateLanguage(messages);
        setTitle(messages.getString(getTitleKey())); // Обновляем заголовок при смене языка
        revalidate();
        repaint();
    }
}