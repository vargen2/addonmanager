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
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Saver {

    private static final ThreadFactory DAEMON_THREAD = r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        return thread;
    };

    private static final ThreadPoolExecutor modelSaver = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2), DAEMON_THREAD, new ThreadPoolExecutor.DiscardPolicy());
    private static final ThreadPoolExecutor settingsSaver = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2), DAEMON_THREAD, new ThreadPoolExecutor.DiscardPolicy());
    private static final Set<Settings> settings = new HashSet<>();

    public static Optional<Model> load(Factory factory) {
        if (Files.notExists(Path.of("save.save")))
            return Optional.empty();

        try (FileInputStream fis = new FileInputStream("save.save")) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            return Optional.of(factory.load((Model) ois.readObject()));
        } catch (IOException | ClassNotFoundException e) {
            App.LOG.severe(e.getMessage());
        }
        return Optional.empty();
    }

    public static void save() {
        modelSaver.execute(() -> {
            try (FileOutputStream fos = new FileOutputStream("save.save")) {
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(App.model);
            } catch (NotSerializableException e) {
                System.out.println(e.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
            App.LOG.fine("saved");
            Util.sleep(2000);
        });
    }

    public static void loadSettings(Settings... settings) {
        Saver.settings.addAll(Arrays.asList(settings));
        try {
            String settingsString = Files.readString(Path.of("settings.txt"));
            Arrays.stream(settings).forEach(setting -> setting.load(settingsString));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveSettings() {
        settingsSaver.execute(() -> {
            String saveString = settings.stream().map(Settings::save).collect(Collectors.joining());
            try {
                Files.writeString(Path.of("settings.txt"), saveString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            App.LOG.fine("saved settings");
            Util.sleep(2000);
        });
    }

}
