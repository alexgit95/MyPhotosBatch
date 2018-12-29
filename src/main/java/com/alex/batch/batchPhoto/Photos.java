package main.java.com.alex.batch.batchPhoto;

import java.util.Date;

import org.springframework.data.annotation.Id;

public class Photos {
	 
		@Id
	  public String id;
	  public String nom;
	  public String chemin;
	  public double longitude;
	  public double lattitude;
	  public Date datePriseVue;
	  public Date dateAjout;
	  public boolean estFavoris;
	  public String ville;
	  public String region;
	  public String pays;
	  public int annee;
	  public int mois;
	  public int jour;
	  public int minute;
	  public int secondes;
	  public int heure;
	  public Evenements evt;
	  
	  
	  public Photos() {}

	public Photos(String nom, String chemin) {
		super();
		this.nom = nom;
		this.chemin = chemin;
	}

	public Photos(String nom, String chemin, Date datePriseVue, Date dateAjout, boolean estFavoris, int annee, int mois,
			int jour, int minute, int secondes, int heure) {
		super();
		this.nom = nom;
		this.chemin = chemin;
		this.datePriseVue = datePriseVue;
		this.dateAjout = dateAjout;
		this.estFavoris = estFavoris;
		this.annee = annee;
		this.mois = mois;
		this.jour = jour;
		this.minute = minute;
		this.secondes = secondes;
		this.heure = heure;
	}

	public Photos(String nom, String chemin, double longitude, double lattitude, Date datePriseVue, Date dateAjout,
			boolean estFavoris, int annee, int mois, int jour, int minute, int secondes, int heure) {
		super();
		this.nom = nom;
		this.chemin = chemin;
		this.longitude = longitude;
		this.lattitude = lattitude;
		this.datePriseVue = datePriseVue;
		this.dateAjout = dateAjout;
		this.estFavoris = estFavoris;
		this.annee = annee;
		this.mois = mois;
		this.jour = jour;
		this.minute = minute;
		this.secondes = secondes;
		this.heure = heure;
	}
	
	

	public Photos(String nom, String chemin, double longitude, double lattitude, Date datePriseVue, Date dateAjout,
			boolean estFavoris, String ville, String region, String pays, int annee, int mois, int jour, int minute,
			int secondes, int heure) {
		super();
		this.nom = nom;
		this.chemin = chemin;
		this.longitude = longitude;
		this.lattitude = lattitude;
		this.datePriseVue = datePriseVue;
		this.dateAjout = dateAjout;
		this.estFavoris = estFavoris;
		this.ville = ville;
		this.region = region;
		this.pays = pays;
		this.annee = annee;
		this.mois = mois;
		this.jour = jour;
		this.minute = minute;
		this.secondes = secondes;
		this.heure = heure;
	}

	@Override
	public String toString() {
		return "Photos [nom=" + nom + ", chemin=" + chemin + ", longitude=" + longitude + ", lattitude=" + lattitude
				+ ", datePriseVue=" + datePriseVue + ", dateAjout=" + dateAjout + ", estFavoris=" + estFavoris
				+ ", ville=" + ville + ", region=" + region + ", pays=" + pays + ", annee=" + annee + ", mois=" + mois
				+ ", jour=" + jour + ", minute=" + minute + ", secondes=" + secondes + ", heure=" + heure + "]";
	}
	
	
	
	
	



	
	
	  
	  
	  

}
