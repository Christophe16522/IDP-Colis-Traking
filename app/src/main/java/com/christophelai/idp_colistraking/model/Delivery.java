package com.christophelai.idp_colistraking.model;

public class Delivery {
    private int id;
    private String nomComplet;
    private String adresse;
    private String telephone;
    private String nComande;
    private String ville;
    private String nSuivi;
    private String status;

    public Delivery() {
    }

    public Delivery(int id, String nomComplet, String adresse, String telephone, String nComande, String ville, String nSuivi, String status) {
        this.id = id;
        this.nomComplet = nomComplet;
        this.adresse = adresse;
        this.telephone = telephone;
        this.nComande = nComande;
        this.ville = ville;
        this.nSuivi = nSuivi;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getnComande() {
        return nComande;
    }

    public void setnComande(String nComande) {
        this.nComande = nComande;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getnSuivi() {
        return nSuivi;
    }

    public void setnSuivi(String nSuivi) {
        this.nSuivi = nSuivi;
    }
}
