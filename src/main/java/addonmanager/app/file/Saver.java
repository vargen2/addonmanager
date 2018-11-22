package addonmanager.app.file;

import addonmanager.app.App;
import addonmanager.app.Factory;
import addonmanager.app.Model;
import addonmanager.app.Settings;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.Collectors;

public class Saver {

    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private static final StampedLock lock = new StampedLock();
    private static final StampedLock settingsLock = new StampedLock();

    private static final Set<Settings> settingsSet = new HashSet<>();

    //todo 1 per game?
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
        long value = lock.tryWriteLock();
        if (value == 0)
            return;
        executor.schedule(() -> {
            try (FileOutputStream fos = new FileOutputStream("save.save")) {
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(App.model);
            } catch (NotSerializableException e) {
                System.out.println(e.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
            App.LOG.fine("saved");
            lock.unlockWrite(value);
        }, 2, TimeUnit.SECONDS);
    }

    public static void exit() {
        try {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("tasks interrupted");
        } finally {
            if (!executor.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }
            executor.shutdownNow();
        }
    }

    public static void loadSettings(Settings... settings) {
        settingsSet.addAll(Arrays.asList(settings));
        try {
            String settingsString = Files.readString(Path.of("settings.txt"));
            Arrays.stream(settings).forEach(setting -> setting.load(settingsString));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveSettings() {
        long value = settingsLock.tryWriteLock();
        if (value == 0)
            return;

        executor.schedule(() -> {
            String saveString = settingsSet.stream().map(Settings::save).collect(Collectors.joining());
            try {
                Files.writeString(Path.of("settings.txt"), saveString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            App.LOG.fine("saved settings");
            settingsLock.unlockWrite(value);
        }, 1, TimeUnit.SECONDS);


    }

}
