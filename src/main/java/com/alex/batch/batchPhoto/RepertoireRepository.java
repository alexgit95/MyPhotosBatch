package main.java.com.alex.batch.batchPhoto;

import org.springframework.data.mongodb.repository.MongoRepository;

import main.java.com.alex.batch.batchPhoto.model.Repertoire;

public interface RepertoireRepository extends MongoRepository<Repertoire, String> {

}
