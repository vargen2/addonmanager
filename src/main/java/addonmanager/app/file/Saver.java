package addonmanager.app.file;

import addonmanager.app.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Saver {

    private static final ThreadPoolExecutor modelSaver = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
    private static final ThreadPoolExecutor settingsSaver = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
    private static final Set<Settings> settings = new HashSet<>();
    private static final Path MODEL_SAVE_PATH = Path.of("save");
    private static final Path SETTINGS_SAVE_PATH = Path.of("settings.txt");

    public static Optional<Model> load(Factory factory) {
        if (Files.notExists(MODEL_SAVE_PATH))
            return Optional.empty();

        try (FileInputStream fis = new FileInputStream(MODEL_SAVE_PATH.toFile())) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            return Optional.of(factory.load((Model) ois.readObject()));
        } catch (IOException | ClassNotFoundException e) {
            App.LOG.severe(e.getMessage());
        }
        return Optional.empty();
    }

    public static void save() {
        modelSaver.execute(() -> {
            try (FileOutputStream fos = new FileOutputStream(MODEL_SAVE_PATH.toFile())) {
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(new Model(App.model));
            } catch (NotSerializableException e) {
                System.out.println(e.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
            App.LOG.fine("saved");
            Util.sleep(2000);
        });
    }

    public static void exit() {
        modelSaver.shutdown();
        settingsSaver.shutdown();
    }

    public static void loadSettings(Settings... settings) {
        Saver.settings.addAll(Arrays.asList(settings));
        if (Files.notExists(SETTINGS_SAVE_PATH))
            return;
        try {
            String settingsString = Files.readString(SETTINGS_SAVE_PATH);
            Arrays.stream(settings).forEach(setting -> setting.load(settingsString));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveSettings() {
        settingsSaver.execute(() -> {
            String saveString = settings.stream().map(Settings::save).collect(Collectors.joining());
            try {
                Files.writeString(SETTINGS_SAVE_PATH, saveString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            App.LOG.fine("saved settings");
            Util.sleep(2000);
        });
    }

}
