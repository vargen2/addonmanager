package addonmanager.gui.setting;

import addonmanager.app.Addon;
import addonmanager.app.App;
import addonmanager.app.Model;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;
import org.controlsfx.property.editor.Editors;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.logging.Level;

public class SettingsController {

    private final ObservableMap<String, Object> observableMap;
    private PopOver popOver;
    private Model model;
    private FXSettings fxSettings;

    public SettingsController(Model model, FXSettings fxSettings) {
        this.model = model;
        this.fxSettings = fxSettings;
        observableMap = FXCollections.observableMap(new LinkedHashMap<>());
        observableMap.put("current.Set All", Addon.ReleaseType.RELEASE);
        observableMap.put("global.Refresh Delay", fxSettings.getRefreshDelay());
        observableMap.put("global.Console Log", App.getConsoleLoggingLevel());
        observableMap.put("global.File Log", App.getFileLoggingLevel());
        var defaultPropertyEditorFactory = new DefaultPropertyEditorFactory();
        PropertySheet propertySheet = new PropertySheet();
        propertySheet.setPropertyEditorFactory(param -> {
            if (param.getValue() instanceof Number) {
                return new NumberSliderEditor(param);
            }

            if (param.getValue() instanceof Level) {
                return Editors.createChoiceEditor(param, App.levels);
            }

            return defaultPropertyEditorFactory.call(param);
        });

        propertySheet.setMode(PropertySheet.Mode.NAME);
        propertySheet.setModeSwitcherVisible(false);
        propertySheet.setSearchBoxVisible(false);
        for (String key : observableMap.keySet()) {
            propertySheet.getItems().add(new CustomPropertyItem(key, observableMap));
        }

        Pane pane = new Pane(propertySheet);
        pane.setPadding(new Insets(2, 2, 2, 2));
        popOver = new PopOver(pane);
        popOver.setAnimated(false);
        popOver.setDetachable(false);
        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_RIGHT);
        popOver.setArrowSize(0);
        popOver.setCornerRadius(0);

        observableMap.addListener((MapChangeListener<? super String, ? super Object>) change -> {
            if (change == null || change.getKey() == null)
                return;

            var addedValue = change.getValueAdded();
            if (change.getKey().equals("current.Set All")) {
                if (addedValue instanceof Addon.ReleaseType)
                    App.setReleaseType(model.getSelectedGame(), (Addon.ReleaseType) addedValue);
            } else if (change.getKey().equals("global.Refresh Delay")) {
                if (addedValue instanceof Number) {
                    int intVal = ((Number) addedValue).intValue();
                    if (intVal % 100 == 0)
                        fxSettings.setRefreshDelay(intVal);
                }
            } else if (change.getKey().equals("global.Console Log")) {
                if (addedValue instanceof Level) {
                    App.setConsoleLoggingLevel((Level) addedValue);
                }
            } else if (change.getKey().equals("global.File Log")) {
                if (addedValue instanceof Level) {
                    App.setFileLoggingLevel((Level) addedValue);
                }
            }
        });
    }


    public void show(Node node) {
        popOver.show(node.getScene().getWindow());
    }

    public void hide() {
        popOver.hide();
    }

    public boolean isShowing() {
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
//            if (Number.class.isAssignableFrom(getType())) {
//
//                return Optional.of(NumberSliderEditor.class);
//            }
////            if(Level.class.isAssignableFrom(getType())){
////                System.out.println("hit bbb");
////                return Optional.of();
////            }
////            // ... return other editors for other types
//
//            return Optional.empty();
//        }
    }

}
