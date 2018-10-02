import java.time.LocalDateTime;

public class Download {
String release;
String title;
String fileSize;
LocalDateTime fileDateUploaded;
String gameVersion;
Long downloads;
String downloadLink;

    @Override
    public String toString() {
        return "Download{" +
                "release='" + release + '\'' +
                ", title='" + title + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", fileDateUploaded=" + fileDateUploaded +
                ", gameVersion='" + gameVersion + '\'' +
                ", downloads=" + downloads +
                ", downloadLink='" + downloadLink + '\'' +
                '}';
    }
}
