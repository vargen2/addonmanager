package addonmanager.app;

import java.time.LocalDateTime;

public final class Download {
    private final String release;
    private final String title;
    private final String fileSize;
    private final LocalDateTime fileDateUploaded;
    private final String gameVersion;
    private final long downloads;
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

    public final String getRelease() {
        return release;
    }

    public final String getTitle() {
        return title;
    }

    public final String getFileSize() {
        return fileSize;
    }

    public final LocalDateTime getFileDateUploaded() {
        return fileDateUploaded;
    }

    public final String getGameVersion() {
        return gameVersion;
    }

    public final long getDownloads() {
        return downloads;
    }

    public final String getDownloadLink() {
        return downloadLink;
    }

    @Override
    public final String toString() {
        return title;
    }

    public final String toDetailedStringOneLine() {
        return "release='" + release + '\'' +
                ", title='" + title + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", fileDateUploaded=" + fileDateUploaded +
                ", gameVersion='" + gameVersion + '\'' +
                ", downloads=" + downloads +
                ", downloadLink='" + downloadLink + '\'';
    }

    public final String toDetailedStringLines() {
        return "release=" + release + "\n" +
                "title=" + title + "\n" +
                "fileSize=" + fileSize + "\n" +
                "fileDateUploaded=" + fileDateUploaded + "\n" +
                "gameVersion=" + gameVersion + "\n" +
                "downloads=" + downloads + "\n" +
                "downloadLink=" + downloadLink;
    }
}
