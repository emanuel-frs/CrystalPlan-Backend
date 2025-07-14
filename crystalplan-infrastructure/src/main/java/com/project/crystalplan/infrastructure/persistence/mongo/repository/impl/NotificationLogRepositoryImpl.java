package com.project.crystalplan.infrastructure.persistence.mongo.repository.impl;

import com.project.crystalplan.domain.enums.NotificationStatus;
import com.project.crystalplan.domain.models.NotificationLog;
import com.project.crystalplan.domain.repositories.NotificationLogRepository;
import com.project.crystalplan.infrastructure.persistence.mongo.repository.springdata.SpringDataNotificationLogMongoRepository;
import com.project.crystalplan.infrastructure.persistence.mongo.document.NotificationLogDocument;
import com.project.crystalplan.infrastructure.persistence.mongo.mapper.NotificationLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class NotificationLogRepositoryImpl implements NotificationLogRepository {

    private final SpringDataNotificationLogMongoRepository springDataRepo;
    private final NotificationLogMapper mapper;

    @Autowired
    public NotificationLogRepositoryImpl(SpringDataNotificationLogMongoRepository springDataRepo,
                                         NotificationLogMapper mapper) {
        this.springDataRepo = springDataRepo;
        this.mapper = mapper;
    }

    @Override
    public NotificationLog save(NotificationLog notificationLog) {
        NotificationLogDocument document = mapper.toDocument(notificationLog);
        NotificationLogDocument saved = springDataRepo.save(document);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<NotificationLog> findById(String id) {
        return springDataRepo.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<NotificationLog> findByUserId(String userId) {
        return springDataRepo.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationLog> findByEventId(String eventId) {
        return springDataRepo.findByEventId(eventId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationLog> findByStatus(NotificationStatus status) {
        return springDataRepo.findByStatus(status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        springDataRepo.deleteById(id);
    }
}