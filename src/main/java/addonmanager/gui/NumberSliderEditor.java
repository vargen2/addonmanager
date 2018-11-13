package addonmanager.gui;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.AbstractPropertyEditor;

public class NumberSliderEditor extends AbstractPropertyEditor<Number, Slider> {

    public NumberSliderEditor(PropertySheet.Item property, Slider control) {
        super(property, control);
        control.setMajorTickUnit(500);
        control.setMinorTickCount(4);
        control.setSnapToTicks(true);
        control.setShowTickLabels(true);
    }

    public NumberSliderEditor(PropertySheet.Item item) {
        this(item, new Slider(0,1000,250));
    }

    @Override
    public void setValue(Number n) {
        this.getEditor().setValue(n.intValue());
    }

    @Override
    protected ObservableValue<Number> getObservableValue() {
        return this.getEditor().valueProperty();
    }

}
