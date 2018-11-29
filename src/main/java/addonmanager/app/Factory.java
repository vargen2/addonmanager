package addonmanager.app;

public interface Factory {
    Game createGame(String name, String directory, String addonDirectory);

    Addon createAddon(Game game, String folderName, String absolutePath);

    Model createModel();

    Model load(Model model);

    CurseAddon load(CurseAddon curseAddon);
}
