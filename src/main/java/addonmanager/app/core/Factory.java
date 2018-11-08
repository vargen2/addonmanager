package addonmanager.app.core;

public interface Factory {
    Game createGame(String name, String directory, String addonDirectory);
    Addon createAddon(String folderName, String absolutePath);
    Model createModel();
}
