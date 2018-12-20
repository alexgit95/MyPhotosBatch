package main.java.com.alex.batch.batchPhoto;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PhotosRepository extends MongoRepository<Photos, String>{
	 public Photos findByNom(String nom);
	 
}
