package main.java.com.alex.batch.batchPhoto;

import java.util.List;

public interface PhotosRepositoryCustom  {
	
	List<Photos> findPhotosWithNoGeocoding();
	List<Photos> findPhotosWithNoGeolocalisation();
	List<Photos> findPhotosWithGeoCoding();
	/**
	 * Recupere la photo la plus proche, dans la limite du pas en minute
	 * @param p
	 * @param pas
	 * @return
	 */
	Photos getNearestPhoto(Photos p, int pas);
	
	/**
	 * Recupere la photo la plus proche avec Geocodage, dans la limite du pas en secondes
	 * @param p
	 * @param pas
	 * @return
	 */
	Photos getNearestPhotoWithGeocoding(Photos p, int pas);
	
	Photos findOnePhotosByNom(String nom);
}
