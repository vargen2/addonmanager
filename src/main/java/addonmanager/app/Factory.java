package addonmanager.app;

public interface Factory {
    Game createGame(String name, String directory, String addonDirectory);

    Addon createAddon(Game game, String folderName, String absolutePath);
    Model createModel();
}
