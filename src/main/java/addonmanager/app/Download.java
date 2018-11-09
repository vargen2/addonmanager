package addonmanager.app;

import java.time.LocalDateTime;

public class Download {
    private final String release;
    private final String title;
    private final String fileSize;
    private final LocalDateTime fileDateUploaded;
    private final String gameVersion;
    private final Long downloads;
    private final String downloadLink;

    public Download(String release, String title, String fileSize, LocalDateTime fileDateUploaded, String gameVersion, Long downloads, String downloadLink) {
        this.release = release;
        this.title = title;
        this.fileSize = fileSize;
        this.fileDateUploaded = fileDateUploaded;
        this.gameVersion = gameVersion;
        this.downloads = downloads;
        this.downloadLink = downloadLink;
    }

    public String getRelease() {
        return release;
    }

    public String getTitle() {
        return title;
    }

    public String getFileSize() {
        return fileSize;
    }

    public LocalDateTime getFileDateUploaded() {
        return fileDateUploaded;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public Long getDownloads() {
        return downloads;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

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
