package addonmanager.gui.setting;

import addonmanager.app.Addon;
import addonmanager.app.App;
import addonmanager.app.Model;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;

import java.util.LinkedHashMap;
import java.util.Optional;

public class Settings {

    private final ObservableMap<String, Object> observableMap;
    private PopOver popOver;
    private Model model;
    private FXSettings fxSettings;

    public Settings(Model model, FXSettings fxSettings) {
        this.model = model;
        this.fxSettings = fxSettings;
        observableMap = FXCollections.observableMap(new LinkedHashMap<>());
        observableMap.put("current.Set All", Addon.ReleaseType.RELEASE);
        observableMap.put("global.Refresh Delay", fxSettings.getRefreshDelay());
    }


    private void init() {


        PropertySheet propertySheet = new PropertySheet();
        propertySheet.setMode(PropertySheet.Mode.NAME);
        propertySheet.setModeSwitcherVisible(false);
        propertySheet.setSearchBoxVisible(false);
        for (String key : observableMap.keySet()) {
            propertySheet.getItems().add(new CustomPropertyItem(key, observableMap));
        }

        VBox rootVBox = new VBox(2, propertySheet);
        rootVBox.setAlignment(Pos.CENTER);
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
            } else if (change.getKey().equals("global.Refresh Delay")) {
                if (change.getValueAdded() instanceof Number) {
                    int intVal = ((Number) change.getValueAdded()).intValue();
                    if (intVal % 100 == 0)
                        fxSettings.setRefreshDelay(intVal);
                }
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

        @Override
        public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
            // for an item of type number, specify the type of editor to use
            if (Number.class.isAssignableFrom(getType())) {

                return Optional.of(NumberSliderEditor.class);
            }
            // ... return other editors for other types

            return Optional.empty();
        }
    }

}
