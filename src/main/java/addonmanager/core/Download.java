package addonmanager.core;

import java.time.LocalDateTime;

public class Download {
    public String release;
    public String title;
    public String fileSize;
    public LocalDateTime fileDateUploaded;
    public String gameVersion;
    public Long downloads;
    public String downloadLink;

    @Override
    public String toString() {
        return title;
    }

    public String toDetailedString() {
        return "release='" + release + '\'' +
                ", title='" + title + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", fileDateUploaded=" + fileDateUploaded +
                ", gameVersion='" + gameVersion + '\'' +
                ", downloads=" + downloads +
                ", downloadLink='" + downloadLink + '\'';
    }
}
