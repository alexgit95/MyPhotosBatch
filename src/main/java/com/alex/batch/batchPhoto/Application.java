package main.java.com.alex.batch.batchPhoto;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import main.java.com.alex.batch.batchPhoto.model.ReponseGeocoding;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import com.google.gson.Gson;

@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	private PhotosRepository repository;
	@Autowired
	private PhotosRepositoryCustom repositoryCustom;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		boolean test=true;
		if(test) {
			
			System.out.println("");
			
			
			//
			//return;
		}
		//repository.deleteAll();
		
		
		
		
		System.out.println("--------Compte Rendu avant traitements---------");
		List<Photos> findPhotosWithGeoCoding = repositoryCustom.findPhotosWithGeoCoding();
		List<Photos> findPhotosWithNoGeocoding = repositoryCustom.findPhotosWithNoGeocoding();
		List<Photos> findPhotosWithNoGeolocalisation = repositoryCustom.findPhotosWithNoGeolocalisation();
		System.out.println("Photos sans geolocalisation : "+findPhotosWithNoGeolocalisation.size());
		System.out.println("Photos sans Geocodage : "+findPhotosWithNoGeocoding.size());
		System.out.println("Photos Geocodees : "+findPhotosWithGeoCoding.size());
		
		
		System.out.println("-----------------------------------------------");
		System.out.println("----------Ajout des nouveaux elements----------");
		List<File> allFichiers = chargerFichiers("Y:\\Images");
		System.out.println("---------------Début du filtage----------------");
		List<File> fileFiltered=filterFichierDejaPresent(allFichiers);
		System.out.println("----------------Fin du filtage-----------------");
		System.out.println("-----Début de la récuperation des infos--------");
		List<Photos> recuperationInfoComplementaire = recuperationInfoComplementaire(fileFiltered);
		System.out.println("-------Fin de la récuperation des infos--------");
		sauvegardePhotosdansBDD(recuperationInfoComplementaire);
		System.out.println("-----------Sauvegarde effectuee----------------");
		System.out.println("------Completion des elements de geocodage-----");
		List<Photos> fillGeocodageInfo = fillGeocodageInfo(repositoryCustom.findPhotosWithNoGeocoding());
		System.out.println("--------------Fin du geocodage-----------------");
		sauvegardePhotosdansBDD(fillGeocodageInfo);
		System.out.println("-----------Sauvegarde effectuee----------------");
		
		
		findPhotosWithGeoCoding = repositoryCustom.findPhotosWithGeoCoding();
		findPhotosWithNoGeocoding = repositoryCustom.findPhotosWithNoGeocoding();
		findPhotosWithNoGeolocalisation = repositoryCustom.findPhotosWithNoGeolocalisation();
		System.out.println("Photos sans geolocalisation : "+findPhotosWithNoGeolocalisation.size());
		System.out.println("Photos sans Geocodage : "+findPhotosWithNoGeocoding.size());
		System.out.println("Photos Geocodees : "+findPhotosWithGeoCoding.size());
		
		
		System.out.println("--Recuperation des elements de geolocalisation---");
		findPhotosWithNoGeolocalisation = repositoryCustom.findPhotosWithNoGeolocalisation();
		System.out.println("Photos sans geolocalisation : "+findPhotosWithNoGeolocalisation.size());
		System.out.println("--Recherche des infos de geolocalisation -------");
		List<Photos> fillGeolocation = fillGeolocationAndGeocoding(findPhotosWithNoGeolocalisation);
		System.out.println("--FIN Recherche des infos de geolocalisation ---");
		repository.saveAll(fillGeolocation);
		System.out.println("-----------Sauvegarde effectuee------------------");
		System.out.println("--------Compte Rendu après traitements---------");
		findPhotosWithGeoCoding = repositoryCustom.findPhotosWithGeoCoding();
		findPhotosWithNoGeocoding = repositoryCustom.findPhotosWithNoGeocoding();
		findPhotosWithNoGeolocalisation = repositoryCustom.findPhotosWithNoGeolocalisation();
		System.out.println("Photos sans geolocalisation : "+findPhotosWithNoGeolocalisation.size());
		System.out.println("Photos sans Geocodage : "+findPhotosWithNoGeocoding.size());
		System.out.println("Photos Geocodees : "+findPhotosWithGeoCoding.size());
		
	}
	
	
	public List<File> chargerFichiers(String emplacement){
		Collection<File> listFiles = FileUtils.listFiles( new File(emplacement), new IOFileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.getAbsolutePath().endsWith("png")
						|| pathname.getAbsolutePath().endsWith("jpg")
						||pathname.getAbsolutePath().endsWith("jpeg");
			}

			@Override
			public boolean accept(File arg0, String arg1) {
				return arg0.getAbsolutePath().endsWith("png")
						|| arg0.getAbsolutePath().endsWith("jpg")
						||arg0.getAbsolutePath().endsWith("jpeg");
			}
		}, new IOFileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return true;
			}

			@Override
			public boolean accept(File arg0, String arg1) {
				return true;
			}
		});
		return new ArrayList<File> (listFiles);
	}
	
	
	public List<File> filterFichierDejaPresent(List<File> allFiles){
		List<File> result = new ArrayList<>();
		for (File file : allFiles) {
			//System.out.println("On test "+file.getName());
			
			//Photos findByNom = repository.findByNom(file.getName());
			Photos findByNom = repositoryCustom.findOnePhotosByNom(file.getName());
			if(findByNom==null) {
				//System.out.println("On ajoute "+file.getName());
				result.add(file);
			}else {
				//System.out.println("Deja présent on ne l'ajoute pas");
			}
		}
		System.out.println("NB elements à traiter : "+result.size());
		return result;
	}
	
	
	public List<Photos> recuperationInfoComplementaire(List<File> filteredFiles) {
		List<Photos> result = new ArrayList<>();
		long previousTime = System.currentTimeMillis();
		for (int i=0;i<filteredFiles.size();i++) {
			try {
				if(i%100==0&&i>0){
					long currentTimeMillis = System.currentTimeMillis();
					long duree=currentTimeMillis-previousTime;
					previousTime=currentTimeMillis;
					duree/=1000;
					
					int nbUniteRestante=(filteredFiles.size()-i)/100;
					long dureeenSeconde=duree*nbUniteRestante;
					long durees=dureeenSeconde%60;
					long dureem=dureeenSeconde/60;
					System.out.println(i+"/"+filteredFiles.size()+" restant "+dureem+":"+durees);
				}
				File file = filteredFiles.get(i);
				
				PhotosBuilder pb = new PhotosBuilder();
				pb.nom(file.getName()).chemin(file.getAbsolutePath()).estFavoris(false);
				pb.dateAjout(new Date());
				
				
				Metadata metadata = ImageMetadataReader.readMetadata(file);
				ArrayList<GpsDirectory> gpsDirectories = new ArrayList<>(metadata.getDirectoriesOfType(GpsDirectory.class));
				if(gpsDirectories.size()>0){
					GpsDirectory gpsDirectory = gpsDirectories.get(0);
					
					Date gpsDate = gpsDirectory.getGpsDate();
					if(gpsDate==null) {
						//System.out.println("Failover de la date pour "+file.getAbsolutePath());
						gpsDate=getDateFromFilename(file);
					}
					
					
					if(gpsDate!=null){
					
					Calendar calo = Calendar.getInstance();
					calo.setTime(gpsDate);
					pb.datePriseVue(gpsDate).annee(calo.get(Calendar.YEAR)).mois(calo.get(Calendar.MONTH)+1).jour(calo.get(Calendar.DATE));
					pb.heure(calo.get(Calendar.HOUR_OF_DAY)).minute(calo.get(Calendar.MINUTE)).secondes(calo.get(Calendar.SECOND));
					}
					
					GeoLocation exifLocation = gpsDirectory.getGeoLocation();
					if(exifLocation!=null) {
						pb.lattitude(exifLocation.getLatitude()).longitude(exifLocation.getLongitude());
					}else {
						//System.out.println("Pas de geolocalisation pour "+file.getAbsolutePath());
					}
				}
				
				
				result.add(pb.build());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return result;
	}
	
	public void sauvegardePhotosdansBDD(List<Photos> allPhotos) {
		repository.saveAll(allPhotos);
	}
	
	public Date getDateFromFilename(File fichier) {

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("'WP_'yyyyMMdd");
		SimpleDateFormat sdf3= new SimpleDateFormat("'WP_'yyyyMMdd'_'HH'_'mm'_'ss'_Pro'");
		try{
			if(fichier.getName().startsWith("WP_")){
				
				if(fichier.getName().contains("_Pro")){
					Date parse = sdf3.parse(fichier.getName());
					return parse;
					
				}else{
					Date parse = sdf2.parse(fichier.getName().substring(0,11));
					return parse;
				}
				
			}else{
				
			
				return sdf1.parse(fichier.getName());
			}
		} catch (ParseException e) {
			
			
			
			System.out.println("Impossible de parser pour la recuperation de la date :"+fichier.getName());
			e.printStackTrace();
			//System.exit(1);
			//e.printStackTrace();
			return null;
		}

	}
	
	
	public List<Photos> fillGeolocationAndGeocoding(List<Photos> all) {
		List<Photos> result = new ArrayList<>();
		for (Photos photos : all) {
			Photos nearestPhoto = repositoryCustom.getNearestPhoto(photos, 15);
			if (nearestPhoto != null) {
				photos.lattitude = nearestPhoto.lattitude;
				photos.longitude = nearestPhoto.longitude;
				photos.pays=nearestPhoto.pays;
				photos.region=nearestPhoto.region;
				photos.ville=nearestPhoto.ville;
				result.add(photos);
			}
		}
		return result;
	}
	
	
	public List<Photos> fillGeocodageInfo(List<Photos> all) {
		List<Photos> result = new ArrayList<>();
		for (int i=0;i<all.size();i++) {
			Photos photos =all.get(i);
			System.out.println("On cherche les infos pour :" + photos.nom);
			// On regarde si on a l'info en bdd
			Photos nearestPhotoWithGeocoding = repositoryCustom.getNearestPhotoWithGeocoding(photos, 180);
			if (nearestPhotoWithGeocoding != null) {
				System.out.println("L'information a été trouvée en base " + nearestPhotoWithGeocoding);
				photos.pays = nearestPhotoWithGeocoding.pays;
				photos.ville = nearestPhotoWithGeocoding.ville;
				photos.region = nearestPhotoWithGeocoding.region;
				result.add(photos);
			} else {
				// On va chercher sur google
				System.out.println("On va chercher l'information sur locationiq....");
				try{
					CloseableHttpClient httpclient = HttpClients.createDefault();
					String url = "https://eu1.locationiq.com/v1/reverse.php?key="+getApiKey()+"&lat="+photos.lattitude+"&lon="+photos.longitude+"&format=json&accept-language=FR&normalizecity=1";
					HttpGet httpGet = new HttpGet(url);
					httpGet.addHeader("Accept-Language", "fr-FR,en");
					//System.out.println(url);
					ResponseHandler<String> handler = new BasicResponseHandler();
					HttpResponse response = httpclient.execute(httpGet);
					String body = handler.handleResponse(response);
					//System.out.println(body);
					Gson gson = new Gson();
					ReponseGeocoding fromJson = gson.fromJson(body, ReponseGeocoding.class);
					
					if(fromJson.getAddress().getCity()!=null){
						photos.ville=fromJson.getAddress().getCity();
						
					}else if(fromJson.getAddress().getCounty()!=null){
						photos.ville=fromJson.getAddress().getCounty();
					}else if(fromJson.getAddress().getVillage()!=null){
						photos.ville=fromJson.getAddress().getVillage();
					}
					
					
					
					photos.region=fromJson.getAddress().getState();
					photos.pays=fromJson.getAddress().getCountry();
					if(photos.pays!=null&&photos.pays.contains("??")){
						System.out.println("api epuise");
						return result;
					}
					System.out.println("On a trouve : "+photos.ville+", "+photos.region+", "+photos.pays);
					result.add(photos);
					Thread.sleep(1010);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			if(i%100==0){
				repository.saveAll(result);
				System.out.println("Sauvegarde partielle effectuée");
			}
		}

		return result;
	}
	
	
	public String getApiKey(){
		return "9b2d715df5569e";
	}
	
	
	
	
	

}