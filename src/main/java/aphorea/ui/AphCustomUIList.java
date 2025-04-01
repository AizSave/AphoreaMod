package aphorea.ui;

import java.util.HashMap;
import java.util.Map;

abstract public class AphCustomUIList {
    public static Map<String, AphCustomUI> list = new HashMap<>();
    public static GunAttackUIManger gunAttack;
    public static SaberAttackUIManger saberAttack;

    static {
        gunAttack = new GunAttackUIManger("gunattack");
        saberAttack = new SaberAttackUIManger("saberattack");
    }
}