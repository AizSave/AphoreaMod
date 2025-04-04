package aphorea.ui;

import necesse.engine.Settings;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.MainGameFormManager;
import necesse.gfx.gameTexture.GameTexture;

import java.util.HashMap;
import java.util.Map;

abstract public class AphCustomUI {
    public static final int TICK_MS = 25;

    public final String formId;

    public Form form;
    public MainGameFormManager mainGameFormManager;

    protected AphCustomUI(String formId) {
        this.formId = formId;
        AphCustomUIList.list.put(formId, this);
    }

    abstract public void startForm();

    abstract public void updatePosition();

    abstract public int getWidth();
    abstract public int getHeight();

    public void updateSize() {
        this.form.setWidth(getWidth());
        this.form.setHeight(getHeight());
    }

    public void setupForm() {
        updatePosition();
    }

    public void onWindowResized() {
        updatePosition();
    }

    public void onUpdateSceneSize() {}

    public static float getZoom() {
        return Settings.sceneSize;
    }

    public static float showProgress(float chargePercent, float chargeTime) {
        float timeSinceStart = chargePercent * chargeTime;
        return (timeSinceStart + TICK_MS) / chargeTime;
    }

    public static Map<String, GameTexture> textureMap = new HashMap<>();


    public static GameTexture getResizedTexture(String string, GameTexture originalTexture, int width, int height) {
        String id = string + "-" + String.format("%.1f", getZoom() / 2);
        GameTexture texture = textureMap.get(id);
        if(texture == null) {
            texture = originalTexture.resize(width, height);
            textureMap.put(id, texture);
        }
        return texture;
    }
}