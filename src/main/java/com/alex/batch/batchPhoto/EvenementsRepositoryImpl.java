package main.java.com.alex.batch.batchPhoto;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class EvenementsRepositoryImpl  implements EvenementsRepositoryCustom{

	 private MongoTemplate mongoTemplate;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Evenements searchEvenementByDate(Date datePhoto) {
		
		
		Calendar avant = Calendar.getInstance();
		avant.setTime(datePhoto);
		avant.add(Calendar.DAY_OF_MONTH, -1);
		Date dateMin = avant.getTime();
		
		Calendar apres = Calendar.getInstance();
		apres.setTime(datePhoto);
		apres.add(Calendar.DAY_OF_MONTH, 1);
		Date dateMax = apres.getTime();
		
		
		Query query = new Query(Criteria.where("debut").gt(dateMin)
				.andOperator(Criteria.where("fin").lt(dateMax)));
		
		
		List<Evenements> find = mongoTemplate.find(query, Evenements.class);
		int nbresult = find.size();
		if(nbresult>0) {
			return find.get(0);
		}else {
			return null;
		}
		
	}
	
	
	
	
	
	
	
	
	
	@Autowired
	public EvenementsRepositoryImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}









	@Override
	public boolean evenementInclutDansUnAutre(Evenements evt) {
		Query query = new Query(Criteria.where("debut").lt(evt.debut)
				.andOperator(Criteria.where("fin").gt(evt.fin)));
		
		List<Evenements> find = mongoTemplate.find(query, Evenements.class);
		if(find==null||find.size()==0) {
			return false;
		}
		for (Evenements evenements : find) {
			if(!evt.id.equals(evenements.id)) {
				return true;
			}
		}
		return false;
	}









	@Override
	public Evenements searchEvenementByDateNotSameId(Evenements evt) {

		Calendar avant = Calendar.getInstance();
		avant.setTime(evt.debut);
		avant.add(Calendar.DAY_OF_MONTH, -1);
		Date dateMin = avant.getTime();
		
		Calendar apres = Calendar.getInstance();
		apres.setTime(evt.debut);
		apres.add(Calendar.DAY_OF_MONTH, 1);
		Date dateMax = apres.getTime();
		
		
		Query query = new Query(Criteria.where("debut").gt(dateMin)
				.andOperator(Criteria.where("debut").lt(dateMax),Criteria.where("_id").ne(evt.id)));
		
		
		List<Evenements> find = mongoTemplate.find(query, Evenements.class);
		int nbresult = find.size();
		if(nbresult>0) {
			return find.get(0);
		}else {
			return null;
		}
	}

}
