package com.project.crystalplan.infrastructure.persistence.mongo.repository.impl;

import com.project.crystalplan.domain.models.User;
import com.project.crystalplan.domain.repositories.UserRepository;
import com.project.crystalplan.infrastructure.persistence.mongo.document.UserDocument;
import com.project.crystalplan.infrastructure.persistence.mongo.mapper.UserMapper;
import com.project.crystalplan.infrastructure.persistence.mongo.repository.springdata.SpringDataUserMongoRepository;
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
        UserDocument savedDoc = springDataRepo.save(mapper.toDocument(user));
        return mapper.toDomain(savedDoc);
    }

    @Override
    public Optional<User> findById(String id) {
        return springDataRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByIdAndActiveTrue(String id) {
        return springDataRepo.findByIdAndActiveTrue(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springDataRepo.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmailAndActiveTrue(String email) {
        return springDataRepo.findByEmailAndActiveTrue(email).map(mapper::toDomain);
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