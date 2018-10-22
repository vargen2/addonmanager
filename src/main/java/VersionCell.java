import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class VersionCell extends TableCell<Addon, String> {


    private final StackPane pane;
    private final Button button;
    private final Label label;

    public VersionCell() {

        super();
        //getChildren().add(new Button("texte"));
        //getChildren().add(new Label("everythinf fine"));
        pane=new StackPane();
        button = new Button("asdasd asd");
        label=new Label("lable");
        StackPane.setAlignment(button, Pos.CENTER);
        StackPane.setAlignment(label, Pos.BOTTOM_CENTER);
        pane.getChildren().add(button);
        pane.getChildren().add(label);
        button.setVisible(false);
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        var addon=getTableRow().getItem();

        label.setText(item);
        if (!empty)
            setGraphic(pane);
    }

    //    @Override
//    protected void updateItem(Number item, boolean empty) {
//        // calling super here is very important - don't skip this!
//        super.updateItem(item, empty);
//
//        // format the number as if it were a monetary value using the
//        // formatting relevant to the current locale. This would format
//        // 43.68 as "$43.68", and -23.67 as "-$23.67"
//        setText(item == null ? "" : NumberFormat.getCurrencyInstance().format(item));
//
//        // change the text fill based on whether it is positive (green)
//        // or negative (red). If the cell is selected, the text will
//        // always be white (so that it can be read against the blue
//        // background), and if the value is zero, we'll make it black.
//        if (item != null) {
//            double value = item.doubleValue();
//            setTextFill(isSelected() ? Color.WHITE :
//                    value == 0 ? Color.BLACK :
//                            value < 0 ? Color.RED : Color.GREEN);
//        }
//    }
}
