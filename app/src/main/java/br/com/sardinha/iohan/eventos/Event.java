package br.com.sardinha.iohan.eventos;

/**
 * Created by Iohan on 13/06/2017.
 */

public class Event {

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String title;
    private String description;

    public Event()
    {

    }

    public Event( String title, String description) {
        this.title = title;
        this.description = description;
    }
}

