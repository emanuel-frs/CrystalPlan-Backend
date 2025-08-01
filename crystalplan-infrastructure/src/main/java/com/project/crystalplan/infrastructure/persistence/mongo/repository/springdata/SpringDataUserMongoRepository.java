package com.project.crystalplan.infrastructure.persistence.mongo.repository.springdata;

import com.project.crystalplan.infrastructure.persistence.mongo.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SpringDataUserMongoRepository extends MongoRepository<UserDocument, String> {
    Optional<UserDocument> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<UserDocument> findByIdAndActiveTrue(String id);
    Optional<UserDocument> findByEmailAndActiveTrue(String email);
}