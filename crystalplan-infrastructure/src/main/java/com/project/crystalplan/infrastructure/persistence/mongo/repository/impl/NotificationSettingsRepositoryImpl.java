package com.project.crystalplan.infrastructure.persistence.mongo.repository.impl;

import com.project.crystalplan.domain.models.NotificationSettings;
import com.project.crystalplan.domain.repositories.NotificationSettingsRepository;
import com.project.crystalplan.infrastructure.persistence.mongo.document.NotificationSettingsDocument;
import com.project.crystalplan.infrastructure.persistence.mongo.mapper.NotificationSettingsMapper;
import com.project.crystalplan.infrastructure.persistence.mongo.repository.springdata.SpringDataNotificationSettingsMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class NotificationSettingsRepositoryImpl implements NotificationSettingsRepository {

    private final SpringDataNotificationSettingsMongoRepository springDataRepo;
    private final NotificationSettingsMapper mapper;

    @Autowired
    public NotificationSettingsRepositoryImpl(SpringDataNotificationSettingsMongoRepository springDataRepo,
                                              NotificationSettingsMapper mapper) {
        this.springDataRepo = springDataRepo;
        this.mapper = mapper;
    }

    @Override
    public NotificationSettings save(NotificationSettings settings) {
        NotificationSettingsDocument saved = springDataRepo.save(mapper.toDocument(settings));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<NotificationSettings> findById(String id) {
        return springDataRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<NotificationSettings> findByUserId(String userId) {
        return springDataRepo.findByUserId(userId).map(mapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        springDataRepo.deleteById(id);
    }
}
