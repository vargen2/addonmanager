package addonmanager.app.file;

import addonmanager.app.App;
import addonmanager.app.Factory;
import addonmanager.app.Model;
import addonmanager.gui.fxapp.FXModel;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Saver {

    private static final BlockingQueue blockingQueue = new LinkedBlockingQueue();
    private static Thread thread;

    public static Model load(Factory factory) {

        try (FileInputStream fis = new FileInputStream("save.save")) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            Model model = new FXModel((Model) ois.readObject());
            return model;
        } catch (NotSerializableException e) {
            System.out.println(e.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void save() {
        if (thread == null)
            init();
        blockingQueue.add(new Object());
    }

    private static void init() {
        if (thread != null)
            return;
        thread = new Thread(() -> {
            while (true) {
                try {
                    blockingQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                blockingQueue.clear();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try (FileOutputStream fos = new FileOutputStream("save.save")) {
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(new Model(App.model));
                } catch (NotSerializableException e) {
                    System.out.println(e.getMessage());

                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("save");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

}
