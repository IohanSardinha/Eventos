package br.com.sardinha.iohan.eventos;

/**
 * Created by Iohan on 13/06/2017.
 */

public class Event {
    private String id;

    public int getId() {
        return Integer.parseInt(id);
    }

    public void setId(int id) {
        this.id = String.valueOf(id);
    }

    public int getUser_id() {
        return Integer.valueOf(user_id);
    }

    public void setUser_id(int user_id) {
        this.user_id = String.valueOf(user_id);
    }

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

    private String user_id;
    private String title;
    private String description;


    public Event(String id, String user_id, String title, String description) {
        this.id = id;
        this.user_id = user_id;
        this.title = title;
        this.description = description;
    }
}

