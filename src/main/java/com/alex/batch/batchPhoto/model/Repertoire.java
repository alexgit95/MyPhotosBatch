package main.java.com.alex.batch.batchPhoto.model;

public class Repertoire {
	
	private String chemin;
	private boolean isRacine;
	
	
	
	
	
	public Repertoire() {
		super();
	}
	public Repertoire(String chemin, boolean isRacine) {
		super();
		this.chemin = chemin;
		this.isRacine = isRacine;
	}
	public String getChemin() {
		return chemin;
	}
	public void setChemin(String chemin) {
		this.chemin = chemin;
	}
	public boolean isRacine() {
		return isRacine;
	}
	public void setRacine(boolean isRacine) {
		this.isRacine = isRacine;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chemin == null) ? 0 : chemin.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Repertoire other = (Repertoire) obj;
		if (chemin == null) {
			if (other.chemin != null)
				return false;
		} else if (!chemin.equals(other.chemin))
			return false;
		return true;
	}
	
	
	

}
