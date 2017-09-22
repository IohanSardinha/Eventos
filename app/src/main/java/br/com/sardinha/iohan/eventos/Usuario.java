package br.com.sardinha.iohan.eventos;

import java.io.Serializable;
import java.text.Normalizer;

/**
 * Created by Iohan on 20/06/2017.
 */

public class Usuario implements Serializable {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getEventosConfirmados() {
        return eventosConfirmados;
    }

    public void setEventosConfirmados(int eventosConfirmados) {
        this.eventosConfirmados = eventosConfirmados;
    }

    public int getEventosComparecidos() {
        return eventosComparecidos;
    }

    public void setEventosComparecidos(int eventosComparecidos) {
        this.eventosComparecidos = eventosComparecidos;
    }

    public String getNomeLow() {
        return nomeLow;
    }

    public void setNomeLow(String nomeLow) {
        this.nomeLow = nomeLow;
    }

    private String id;
    private String nome;
    private String nomeLow;
    private String email;
    private int eventosConfirmados = 10;
    private int eventosComparecidos = 10;

    public Usuario() {
    }

    public Usuario(String id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.nomeLow = Normalizer.normalize(nome, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();
        this.email = email;
    }

    public Usuario(String id, String nome, String email,int eventosConfirmados, int eventosComparecidos) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.eventosConfirmados = eventosConfirmados;
        this.eventosComparecidos = eventosComparecidos;
    }


}
