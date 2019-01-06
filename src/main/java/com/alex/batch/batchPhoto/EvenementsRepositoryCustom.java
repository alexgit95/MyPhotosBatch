package main.java.com.alex.batch.batchPhoto;

import java.util.Date;

public interface EvenementsRepositoryCustom {
	
	/**
	 * Recupere un evenement lié à la photo par la date
	 * @param datePhoto
	 * @return
	 */
	Evenements searchEvenementByDate(Date datePhoto);
	
	boolean evenementInclutDansUnAutre(Evenements evt);
	
	Evenements searchEvenementByDateNotSameId(Evenements evt);
	
	
	
	Evenements findEvtWherePhotoisInclude(Photos p);
	
	Evenements findEvtWherePhotoisLink(Photos p);
	
	
}
