import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.SetChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.*;
import org.controlsfx.control.TaskProgressView;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicInteger;

public class Controller {

    @FXML
    private Button experimentButton, scanButton;
    @FXML
    private ChoiceBox<Game> gameChoiceBox;
    @FXML
    private TableView<Addon> tableView;
    @FXML
    private StackPane bottomStackPane;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label statusLabel;
    @FXML
    private TaskProgressView<Task<Void>> taskProgressView;

    @FXML
    private void initialize() {
        Model model = new Model();

        gameChoiceBox.setItems(model.games);
        gameChoiceBox.valueProperty().bindBidirectional(model.selectedGame);

        progressBar.prefWidthProperty().bind(bottomStackPane.widthProperty());
        statusLabel.prefWidthProperty().bind(bottomStackPane.widthProperty());

        experimentButton.setOnAction(event -> {
            //Experi exp = new Experi();
            //exp.experimentRedirect("omni-cc");


//            Finder finder = new Finder("World of Warcraft",true);
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

            var drives = File.listRoots();
            var ds = new DirectoryStream(drives, "Interface\\Addons");
            ds.dirs.addListener(new ListChangeListener<Path>() {
                @Override
                public void onChanged(Change<? extends Path> c) {
                    c.next();
                    var aaa = c.getAddedSubList();
                    aaa.forEach(x -> System.out.println(x.toString()));
                    System.out.println(DirectoryStream.counter);
                }
            });
            ds.getDirs();
            System.out.println("done");
//            var res=FileUtils.listFiles(new File("C:\\"),new NameFileFilter("Users"), DirectoryFileFilter.DIRECTORY);
//            res.forEach(x->System.out.println(x.getPath()));

//            CommonIOFinder walker = new CommonIOFinder(
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
//                        DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
//                            @Override
//                            public boolean accept(Path entry) throws IOException {
//                                BasicFileAttributes attr = Files.readAttributes(entry,
//                                        BasicFileAttributes.class);
//
//                                return (entry.startsWith("World of Warcraft"));
//                            }
//                        };
//                        try (DirectoryStream<Path> stream = Files.newDirectoryStream(
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
        });

        scanButton.setOnAction(event -> {
            AtomicInteger counter = new AtomicInteger(0);
            DirectoryScanner ds = new DirectoryScanner();
            SetChangeListener<File> fileListener = change -> {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() {
                        Game game = new Game("Wow " + (counter.addAndGet(1)), change.getElementAdded().getPath(), File.separator + "Interface" + File.separator + "AddOns");
                        Platform.runLater(() -> {
                            if (tableView.itemsProperty().getValue().size() == 0)
                                model.selectedGame.setValue(game);
                            model.games.add(game);
                        });
                        Task<Void> refreshTask = new Task<>() {
                            @Override
                            protected Void call() {
                                game.refresh();
                                return null;
                            }
                        };
                        Thread t = new Thread(refreshTask);
                        t.setDaemon(true);
                        t.start();
                        return null;
                    }
                };
                Platform.runLater(() -> taskProgressView.getTasks().add(task));
                Thread t = new Thread(task);
                t.setDaemon(true);
                t.start();
            };
            ds.fileObservableList.addListener(fileListener);
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    updateMessage("searching...");
                    ds.searchForWowDirectorys();
                    updateMessage("building...");
                    updateMessage("done");
                    return null;
                }
            };
            Platform.runLater(() -> taskProgressView.getTasks().add(task));
            task.setOnScheduled(event1 -> {
                statusLabel.textProperty().bind(task.messageProperty());
                progressBar.progressProperty().bind(ds.progress);
                progressBar.setVisible(true);
            });
            task.setOnSucceeded(workerStateEvent -> {
                progressBar.setVisible(false);
                progressBar.progressProperty().unbind();
                statusLabel.textProperty().unbind();
                ds.fileObservableList.removeListener(fileListener);
                statusLabel.setText("found " + counter.get() + " games");
            });
            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();

        });

        model.selectedGame.addListener((observable, oldValue, newValue) -> tableView.setItems(newValue.addons));


        TableColumn<Addon, String> nameCol = new TableColumn<Addon, String>("Title");
        nameCol.setCellValueFactory(new PropertyValueFactory("title"));
        nameCol.setPrefWidth(200);
        TableColumn<Addon, String> gameVersionCol = new TableColumn<Addon, String>("Game Version");
        gameVersionCol.setCellValueFactory(new PropertyValueFactory("gameVersion"));
        gameVersionCol.setPrefWidth(100);

        tableView.getColumns().setAll(nameCol, gameVersionCol);

    }
}
