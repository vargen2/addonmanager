package addonmanager.gui;

import addonmanager.app.Addon;
import addonmanager.app.App;
import addonmanager.app.Model;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableMapValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.AbstractPropertyEditor;
import org.controlsfx.property.editor.PropertyEditor;

import java.time.LocalDate;
import java.time.Month;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class Settings {

    private final ObservableMap<String, Object> observableMap;
    private PopOver popOver;
    private Model model;

    public Settings(Model model) {
        this.model = model;
        observableMap = FXCollections.observableMap(new LinkedHashMap<>());
        observableMap.put("current.Set All", Addon.ReleaseType.RELEASE);
    }

    private void init() {
        PropertySheet propertySheet = new PropertySheet();
        propertySheet.setModeSwitcherVisible(false);
        propertySheet.setSearchBoxVisible(false);
        for (String key : observableMap.keySet()) {
            propertySheet.getItems().add(new CustomPropertyItem(key, observableMap));
        }

        VBox rootVBox = new VBox(0, propertySheet);
        rootVBox.setPadding(new Insets(2, 2, 2, 2));
        popOver = new PopOver(rootVBox);
        popOver.setAnimated(true);
        popOver.setDetachable(false);
        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);

        observableMap.addListener((MapChangeListener<? super String, ? super Object>) change -> {
            if (change == null || change.getKey() == null)
                return;
            if (change.getKey().equals("current.Set All")) {
                if (change.getValueAdded() instanceof Addon.ReleaseType)
                    App.setReleaseType(model.getSelectedGame(), (Addon.ReleaseType) change.getValueAdded());
            }
        });
    }

    public void show(Node node) {
        if (popOver == null)
            init();
        popOver.show(node);
    }

    public void hide() {
        if (popOver == null)
            return;
        popOver.hide();
    }

    public boolean isShowing() {
        if (popOver == null)
            return false;
        return popOver.isShowing();
    }

    class CustomPropertyItem implements PropertySheet.Item {


        private String key;
        private String category, name;
        private ObservableMap<String, Object> observableMap;

        public CustomPropertyItem(String key, ObservableMap<String, Object> observableMap) {
            this.key = key;
            String[] skey = key.split("\\.", 2);
            category = skey[0];
            name = skey[1];
            this.observableMap = observableMap;
        }

        @Override
        public Class<?> getType() {
            return observableMap.get(key).getClass();
        }

        @Override
        public String getCategory() {
            return category;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            // doesn't really fit into the map
            return null;
        }

        @Override
        public Object getValue() {
            return observableMap.get(key);
        }

        @Override
        public void setValue(Object value) {
            observableMap.put(key, value);
        }

        @Override
        public Optional<ObservableValue<? extends Object>> getObservableValue() {
            return Optional.empty();
        }

//        @Override
//        public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
//            // for an item of type number, specify the type of editor to use
//            if (Number.class.isAssignableFrom(getType())) return Optional.of(NumberSliderEditor.class);
//
//            // ... return other editors for other types
//
//            return Optional.empty();
//        }
    }

//    class NumberSliderEditor extends AbstractPropertyEditor<Number, Slider> {
//
//        public NumberSliderEditor(PropertySheet.Item property, Slider control) {
//            super(property, control);
//        }
//
//        public NumberSliderEditor(PropertySheet.Item item) {
//            this(item, new Slider());
//        }
//
//        @Override
//        public void setValue(Number n) {
//            this.getEditor().setValue(n.doubleValue());
//        }
//
//        @Override
//        protected ObservableValue<Number> getObservableValue() {
//            return this.getEditor().valueProperty();
//        }
//
//    }
}
