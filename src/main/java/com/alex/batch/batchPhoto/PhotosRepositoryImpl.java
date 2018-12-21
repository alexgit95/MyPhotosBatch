package main.java.com.alex.batch.batchPhoto;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class PhotosRepositoryImpl implements PhotosRepositoryCustom {
	
    private MongoTemplate mongoTemplate;
	
	@Override
	public List<Photos> findPhotosWithNoGeocoding() {
		Query query = new Query(Criteria.where("longitude").ne(0));
		query.addCriteria(Criteria.where("lattitude").ne(0));
		query.addCriteria(Criteria.where("pays").is(null));
		List<Photos> find = mongoTemplate.find(query, Photos.class);
		//System.out.println(find);
		return find;
	}
	
	@Autowired
	public PhotosRepositoryImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	

	@Override
	public List<Photos> findPhotosWithNoGeolocalisation() {
		Query query = new Query(Criteria.where("longitude").is(0));
		query.addCriteria(Criteria.where("lattitude").is(0));
		
		List<Photos> find = mongoTemplate.find(query, Photos.class);
		
		//System.out.println(find);
		return find;
	}

	@Override
	public List<Photos> findPhotosWithGeoCoding() {
		Query query = new Query(Criteria.where("pays").ne(null));
		List<Photos> find = mongoTemplate.find(query, Photos.class);
		
		//System.out.println(find);
		return find;
	}

	@Override
	public Photos getNearestPhoto(Photos p, int pas) {
		Photos result=null;
		int nbTentatives=1;
		while(result==null&&nbTentatives<pas&&p.datePriseVue!=null) {
			Calendar avant = Calendar.getInstance();
			avant.setTime(p.datePriseVue);
			avant.add(Calendar.MINUTE, nbTentatives*-1);
			Date dateMin = avant.getTime();
			
			Calendar apres = Calendar.getInstance();
			apres.setTime(p.datePriseVue);
			apres.add(Calendar.MINUTE, nbTentatives);
			Date dateMax = apres.getTime();
			
			//System.out.println("Recherche "+dateMin+" - "+dateMax);
			
			Query query = new Query(Criteria.where("datePriseVue").gt(dateMin)
					.andOperator(Criteria.where("datePriseVue").lt(dateMax), 
							Criteria.where("longitude").ne(0), 
							Criteria.where("lattitude").ne(0)));
			
			
			List<Photos> find = mongoTemplate.find(query, Photos.class);
			int nbresult = find.size();
			if(nbresult>0) {
				//System.out.println("Avec pas à "+nbTentatives+" on a trouve "+nbresult);
				result=find.get(0);
			}else {
				//System.out.println("Rien a été trouve avec un pas à "+nbTentatives+" pour "+p);
				nbTentatives++;
			}
		}
		/*List<Integer> findDistinct = mongoTemplate.findDistinct(new Query(Criteria.where("longitude").ne(0)), "annee", Photos.class, Integer.class);
		System.out.println("Alex : "+findDistinct);*/
		return result;
	}

	@Override
	public Photos getNearestPhotoWithGeocoding(Photos p, int pas) {
		if(p.datePriseVue==null){
			return null;
		}
		Calendar avant = Calendar.getInstance();
		avant.setTime(p.datePriseVue);
		avant.add(Calendar.SECOND, pas*-1);
		Date dateMin = avant.getTime();
		
		Calendar apres = Calendar.getInstance();
		apres.setTime(p.datePriseVue);
		apres.add(Calendar.SECOND, pas);
		Date dateMax = apres.getTime();
		Query query = new Query(Criteria.where("datePriseVue").gt(dateMin)
				.andOperator(Criteria.where("datePriseVue").lt(dateMax), 
						Criteria.where("pays").ne(null), 
						Criteria.where("region").ne(null)));
		
		
		List<Photos> find = mongoTemplate.find(query, Photos.class);
		int nbresult = find.size();
		if(nbresult>0) {
			return find.get(0);
		}else {
			return null;
		}
	
	}

	@Override
	public Photos findOnePhotosByNom(String nom) {
		Query query = new Query(Criteria.where("nom").is(nom));
		List<Photos> find = mongoTemplate.find(query, Photos.class);
		if(find.size()>0){
			return find.get(0);
		}
		return null;
		
	}

	

}
