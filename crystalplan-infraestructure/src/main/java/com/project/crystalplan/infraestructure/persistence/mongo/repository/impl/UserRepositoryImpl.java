package com.project.crystalplan.infraestructure.persistence.mongo.repository.impl;

import com.project.crystalplan.domain.models.User;
import com.project.crystalplan.domain.repositories.UserRepository;
import com.project.crystalplan.infraestructure.persistence.mongo.repository.springdata.SpringDataUserMongoRepository;
import com.project.crystalplan.infraestructure.persistence.mongo.document.UserDocument;
import com.project.crystalplan.infraestructure.persistence.mongo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final SpringDataUserMongoRepository springDataRepo;
    private final UserMapper mapper;

    @Autowired
    public UserRepositoryImpl(SpringDataUserMongoRepository springDataRepo, UserMapper mapper) {
        this.springDataRepo = springDataRepo;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserDocument doc = mapper.toDocument(user);
        return mapper.toDomain(springDataRepo.save(doc));
    }

    @Override
    public Optional<User> findById(String id) {
        return springDataRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springDataRepo.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataRepo.existsByEmail(email);
    }

    @Override
    public void deleteById(String id) {
        springDataRepo.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return springDataRepo.existsById(id);
    }
}

