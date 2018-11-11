package addonmanager.gui;

import addonmanager.app.Addon;
import addonmanager.app.App;
import addonmanager.app.Model;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
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

//todo ändra till propertysheet i popovern
public class Settings {


    private VBox releaseTypeVBox;
    private VBox rootVBox;
    private PopOver popOver;
    private Model model;
    private PropertySheet propertySheet;

    public Settings(Model model) {
        this.model = model;

    }

    private void init() {
        propertySheet = new PropertySheet();
        propertySheet.setModeSwitcherVisible(false);
        propertySheet.setSearchBoxVisible(false);
        for (String key : customDataMap.keySet()) {
            CustomPropertyItem customPropertyItem=new CustomPropertyItem(key);
            customPropertyItem.setValue(Addon.ReleaseType.RELEASE);
            customPropertyItem.getObservableValue().get().addListener((observable, oldValue, newValue) -> {
               App.setReleaseType(model.getSelectedGame(),(Addon.ReleaseType) newValue);
            });
            propertySheet.getItems().add(customPropertyItem);
        }


//        Label releaseTypeLabel = new Label("Set all");
//        Button b1 = new Button("release");
//        Button b2 = new Button("beta");
//        Button b3 = new Button("alpha");
//        b1.setOnAction(event -> App.setReleaseType(model.getSelectedGame(), Addon.ReleaseType.RELEASE));
//        b2.setOnAction(event -> App.setReleaseType(model.getSelectedGame(), Addon.ReleaseType.BETA));
//        b3.setOnAction(event -> App.setReleaseType(model.getSelectedGame(), Addon.ReleaseType.ALPHA));
//        HBox releaseTypeHBox = new HBox(0, b1, b2, b3);
//        releaseTypeVBox = new VBox(0, releaseTypeLabel, releaseTypeHBox);
        rootVBox = new VBox(0, propertySheet);
        rootVBox.setPadding(new Insets(2, 2, 2, 2));
        popOver = new PopOver(rootVBox);
        popOver.setAnimated(true);
        popOver.setDetachable(false);
        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);


    }

    private void refresh() {
        if (model.getSelectedGame() == null) {
//            propertySheet.getItems()
//                    .stream()
//                    .filter(x->x.getCategory().equals("current"))
//                    .map(x->(CustomPropertyItem)x)
//                    .forEach(x->x.setEditable(false));
            //rootVBox.getChildren().remove(releaseTypeVBox);
        } else {
//            propertySheet.getItems()
//                    .stream()
//                    .filter(x->x.getCategory().equals("current"))
//                    .map(x->(CustomPropertyItem)x)
//                    .forEach(x->x.setEditable(true));
//            if (!rootVBox.getChildren().contains(releaseTypeVBox))
//                rootVBox.getChildren().addAll(releaseTypeVBox);
        }

    }

    public void show(Node node) {
        if (popOver == null)
            init();
        refresh();
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

    public static Map<String, Object> customDataMap = new LinkedHashMap<>();

    static {
        //customDataMap.put("basic.My Text", "Same text"); // Creates a TextField in property sheet
        //customDataMap.put("basic.My Date", LocalDate.of(2016, Month.JANUARY, 1)); // Creates a DatePicker
        customDataMap.put("current.Set All", Addon.ReleaseType.RELEASE); // Creates a ChoiceBox
        //customDataMap.put("current.Set All", Addon.ReleaseType.RELEASE); // Creates a ChoiceBox
        //customDataMap.put("misc.My Boolean", false); // Creates a CheckBox
        // customDataMap.put("misc.My Number", 500); // Creates a NumericField
        // customDataMap.put("misc.My Color", Color.ALICEBLUE); // Creates a ColorPicker
    }

    class CustomPropertyItem implements PropertySheet.Item {


        private String key;
        private String category, name;
        private ObjectProperty objectProperty=new SimpleObjectProperty();
        //private boolean editable;

        public CustomPropertyItem(String key) {
            this.key = key;
            String[] skey = key.split("\\.", 2);
            category = skey[0];
            name = skey[1];
        }
//
//        @Override
//        public boolean isEditable() {
//            return editable;
//        }
//
//        public void setEditable(boolean editable){
//            this.editable=editable;
//        }

        @Override
        public Class<?> getType() {
            return customDataMap.get(key).getClass();
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
            return customDataMap.get(key);
        }

        @Override
        public void setValue(Object value) {
            customDataMap.put(key, value);
            objectProperty.setValue(value);
        }

        @Override
        public Optional<ObservableValue<? extends Object>> getObservableValue() {
            return Optional.of(objectProperty);
        }

        @Override
        public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
            // for an item of type number, specify the type of editor to use
            if (Number.class.isAssignableFrom(getType())) return Optional.of(NumberSliderEditor.class);

            // ... return other editors for other types

            return Optional.empty();
        }
    }

    class NumberSliderEditor extends AbstractPropertyEditor<Number, Slider> {

        public NumberSliderEditor(PropertySheet.Item property, Slider control) {
            super(property, control);
        }

        public NumberSliderEditor(PropertySheet.Item item) {
            this(item, new Slider());
        }

        @Override
        public void setValue(Number n) {
            this.getEditor().setValue(n.doubleValue());
        }

        @Override
        protected ObservableValue<Number> getObservableValue() {
            return this.getEditor().valueProperty();
        }

    }
}
