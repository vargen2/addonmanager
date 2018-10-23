package addonmanager.old;

public class GameVersion {

    int id;
    int gameVersionTypeID;
    String name;
    String slug;

    @Override
    public String toString() {
        return "addonmanager.old.GameVersion{" +
                "id=" + id +
                ", gameVersionTypeID=" + gameVersionTypeID +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                '}';
    }
}
