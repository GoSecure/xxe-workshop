package net.gosecure.rssviewer.model;

public class RssItem {

    private final String title;
    private final String description;
    private final String url;

    public RssItem(String url, String title, String description) {
        this.url = url;
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }
}
