package main.java.com.alex.batch.batchPhoto;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import com.google.gson.Gson;

import main.java.com.alex.batch.batchPhoto.model.ReponseGeocoding;

@SpringBootApplication
public class Application implements CommandLineRunner {

	@Value("${locationiq.cle}")
	private String CLE_LOCATIONIQ;
	@Value("${analyse.repertoireRacine}")
	private String RACINE_ANALYSE;
	@Value("${backup.emplacement.sauvegarde}")
	private String REPETOIRE_BACKUP;
	@Value("${ifttt.cle}")
	private String CLE_IFTTT;
	@Value("${backup.only}")
	private boolean backupOnly;
	@Value("${backup.activation}")
	private boolean isBackupActivated;
	@Value("${backup.restore.activation}")
	private boolean restoreActivation;
	@Value("${backup.restore.repertoire}")
	private String restoreRepository;
	
	@Autowired
	private PhotosRepository repository;
	@Autowired
	private PhotosRepositoryCustom repositoryCustom;
	@Autowired
	private EvenementsRepository repositoryEvenements;
	@Autowired
	private EvenementsRepositoryCustom repositoryEvenementsCustom;
	
	public static final  int nbPhotoForEvt=20;
	
	public static final int nbVillesMaxParNOmEvt=5;
		
	public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	public static final SimpleDateFormat sdfBackup = new SimpleDateFormat("yyyyMMddhhmm");

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		
	}

	@Override
	public void run(String... args) throws Exception {
		if (isBackupActivated) {
			System.out.println("Demarrage du backup");
			Gson gson = new Gson();
			List<Photos> allPhotosToBackup = repository.findAll();
			List<Evenements> allEvtsToBackup = repositoryEvenements.findAll();
			File repoBackup = new File(REPETOIRE_BACKUP);
			String suffix = sdfBackup.format(new Date());
			try {
				FileUtils.write(new File(repoBackup, "photos" + suffix + ".json"), gson.toJson(allPhotosToBackup),Charset.forName("UTF-8"));
				FileUtils.write(new File(repoBackup, "evenements-photos" + suffix + ".json"),
						gson.toJson(allEvtsToBackup),Charset.forName("UTF-8"));
				notificationBackupOk();
			} catch (Exception e) {
				notificationBackupKO();
			}
			System.out.println("Fin du backup");
		}
		if(restoreActivation) {
			//TODO Implementer la restauration
		}
		
		if(backupOnly) {
			return;
		}
		
		
		System.out.println("--------Compte Rendu avant traitements---------");
		List<Photos> findPhotosWithGeoCoding = repositoryCustom.findPhotosWithGeoCoding();
		List<Photos> findPhotosWithNoGeocoding = repositoryCustom.findPhotosWithNoGeocoding();
		List<Photos> findPhotosWithNoGeolocalisation = repositoryCustom.findPhotosWithNoGeolocalisation();
		System.out.println("Photos sans geolocalisation : "+findPhotosWithNoGeolocalisation.size());
		System.out.println("Photos sans Geocodage : "+findPhotosWithNoGeocoding.size());
		System.out.println("Photos Geocodees : "+findPhotosWithGeoCoding.size());
		
		
		System.out.println("-----------------------------------------------");
		System.out.println("----------Ajout des nouveaux elements----------");
		List<File> allFichiers = chargerFichiers(RACINE_ANALYSE);
		System.out.println("---------------Début du filtrage----------------");
		List<File> fileFiltered=filterFichierDejaPresent(allFichiers);
		notificationNouvellesPhotos(fileFiltered.size());
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
		notificationResultat(findPhotosWithNoGeolocalisation.size(),findPhotosWithNoGeocoding.size(),findPhotosWithGeoCoding.size());
		
		
		
		
		gestionEvenements2(args);
		
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
				//System.out.println("Deja pr�sent on ne l'ajoute pas");
			}
		}
		System.out.println("NB elements � traiter : "+result.size());
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
				
				Photos build = pb.build();
				build.isScanEvenement=false;
				result.add(build);
				
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
		SimpleDateFormat sdf4= new SimpleDateFormat("'IMG_'yyyyMMdd'_'HHmmss");
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
				if(fichier.getName().startsWith("IMG_")) {
					return sdf4.parse(fichier.getName());
				}else {
					return sdf1.parse(fichier.getName());
				}
			
				
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
		int i=0;
		List<Photos> result = new ArrayList<>();
		for (Photos photos : all) {
			i++;
			Photos nearestPhoto = repositoryCustom.getNearestPhoto(photos, 15);
			if (nearestPhoto != null) {
				photos.lattitude = nearestPhoto.lattitude;
				photos.longitude = nearestPhoto.longitude;
				photos.pays=nearestPhoto.pays;
				photos.region=nearestPhoto.region;
				photos.ville=nearestPhoto.ville;
				result.add(photos);
			}
			if(i%100==0){
				System.out.println(i+"/"+all.size());
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
				System.out.println("L'information a �t� trouv�e en base " + nearestPhotoWithGeocoding);
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
			if(i%200==0){
				repository.saveAll(result);
				System.out.println("Sauvegarde partielle effectuée");
			}
		}

		return result;
	}
	
	
	public String getApiKey(){
		return CLE_LOCATIONIQ;
	}
	
	
	
	
	
	
	
	
	
	
	  private void notificationNouvellesPhotos(int nbNouvellesPhotos) throws UnsupportedEncodingException, IOException, ClientProtocolException {
          CloseableHttpClient httpclient = HttpClients.createDefault();
          final String url="https://maker.ifttt.com/trigger/nouvelles_photos/with/key/"+getApiKeyIFTTT()+"?value1="+nbNouvellesPhotos;
          HttpPost httpPost = new HttpPost(url);
          httpclient.execute(httpPost);
    }
	  
	  private void notificationBackupOk() throws UnsupportedEncodingException, IOException, ClientProtocolException {
          CloseableHttpClient httpclient = HttpClients.createDefault();
          final String url="https://maker.ifttt.com/trigger/backup_photo_ok/with/key/"+getApiKeyIFTTT();
          HttpPost httpPost = new HttpPost(url);
          httpclient.execute(httpPost);
    }
	  
	  private void notificationBackupKO() throws UnsupportedEncodingException, IOException, ClientProtocolException {
          CloseableHttpClient httpclient = HttpClients.createDefault();
          final String url="https://maker.ifttt.com/trigger/backup_photo_ko/with/key/"+getApiKeyIFTTT();
          HttpPost httpPost = new HttpPost(url);
          httpclient.execute(httpPost);
    }
	  
	  private void notificationRobotTermine() throws UnsupportedEncodingException, IOException, ClientProtocolException {
          CloseableHttpClient httpclient = HttpClients.createDefault();
          final String url="https://maker.ifttt.com/trigger/analyse_photo_ok/with/key/"+getApiKeyIFTTT();
          HttpPost httpPost = new HttpPost(url);
          httpclient.execute(httpPost);
    }

   

    private void notificationResultat(int nbPhotosSansGeolocalisation, int nbPhotosSansGecodage, int nbPhotosGeocode) throws UnsupportedEncodingException, IOException, ClientProtocolException {
          CloseableHttpClient httpclient = HttpClients.createDefault();
          final String url="https://maker.ifttt.com/trigger/resultatTraitementPhotos/with/key/"+getApiKeyIFTTT()+"?value1="+nbPhotosSansGeolocalisation+"&value2="+nbPhotosSansGecodage+"&value3="+nbPhotosGeocode;
          HttpPost httpPost = new HttpPost(url);
          httpclient.execute(httpPost);
    }


public String getApiKeyIFTTT() {
        return CLE_IFTTT;
}

public void gestionEvenements2(String... args) throws Exception {
	System.out.println("Recherche de toutes les photos, triees par date de prise de vue");
	List<Photos> allPhotos = repositoryCustom.findAllOrderByDate();
	final double nbPhotos = allPhotos.size();
	System.out.println("Fin de la recherche, photos a traiter : "+nbPhotos);
	for(int i=0;i<nbPhotos;i++){
		if(allPhotos.get(i).datePriseVue==null){
			continue;
		}
		if(allPhotos.get(i).evt!=null){
			//System.out.println(allPhotos.get(i).chemin+" a deja un evt, RAS");
			continue;
		}
		if(allPhotos.get(i).isScanEvenement) {
			continue;
		}
		
		Evenements findEvtWherePhotoisInclude = repositoryEvenementsCustom.findEvtWherePhotoisInclude(allPhotos.get(i));
		if(findEvtWherePhotoisInclude!=null){
			findEvtWherePhotoisInclude.fin=allPhotos.get(i).datePriseVue;
			repositoryEvenements.save(findEvtWherePhotoisInclude);
			//System.out.println(allPhotos.get(i).chemin+" est inclus dans "+findEvtWherePhotoisInclude.toString());
			continue;
		}
		Evenements findEvtWherePhotoisLink = repositoryEvenementsCustom.findEvtWherePhotoisLink(allPhotos.get(i));
		if(findEvtWherePhotoisLink!=null){
			findEvtWherePhotoisLink.fin=allPhotos.get(i).datePriseVue;
			repositoryEvenements.save(findEvtWherePhotoisLink);
			//System.out.println(allPhotos.get(i).chemin+" est lie a "+findEvtWherePhotoisLink);
			continue;
		}
		
		Evenements evenements = new Evenements();
		evenements.debut=allPhotos.get(i).datePriseVue;
		evenements.fin=allPhotos.get(i).datePriseVue;
		evenements.valid=false;
		repositoryEvenements.save(evenements);
		//System.out.println(allPhotos.get(i).chemin+" orphelin, creation"+evenements);
	}
	System.out.println("Fin du calcul des evenements");
	
	
	//On recupere tous les evenements
	List<Evenements> allEvts = repositoryEvenements.findAll();
	for (Evenements evenements : allEvts) {
		List<Photos> findPhotosBetweenTwoDates = repositoryCustom.findPhotosBetweenTwoDates(evenements.debut, evenements.fin);
		if(findPhotosBetweenTwoDates.size()>=nbPhotoForEvt){
			//System.out.println("L'evenement debutant le "+evenements.debut+" "+evenements.fin+" est conserve :"+findPhotosBetweenTwoDates.size());
			for(int i=0;i<findPhotosBetweenTwoDates.size();i++){
				findPhotosBetweenTwoDates.get(i).evt=evenements;
			}
			repository.saveAll(findPhotosBetweenTwoDates);
			
		}else{
			//System.out.println("On supprime l'evenement il n'a que "+findPhotosBetweenTwoDates.size()+" : "+evenements);
			repositoryEvenements.delete(evenements);
		}
		
	}
	List<Photos> findPhotosWithoutEvents = repositoryCustom.findPhotosWithoutEvents();
	
	final double sansEvtSize = findPhotosWithoutEvents.size();
	double pctEvt=100-((sansEvtSize/nbPhotos)*100);
	System.out.println("Il y a "+nbPhotos+" photos, il en reste "+sansEvtSize+" sans evenement, soit "+pctEvt+"% ok");
	
	
	//Generation de nom
	allEvts = repositoryEvenements.findAll();
	for(int i=0;i<allEvts.size();i++) {
		Calendar caldebut=Calendar.getInstance();
		caldebut.setTime(allEvts.get(i).debut);
		caldebut.add(Calendar.DAY_OF_MONTH, 5);
		if(allEvts.get(i).fin.after(caldebut.getTime())) {
			//On part sur des vacances
			List<Photos> findPhotosByEvenements = repositoryCustom.findPhotosByEvenements(allEvts.get(i));
			if("France".equalsIgnoreCase(findPhotosByEvenements.get(10).pays)) {
				allEvts.get(i).nom="Vacances à "+gestionVille(findPhotosByEvenements)+" du "+sdf.format(allEvts.get(i).debut)+" au "+sdf.format(allEvts.get(i).fin);
			
			}else{
				allEvts.get(i).nom="Vacances à "+findPhotosByEvenements.get(10).region+", "+findPhotosByEvenements.get(10).pays+" du "+sdf.format(allEvts.get(i).debut)+" au "+sdf.format(allEvts.get(i).fin);
			}
			System.out.println(allEvts.get(i).nom);
			continue;
		}
		
		//On essai un week end ou quelques jours
		caldebut.setTime(allEvts.get(i).debut);
		caldebut.add(Calendar.DAY_OF_MONTH, 2);
		if(allEvts.get(i).fin.after(caldebut.getTime())) {
			Calendar tempcal= Calendar.getInstance();
			tempcal.setTime(allEvts.get(i).fin);
			
			//On part sur quelques jours
			//On test si cela se termine sur un dimanche
			if(tempcal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY||tempcal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY) {
				List<Photos> findPhotosByEvenements = repositoryCustom.findPhotosByEvenements(allEvts.get(i));
				if("France".equalsIgnoreCase(findPhotosByEvenements.get(18).pays)) {
					allEvts.get(i).nom="Week end à "+gestionVille(findPhotosByEvenements)+" du "+sdf.format(allEvts.get(i).debut)+" au "+sdf.format(allEvts.get(i).fin);
				}else {
					allEvts.get(i).nom="Week end à "+gestionVille(findPhotosByEvenements)+", "+findPhotosByEvenements.get(18).pays+" du "+sdf.format(allEvts.get(i).debut)+" au "+sdf.format(allEvts.get(i).fin);
				}
				System.out.println(allEvts.get(i).nom);
				continue;
				
			}
			//Sinon quelques jours
			List<Photos> findPhotosByEvenements = repositoryCustom.findPhotosByEvenements(allEvts.get(i));
			allEvts.get(i).nom="Quelques jours à "+gestionVille(findPhotosByEvenements)+" du "+sdf.format(allEvts.get(i).debut)+" au "+sdf.format(allEvts.get(i).fin);
			System.out.println(allEvts.get(i).nom);
			continue;
		}
		
		
		//C'est peu de temps
		List<Photos> findPhotosByEvenements = repositoryCustom.findPhotosByEvenements(allEvts.get(i));
		if("France".equalsIgnoreCase(findPhotosByEvenements.get(18).pays)) {
			allEvts.get(i).nom="Sortie à "+gestionVille(findPhotosByEvenements)+" le "+sdf.format(allEvts.get(i).debut);
		}else {
			allEvts.get(i).nom="Sortie à "+gestionVille(findPhotosByEvenements)+", "+findPhotosByEvenements.get(18).pays+" le "+sdf.format(allEvts.get(i).debut);
		}
		System.out.println(allEvts.get(i).nom);
		
	}
	repositoryEvenements.saveAll(allEvts);
	
	
	//on passe le scan a true
	List<Photos> findAll = repository.findAll();
	for (Photos photos : findAll) {
		photos.isScanEvenement=true;
	}
	repository.saveAll(findAll);
	
	notificationRobotTermine();
	//FIN
}

public String gestionVille(List<Photos> allPhotos) {
	String result="";
	Set<String> villes= new HashSet<>();
	for(Photos p : allPhotos) {
		if(p.ville!=null) {
			villes.add(p.ville);
		}
	}
	int compteur=0;
	for (String string : villes) {
		if(compteur<=nbVillesMaxParNOmEvt) {
			result+=string+", ";
		}
		compteur++;
	}
	if(compteur>nbVillesMaxParNOmEvt) {
		result+="et quelques autres...";
	}
	
	
	return result;
}
	
	

}