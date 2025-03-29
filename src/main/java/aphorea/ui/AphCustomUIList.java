package aphorea.ui;

import java.util.HashMap;
import java.util.Map;

abstract public class AphCustomUIList {
    public static Map<String, AphCustomUI> list = new HashMap<>();
    public static AttackTrackManger attackTrackManager;

    static {
        attackTrackManager = new AttackTrackManger("attacktrackmanager");
    }
}