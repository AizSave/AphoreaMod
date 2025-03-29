package aphorea.ui;

import necesse.gfx.forms.Form;
import necesse.gfx.forms.MainGameFormManager;

abstract public class AphCustomUI {
    public final String formId;

    public Form form;
    public MainGameFormManager mainGameFormManager;

    protected AphCustomUI(String formId) {
        this.formId = formId;
        AphCustomUIList.list.put(formId, this);
    }

    abstract public void startForm();

    abstract public void updatePosition();

    public void setupForm() {
        updatePosition();
    }

    public void onWindowResized() {
        updatePosition();
    }

}