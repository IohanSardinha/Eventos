package br.com.sardinha.iohan.eventos;

import android.media.Image;
import android.net.Uri;
import android.widget.ImageView;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by iohan.soares on 24/02/2017.
 */

public class Evento implements Serializable,Comparable<Evento> {

    //region Getters and Setters

    public String getUserID() {
    return userID;
    }

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

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setDataHora(double dataHora) {
        this.dataHora = dataHora;
    }

    public String getTituloLow() {
        return tituloLow;
    }

    public void setTituloLow(String tituloLow) {
        this.tituloLow = tituloLow;
    }
    //endregion

    private String titulo;
    private String tituloLow;
    private String endereco;
    private String horaInicio;
    private String horaEncerramento;
    private String dataInicio;
    private String dataEncerramento;
    private String tipo;
    private String privacidade;
    private String descricao;
    private int limite;
    private double dataHora;
    private String imagem;
    private String id;
    private String idPesquisa;
    private String userID;

    public Evento()
    {
        //Empty constructor for Firebase
    }

    public Evento(String userID,String titulo,String endereco, String dataInicio, String dataEncerramento, String horaInicio, String horaEncerramento, String tipo, String privacidade, String descricao, int limite){
        this.userID = userID;
        this.setTitulo(titulo);
        this.endereco = endereco;
        this.setDataInicio(dataInicio);
        this.setDataEncerramento(dataEncerramento);
        this.setHoraInicio(horaInicio);
        this.setHoraEncerramento(horaEncerramento);
        this.setTipo(tipo);
        this.setPrivacidade(privacidade);
        this.setDescricao(descricao);
        this.setLimite(limite);
        this.tituloLow = Normalizer.normalize(titulo, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();
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
        this.dataHora = Double.parseDouble(tempDataHora);
    }


    @Override
    public int compareTo(Evento e) {
        return Double.compare(this.dataHora,e.dataHora);
    }


    public String getIdPesquisa() {
        return idPesquisa;
    }

    public void setIdPesquisa(String idPesquisa) {
        this.idPesquisa = idPesquisa;
    }
}
