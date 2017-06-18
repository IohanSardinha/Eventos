package br.com.sardinha.iohan.eventos;

import android.media.Image;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by iohan.soares on 24/02/2017.
 */

public class Evento implements Serializable {

    //region Getters and Setters
    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraEncerramento() {
        return horaEncerramento;
    }

    public void setHoraEncerramento(String horaEncerramento) {
        this.horaEncerramento = horaEncerramento;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPrivacidade() {
        return privacidade;
    }

    public void setPrivacidade(String privacidade) {
        this.privacidade = privacidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getLimite() {
        return limite;
    }

    public void setLimite(int limite) {
        this.limite = limite;
    }

    public double getDataHora(){return dataHora;}

    public String getDataEncerramento() {
        return dataEncerramento;
    }

    public void setDataEncerramento(String dataEncerramento) {
        this.dataEncerramento = dataEncerramento;
    }

    public int getImagem() {
        return imagem;
    }

    public void setImagem(int imagem) {
        this.imagem = imagem;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //endregion

    private String titulo;
    private String horaInicio;
    private String horaEncerramento;
    private String dataInicio;
    private String dataEncerramento;
    private String tipo;
    private String privacidade;
    private String descricao;
    private int limite;
    private double dataHora;
    private int imagem;
    private String id;

    public Evento()
    {
        //Empty constructor for Firebase
    }

    public Evento(String titulo, String dataInicio, String dataEncerramento, String horaInicio, String horaEncerramento, String tipo, String privacidade, String descricao, int limite){
        this.setTitulo(titulo);
        this.setDataInicio(dataInicio);
        this.setDataEncerramento(dataEncerramento);
        this.setHoraInicio(horaInicio);
        this.setHoraEncerramento(horaEncerramento);
        this.setTipo(tipo);
        this.setPrivacidade(privacidade);
        this.setDescricao(descricao);
        this.setLimite(limite);
        List<String> tempData = Arrays.asList(dataInicio.split("/"));
        Collections.reverse(tempData);
        List<String> tempHora = Arrays.asList(horaInicio.split(":"));
        String tempDataHora = "";
        for (String s : tempData) {
            tempDataHora += s;
        }
        for (String s: tempHora) {
            tempDataHora += s;
        }
        if(tipo.equals("Aniversário")){
            this.setImagem(R.drawable.aniversario);
        }
        else if(tipo.equals("Show")){
            this.setImagem(R.drawable.show);
        }
        else if(tipo.equals("Festa"))
        {
            this.setImagem(R.drawable.festa);
        }
        else
        {
            imagem = -1;
        }
        this.dataHora = Double.parseDouble(tempDataHora);
    }



}
