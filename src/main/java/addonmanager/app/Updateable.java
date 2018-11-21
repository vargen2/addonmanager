package addonmanager.app;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Updateable {
    ReadOnlyStringProperty messageProperty();

    ReadOnlyDoubleProperty progressProperty();

    void updateMessage(String message);

    void updateProgress(double current, double max);

    Updateable EMPTY_UPDATEABLE = new Updateable() {

        private ReadOnlyStringProperty message = new SimpleStringProperty("");
        private ReadOnlyDoubleProperty progress = new SimpleDoubleProperty(0);

        @Override
        public ReadOnlyStringProperty messageProperty() {
            return message;
        }

        @Override
        public ReadOnlyDoubleProperty progressProperty() {
            return progress;
        }

        @Override
        public void updateMessage(String message) {

        }

        @Override
        public void updateProgress(double current, double max) {

        }
    };

    static Updateable createUpdateable(Task task, Consumer<String> stringConsumer) {
        return new Updateable() {
            @Override
            public ReadOnlyStringProperty messageProperty() {
                return task.messageProperty();
            }

            @Override
            public ReadOnlyDoubleProperty progressProperty() {
                return task.progressProperty();
            }

            @Override
            public void updateMessage(String message) {
                stringConsumer.accept(message);
            }

            @Override
            public void updateProgress(double current, double max) {

            }
        };
    }

    static Updateable createUpdateable(Task task, BiConsumer<Double, Double> doubleBiConsumerConsumer) {
        return new Updateable() {
            @Override
            public ReadOnlyStringProperty messageProperty() {
                return task.messageProperty();
            }

            @Override
            public ReadOnlyDoubleProperty progressProperty() {
                return task.progressProperty();
            }

            @Override
            public void updateMessage(String message) {

            }

            @Override
            public void updateProgress(double current, double max) {
                doubleBiConsumerConsumer.accept(current, max);
            }
        };
    }

    static Updateable createUpdateable(Task task, Consumer<String> stringConsumer, BiConsumer<Double, Double> doubleBiConsumerConsumer) {
        return new Updateable() {
            @Override
            public ReadOnlyStringProperty messageProperty() {
                return task.messageProperty();
            }

            @Override
            public ReadOnlyDoubleProperty progressProperty() {
                return task.progressProperty();
            }

            @Override
            public void updateMessage(String message) {
                stringConsumer.accept(message);
            }

            @Override
            public void updateProgress(double current, double max) {
                doubleBiConsumerConsumer.accept(current, max);
            }
        };
    }
}
