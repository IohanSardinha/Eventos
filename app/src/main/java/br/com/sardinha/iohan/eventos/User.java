package br.com.sardinha.iohan.eventos;

/**
 * Created by iohan.soares on 12/06/2017.
 */

public class User {

    private String id;
    private String email;
    private String name;
    private String password;

    public User(String id, String email, String name, String password) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return Integer.parseInt(id);
    }

    public void setId(int id) {
        this.id = String.valueOf(id);
    }
}
