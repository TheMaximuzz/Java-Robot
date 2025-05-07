package robots.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@JsonSerialize(using = WindowStateSerializer.class)
public class WindowState {
    private int x;
    private int y;
    private boolean visible;
    private boolean maximized;
    private int normalX;
    private int normalY;
    private int width;
    private int height;
    private int normalWidth;
    private int normalHeight;
    private boolean iconified;
    private boolean pendingMaximize;
    private String windowName;

    public WindowState(String windowName, int x, int y, boolean visible, boolean maximized,
                       int normalX, int normalY, int width, int height,
                       int normalWidth, int normalHeight, boolean iconified, boolean pendingMaximize) {
        this.windowName = windowName;
        this.x = x;
        this.y = y;
        this.visible = visible;
        this.maximized = maximized;
        this.normalX = normalX;
        this.normalY = normalY;
        this.width = width;
        this.height = height;
        this.normalWidth = normalWidth;
        this.normalHeight = normalHeight;
        this.iconified = iconified;
        this.pendingMaximize = pendingMaximize;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isMaximized() {
        return maximized;
    }

    public int getNormalX() {
        return normalX;
    }

    public int getNormalY() {
        return normalY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getNormalWidth() {
        return normalWidth;
    }

    public int getNormalHeight() {
        return normalHeight;
    }

    public boolean isIconified() {
        return iconified;
    }

    public boolean isPendingMaximize() {
        return pendingMaximize;
    }

    public String getWindowName() {
        return windowName;
    }

    public Map<String, Object> saveParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put(windowName + "X", x);
        params.put(windowName + "Y", y);
        params.put(windowName + "Visible", visible);
        params.put(windowName + "Maximized", maximized);
        params.put(windowName + "NormalX", normalX);
        params.put(windowName + "NormalY", normalY);
        params.put(windowName + "Width", width);
        params.put(windowName + "Height", height);
        params.put(windowName + "NormalWidth", normalWidth);
        params.put(windowName + "NormalHeight", normalHeight);
        params.put(windowName + "Iconified", iconified);
        params.put(windowName + "PendingMaximize", pendingMaximize);
        return params;
    }
}

class WindowStateSerializer extends JsonSerializer<WindowState> {
    @Override
    public void serialize(WindowState value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField(value.getWindowName() + "X", value.getX());
        gen.writeNumberField(value.getWindowName() + "Y", value.getY());
        gen.writeBooleanField(value.getWindowName() + "Visible", value.isVisible());
        gen.writeBooleanField(value.getWindowName() + "Maximized", value.isMaximized());
        gen.writeNumberField(value.getWindowName() + "NormalX", value.getNormalX());
        gen.writeNumberField(value.getWindowName() + "NormalY", value.getNormalY());
        gen.writeNumberField(value.getWindowName() + "Width", value.getWidth());
        gen.writeNumberField(value.getWindowName() + "Height", value.getHeight());
        gen.writeNumberField(value.getWindowName() + "NormalWidth", value.getNormalWidth());
        gen.writeNumberField(value.getWindowName() + "NormalHeight", value.getNormalHeight());
        gen.writeBooleanField(value.getWindowName() + "Iconified", value.isIconified());
        gen.writeBooleanField(value.getWindowName() + "PendingMaximize", value.isPendingMaximize());
        gen.writeEndObject();
    }
}