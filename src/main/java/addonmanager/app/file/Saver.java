package addonmanager.app.file;

import addonmanager.app.App;
import addonmanager.app.Factory;
import addonmanager.app.Model;
import addonmanager.gui.fxapp.FXModel;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

public class Saver {

    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private static final StampedLock lock = new StampedLock();

    //todo 1 per game?
    //todo 1 file for app and
    // todo 1for fx settings
    public static Model load(Factory factory) {
        if (Files.notExists(Path.of("save.save")))
            return null;

        try (FileInputStream fis = new FileInputStream("save.save")) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            Model model = new FXModel((Model) ois.readObject());
            return model;
        } catch (IOException | ClassNotFoundException e) {
            App.LOG.severe(e.getMessage());
        }
        return null;
    }

    public static void save() {
        long value = lock.tryWriteLock();
        if (value == 0)
            return;
        executor.schedule(() -> {
            try (FileOutputStream fos = new FileOutputStream("save.save")) {
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(new Model(App.model));
            } catch (NotSerializableException e) {
                System.out.println(e.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
            App.LOG.info("saved");
            lock.unlockWrite(value);
        }, 3, TimeUnit.SECONDS);
    }

    public static void exit() {
        try {
            System.out.println("attempt to shutdown executor");
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("tasks interrupted");
        } finally {
            if (!executor.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }
            executor.shutdownNow();
            System.out.println("shutdown finished");
        }
    }

//    public static void save() {
//        save2();
////        if (thread == null)
////            init();
////        blockingQueue.add(new Object());
//    }

//    private static void init() {
//        if (thread != null)
//            return;
//        thread = new Thread(() -> {
//            while (true) {
//                try {
//                    blockingQueue.take();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                blockingQueue.clear();
//
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                try (FileOutputStream fos = new FileOutputStream("save.save")) {
//                    ObjectOutputStream oos = new ObjectOutputStream(fos);
//                    oos.writeObject(new Model(App.model));
//                } catch (NotSerializableException e) {
//                    System.out.println(e.getMessage());
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("save");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        thread.setDaemon(true);
//        thread.start();
//    }

}
