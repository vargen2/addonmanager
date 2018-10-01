public class GameVersion {

    int id;
    int gameVersionTypeID;
    String name;
    String slug;

    @Override
    public String toString() {
        return "GameVersion{" +
                "id=" + id +
                ", gameVersionTypeID=" + gameVersionTypeID +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                '}';
    }
}
