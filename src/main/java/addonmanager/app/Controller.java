package addonmanager.app;

import addonmanager.core.Addon;
import addonmanager.core.Game;
import addonmanager.core.Model;
import addonmanager.file.DirectoryScanTask;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SegmentedButton;
import org.controlsfx.control.TaskProgressView;

import java.util.function.Consumer;

public class Controller {


    @FXML
    private Button refreshButton;
    @FXML
    private Button settingsButton;
    @FXML
    private ChoiceBox gameChoiceBox;
    @FXML
    private TableView<Addon> tableView;
    @FXML
    private StackPane bottomStackPane;
    @FXML
    private TaskProgressView<Task<Void>> taskProgressView;

    @FXML
    private void initialize() {
        Model model = new Model();
        Settings settings = new Settings(model);

        gameChoiceBox.getItems().add(new Separator());

        ChoiceBoxItem add = new ChoiceBoxItem(new Consumer() {
            @Override
            public void accept(Object o) {

            }
        }, "Add Games...");
        gameChoiceBox.getItems().add(add);
        ChoiceBoxItem manual = new ChoiceBoxItem(new Consumer() {
            @Override
            public void accept(Object o) {

            }
        }, "Add Directory manually...");
        gameChoiceBox.getItems().add(manual);
        ChoiceBoxItem scan = new ChoiceBoxItem(new Consumer() {


            @Override
            public void accept(Object o) {
                DirectoryScanTask ds = new DirectoryScanTask(model, gameChoiceBox, taskProgressView, false);
                Platform.runLater(() -> taskProgressView.getTasks().add(ds));

                Thread t = new Thread(ds);
                t.setDaemon(true);
                t.start();
            }
        }, "Scan for Directories...");
        gameChoiceBox.getItems().add(scan);

        gameChoiceBox.setValue(add);
        gameChoiceBox.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                //System.out.println("triggred");
                if (((ChoiceBoxItem) newValue).getGame() != null) {
                    gameChoiceBox.getItems().remove(add);
                    model.selectedGame.setValue(((ChoiceBoxItem) newValue).getGame());
                }
                if (newValue == manual || newValue == scan) {
                    ((ChoiceBoxItem) newValue).getConsumer().accept(0);
                    gameChoiceBox.setValue(oldValue);
                }

            }
        });


        //gameChoiceBox.setItems(model.games);
        //gameChoiceBox.valueProperty().bindBidirectional(model.selectedGame);

//        progressBar.prefWidthProperty().bind(bottomStackPane.widthProperty());
//        statusLabel.prefWidthProperty().bind(bottomStackPane.widthProperty());
//
//        progressBar.setVisible(false);
//        statusLabel.setVisible(false);

        taskProgressView.getTasks().addListener(new ListChangeListener<Task<Void>>() {
            @Override
            public void onChanged(Change<? extends Task<Void>> c) {
                c.next();
                if (c.wasAdded())
                    taskProgressView.setPrefHeight(taskProgressView.getPrefHeight() + 70);
                if (c.wasRemoved())
                    taskProgressView.setPrefHeight(taskProgressView.getPrefHeight() - 70);

            }
        });

//        experimentButton.setOnAction(event -> {
        //addonmanager.net.Experi exp = new addonmanager.net.Experi();
        //exp.experimentRedirect("omni-cc");


//            addonmanager.old.Finder finder = new addonmanager.old.Finder("World of Warcraft",true);
//            try {
//                Files.walkFileTree(File.listRoots()[0].toPath(), EnumSet.noneOf(FileVisitOption.class),4, finder);
//                //Files.walkFileTree(File.listRoots()[0].toPath(), finder);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            finder.done();


//            FileSystemManager fsManager = VFS.getManager();
//            FileObject fileObject = fsManager.resolveFile( "yourFileNameHere" );
//            FileObject[] files = fileObject.findFiles( new FileTypeSelector( FileType.FOLDER ) )


//            var drives = File.listRoots();
//            var ds = new addonmanager.old.DirectoryStream(drives, "Interface\\Addons");
//            ds.dirs.addListener(new ListChangeListener<Path>() {
//                @Override
//                public void onChanged(Change<? extends Path> c) {
//                    c.next();
//                    var aaa = c.getAddedSubList();
//                    aaa.forEach(x -> System.out.println(x.toString()));
//                    System.out.println(addonmanager.old.DirectoryStream.counter);
//                }
//            });
//            ds.getDirs();
//            System.out.println("done");


//            var res=FileUtils.listFiles(new File("C:\\"),new NameFileFilter("Users"), DirectoryFileFilter.DIRECTORY);
//            res.forEach(x->System.out.println(x.getPath()));

//            addonmanager.old.CommonIOFinder walker = new addonmanager.old.CommonIOFinder(
//                    //DirectoryFileFilter.DIRECTORY
//            );
//
//            try {
//                var aa=walker.getDirectories(new File("C:\\"));
//                aa.forEach(x->System.out.println(x.toString()));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


//            Path dirs = Paths.get("C:/");
//
//            try {
//                Files.walkFileTree(dirs, new SimpleFileVisitor<Path>() {
//                    @Override
//                    public FileVisitResult preVisitDirectory(Path file,
//                                                             BasicFileAttributes attrs) {
//
//                        addonmanager.old.DirectoryStream.Filter<Path> filter = new addonmanager.old.DirectoryStream.Filter<Path>() {
//                            @Override
//                            public boolean accept(Path entry) throws IOException {
//                                BasicFileAttributes attr = Files.readAttributes(entry,
//                                        BasicFileAttributes.class);
//
//                                return (entry.startsWith("World of Warcraft"));
//                            }
//                        };
//                        try (addonmanager.old.DirectoryStream<Path> stream = Files.newDirectoryStream(
//                                file, filter)) {
//                            for (Path path : stream) {
//                                System.out.println(path.toString());
//                            }
//                        } catch (IOException ex) {
//                            ex.printStackTrace();
//                        }
//                        return FileVisitResult.CONTINUE;
//
//                    }
//                });
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });

//        scanButton.setOnAction(event -> {
//            addonmanager.file.DirectoryScanner2 ds = new addonmanager.file.DirectoryScanner2(model, gameChoiceBox, taskProgressView);
//            Platform.runLater(() -> taskProgressView.getTasks().add(ds));
//
//            Thread t = new Thread(ds);
//            t.setDaemon(true);
//            t.start();
//        });

        model.selectedGame.addListener((observable, oldValue, newValue) -> {
            tableView.setItems(newValue.addons);
            refreshButton.setDisable(newValue == null);
        });


        TableColumn<Addon, String> titleVersionCol = new TableColumn<>("Title");
        titleVersionCol.setCellValueFactory(new PropertyValueFactory("titleVersion"));
        titleVersionCol.setPrefWidth(200);

//        TableColumn<Addon, Addon.ReleaseType> releaseTypeCol = new TableColumn<>("Release Type");
//        releaseTypeCol.setCellValueFactory(new PropertyValueFactory("releaseType"));
//        releaseTypeCol.setPrefWidth(50);
//        releaseTypeCol.setCellFactory(new Callback<TableColumn<Addon, Addon.ReleaseType>, TableCell<Addon, Addon.ReleaseType>>() {
//            @Override
//            public TableCell<Addon, Addon.ReleaseType> call(TableColumn<Addon, Addon.ReleaseType> param) {
//                return new TableCell<Addon, Addon.ReleaseType>() {
//
//                    @Override
//                    public void updateItem(Addon.ReleaseType item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (!isEmpty()) {
//                            this.setStyle("-fx-font-size: 2em");
//                            setText(item.toString());
//                        }else {
//                            setText(null);
//                        }
//                    }
//                };
//            }
//        });

        TableColumn<Addon, String> releaseLatestCol = new TableColumn<>("Latest Version");
        releaseLatestCol.setCellValueFactory(new PropertyValueFactory("releaseLatest"));
        releaseLatestCol.setPrefWidth(100);
        releaseLatestCol.setCellFactory(ReleaseLatestVersionCell.cellFactory());

        TableColumn<Addon, Addon.State> stateCol = new TableColumn<>("Status");
        stateCol.setCellFactory(StatusCell.cellFactory());

        stateCol.setCellValueFactory(new PropertyValueFactory<Addon, Addon.State>("state"));
        stateCol.setPrefWidth(200);


        TableColumn<Addon, String> gameVersionCol = new TableColumn<>("Game Version");
        gameVersionCol.setCellValueFactory(new PropertyValueFactory("gameVersion"));
        gameVersionCol.setPrefWidth(100);

        tableView.getColumns().setAll(titleVersionCol, releaseLatestCol, stateCol, gameVersionCol);
//        tableView.addEventFilter(ScrollEvent.ANY, scrollEvent -> {
//            tableView.refresh();
//
//            System.out.println("scroll");
//        });

        refreshButton.setOnAction(event -> {
            Game game = model.selectedGame.getValue();
            if (game == null)
                return;
            game.refresh();
            game.refreshFromNet();
        });

        settingsButton.setOnAction(event -> {
            if(!settings.isShowing())
                settings.show(settingsButton);
            else
                settings.hide();
        });



    }

//    private void directoryscanner1(Model model) {
//        AtomicInteger counter = new AtomicInteger(0);
//        DirectoryScanner ds = new DirectoryScanner();
//        SetChangeListener<File> fileListener = change -> {
//            Task<Void> task = new Task<>() {
//                @Override
//                protected Void call() {
//                    Game game = new Game("Wow " + (counter.addAndGet(1)), change.getElementAdded().getPath(), File.separator + "Interface" + File.separator + "AddOns");
//                    Platform.runLater(() -> {
//                        if (tableView.itemsProperty().getValue().size() == 0)
//                            model.selectedGame.setValue(game);
//                        model.games.add(game);
//                    });
//                    Task<Void> refreshTask = new Task<>() {
//                        @Override
//                        protected Void call() {
//                            game.refresh();
//                            return null;
//                        }
//                    };
//                    Thread t = new Thread(refreshTask);
//                    t.setDaemon(true);
//                    t.start();
//                    return null;
//                }
//            };
//            Platform.runLater(() -> taskProgressView.getTasks().add(task));
//            Thread t = new Thread(task);
//            t.setDaemon(true);
//            t.start();
//        };
//        ds.fileObservableList.addListener(fileListener);
//        Task<Void> task = new Task<>() {
//            @Override
//            protected Void call() {
//                updateMessage("searching...");
//                ds.searchForWowDirectorys();
//                updateMessage("building...");
//                updateMessage("done");
//                return null;
//            }
//        };
//        Platform.runLater(() -> taskProgressView.getTasks().add(task));
//        task.setOnScheduled(event1 -> {
////            statusLabel.textProperty().bind(task.messageProperty());
////            progressBar.progressProperty().bind(ds.progress);
////            progressBar.setVisible(true);
//        });
//        task.setOnSucceeded(workerStateEvent -> {
////            progressBar.setVisible(false);
////            progressBar.progressProperty().unbind();
////            statusLabel.textProperty().unbind();
//            ds.fileObservableList.removeListener(fileListener);
////            statusLabel.setText("found " + counter.get() + " games");
//        });
//        Thread t = new Thread(task);
//        t.setDaemon(true);
//        t.start();
//    }


}
