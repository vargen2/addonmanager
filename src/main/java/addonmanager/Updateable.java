package addonmanager;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Updateable {
    public void updateMessage(String message);

    public void updateProgress(double current, double max);

    static Updateable EMPTY_UPDATEABLE = new Updateable() {
        @Override
        public void updateMessage(String message) {

        }

        @Override
        public void updateProgress(double current, double max) {

        }
    };

    static Updateable createUpdateable(Consumer<String> stringConsumer) {
        return new Updateable() {
            @Override
            public void updateMessage(String message) {
                stringConsumer.accept(message);
            }

            @Override
            public void updateProgress(double current, double max) {

            }
        };
    }

    static Updateable createUpdateable(BiConsumer<Double, Double> doubleBiConsumerConsumer) {
        return new Updateable() {
            @Override
            public void updateMessage(String message) {

            }

            @Override
            public void updateProgress(double current, double max) {
                doubleBiConsumerConsumer.accept(current, max);
            }
        };
    }

    static Updateable createUpdateable(Consumer<String> stringConsumer, BiConsumer<Double, Double> doubleBiConsumerConsumer) {
        return new Updateable() {
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
