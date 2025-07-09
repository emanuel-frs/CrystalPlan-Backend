package com.project.crystalplan.infraestructure.persistence.mongo.repository.springdata;

import com.project.crystalplan.infraestructure.persistence.mongo.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SpringDataUserMongoRepository extends MongoRepository<UserDocument, String> {
    Optional<UserDocument> findByEmail(String email);
    boolean existsByEmail(String email);
}

