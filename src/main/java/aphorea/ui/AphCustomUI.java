package aphorea.ui;

import necesse.gfx.forms.Form;
import necesse.gfx.forms.MainGameFormManager;

import java.util.HashMap;
import java.util.Map;

abstract public class AphCustomUI {
    public static Map<String, AphCustomUI> list = new HashMap<>();
    public static AttackTrackManger attackTrackManager;

    public Form form = null;
    public MainGameFormManager mainGameFormManager = null;

    abstract public void updatePosition();
}