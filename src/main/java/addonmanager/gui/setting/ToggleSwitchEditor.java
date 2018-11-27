package addonmanager.gui.setting;

import javafx.beans.value.ObservableValue;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.property.editor.AbstractPropertyEditor;

public class ToggleSwitchEditor extends AbstractPropertyEditor<Boolean, ToggleSwitch> {

    public ToggleSwitchEditor(PropertySheet.Item property, ToggleSwitch control) {
        super(property, control);
    }

    public ToggleSwitchEditor(PropertySheet.Item item) {
        this(item, new ToggleSwitch());
    }

    @Override
    public void setValue(Boolean b) {
        this.getEditor().setSelected(b);
    }

    @Override
    protected ObservableValue<Boolean> getObservableValue() {
        return this.getEditor().selectedProperty();
    }

}
