package br.com.sardinha.iohan.eventos.Class;


public class Comentario {



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    private String id;
    private String userID;
    private String comentario;
    private long timestamp;

    public Comentario()
    {

    }
    public Comentario(String id, String userID, String comentario)
    {
        this.id = id;
        this.userID = userID;
        this.comentario = comentario;
        timestamp = System.currentTimeMillis() / 1000L;

    }
}
