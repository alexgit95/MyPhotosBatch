package main.java.com.alex.batch.batchPhoto;

import java.util.Date;

public class PhotosBuilder {
	private String nom;
	private String chemin;
	private double longitude;
	private double lattitude;
	private Date datePriseVue;
	private Date dateAjout;
	private boolean estFavoris;
	private String ville;
	private String region;
	private String pays;
	private int annee;
	private int mois;
	private int jour;
	private int minute;
	private int secondes;
	private int heure;
	
	private boolean isScanEvenement;

	public Photos build() {
		//return new Photos(nom, chemin, longitude, lattitude, datePriseVue, dateAjout, estFavoris, annee, mois, jour, minute, secondes, heure);
		return new Photos(nom, chemin, longitude, lattitude, datePriseVue, dateAjout, estFavoris, ville, region, pays, annee, mois, jour, minute, secondes, heure);
	}
	
	
	public PhotosBuilder nom(String nom) {
		this.nom=nom;
		return this;
	}
	public PhotosBuilder chemin(String chemin) {
		this.chemin=chemin;
		return this;
	}
	
	public PhotosBuilder lattitude(double lattitude) {
		this.lattitude=lattitude;
		return this;
	}
	public PhotosBuilder longitude(double longitude) {
		this.longitude=longitude;
		return this;
	}
	
	public PhotosBuilder datePriseVue(Date datePriseVue) {
		this.datePriseVue=datePriseVue;
		return this;
	}
	public PhotosBuilder dateAjout(Date dateAjout) {
		this.dateAjout=dateAjout;
		return this;
	}
	
	public PhotosBuilder estFavoris(boolean estFavoris) {
		this.estFavoris=estFavoris;
		return this;
	}
	public PhotosBuilder ville(String ville) {
		this.ville=ville;
		return this;
	}
	public PhotosBuilder region(String region) {
		this.region=region;
		return this;
	}
	public PhotosBuilder pays(String pays) {
		this.pays=pays;
		return this;
	}
	public PhotosBuilder annee(int annee) {
		this.annee=annee;
		return this;
	}
	public PhotosBuilder mois(int mois) {
		this.mois=mois;
		return this;
	}
	public PhotosBuilder jour(int jour) {
		this.jour=jour;
		return this;
	}
	public PhotosBuilder heure(int heure) {
		this.heure=heure;
		return this;
	}
	public PhotosBuilder minute(int minute) {
		this.minute=minute;
		return this;
	}
	public PhotosBuilder secondes(int secondes) {
		this.secondes=secondes;
		return this;
	}
	

}
