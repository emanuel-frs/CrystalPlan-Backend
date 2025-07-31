package com.project.crystalplan.infrastructure.persistence.mongo.repository.impl;

import com.project.crystalplan.domain.models.User;
import com.project.crystalplan.infrastructure.persistence.mongo.document.UserDocument;
import com.project.crystalplan.infrastructure.persistence.mongo.mapper.UserMapper;
import com.project.crystalplan.infrastructure.persistence.mongo.repository.springdata.SpringDataUserMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Habilita o uso de Mockito no JUnit 5
class UserRepositoryImplTest {

    @Mock // Mocka a interface SpringDataUserMongoRepository
    private SpringDataUserMongoRepository springDataRepo;

    @Mock // Mocka a interface UserMapper
    private UserMapper mapper;

    @InjectMocks // Injeta os mocks acima nesta instância
    private UserRepositoryImpl userRepository;

    // Dados de exemplo para usar nos testes
    private User sampleUser;
    private UserDocument sampleUserDocument;

    @BeforeEach
    void setUp() {
        // Inicializa os objetos de exemplo para cada teste
        sampleUser = new User("user-id-1", "John Doe", "john.doe@example.com", "password123");
        sampleUserDocument = new UserDocument("user-id-1", "John Doe", "john.doe@example.com", "password123");

        // *** AS LINHAS DE STUBBING DO MAPPER FORAM REMOVIDAS DAQUI ***
        // *** E MOVIDAS PARA OS TESTES INDIVIDUAIS ONDE SÃO USADAS. ***
    }

    @Test
    void save_ShouldSaveUserAndReturnDomainObject() {
        // Stubs específicos para este teste
        when(mapper.toDocument(any(User.class))).thenReturn(sampleUserDocument);
        when(mapper.toDomain(any(UserDocument.class))).thenReturn(sampleUser);
        when(springDataRepo.save(any(UserDocument.class))).thenReturn(sampleUserDocument);

        // Ação: Chamar o método save do repositório
        User savedUser = userRepository.save(sampleUser);

        // Verificação:
        verify(mapper, times(1)).toDocument(sampleUser);
        verify(springDataRepo, times(1)).save(sampleUserDocument);
        verify(mapper, times(1)).toDomain(sampleUserDocument);
        assertEquals(sampleUser, savedUser);
    }

    @Test
    void findById_ShouldReturnUserWhenFound() {
        // Stubs específicos para este teste
        when(mapper.toDomain(any(UserDocument.class))).thenReturn(sampleUser);
        when(springDataRepo.findById("user-id-1")).thenReturn(Optional.of(sampleUserDocument));

        // Ação: Chamar o método findById do repositório
        Optional<User> foundUser = userRepository.findById("user-id-1");

        // Verificação:
        verify(springDataRepo, times(1)).findById("user-id-1");
        verify(mapper, times(1)).toDomain(sampleUserDocument);
        assertTrue(foundUser.isPresent());
        assertEquals(sampleUser, foundUser.get());
    }

    @Test
    void findById_ShouldReturnEmptyOptionalWhenNotFound() {
        // Stubs específicos para este teste (mapper.toDomain NÃO deve ser chamado)
        when(springDataRepo.findById("non-existent-id")).thenReturn(Optional.empty());

        // Ação: Chamar o método findById do repositório
        Optional<User> foundUser = userRepository.findById("non-existent-id");

        // Verificação:
        verify(springDataRepo, times(1)).findById("non-existent-id");
        verify(mapper, never()).toDomain(any(UserDocument.class)); // Verifique que o toDomain não foi chamado
        assertFalse(foundUser.isPresent());
    }

    @Test
    void findByEmail_ShouldReturnUserWhenFound() {
        // Stubs específicos para este teste
        when(mapper.toDomain(any(UserDocument.class))).thenReturn(sampleUser);
        when(springDataRepo.findByEmail("john.doe@example.com")).thenReturn(Optional.of(sampleUserDocument));

        // Ação: Chamar o método findByEmail do repositório
        Optional<User> foundUser = userRepository.findByEmail("john.doe@example.com");

        // Verificação:
        verify(springDataRepo, times(1)).findByEmail("john.doe@example.com");
        verify(mapper, times(1)).toDomain(sampleUserDocument);
        assertTrue(foundUser.isPresent());
        assertEquals(sampleUser, foundUser.get());
    }

    @Test
    void findByEmail_ShouldReturnEmptyOptionalWhenNotFound() {
        // Stubs específicos para este teste (mapper.toDomain NÃO deve ser chamado)
        when(springDataRepo.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Ação: Chamar o método findByEmail do repositório
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Verificação:
        verify(springDataRepo, times(1)).findByEmail("nonexistent@example.com");
        verify(mapper, never()).toDomain(any(UserDocument.class)); // Verifique que o toDomain não foi chamado
        assertFalse(foundUser.isPresent());
    }

    @Test
    void existsByEmail_ShouldReturnTrueWhenExists() {
        // Stubs específicos para este teste (mapper não é usado neste método)
        when(springDataRepo.existsByEmail("john.doe@example.com")).thenReturn(true);

        // Ação: Chamar o método existsByEmail do repositório
        boolean exists = userRepository.existsByEmail("john.doe@example.com");

        // Verificação:
        verify(springDataRepo, times(1)).existsByEmail("john.doe@example.com");
        assertTrue(exists);
        verifyNoMoreInteractions(mapper); // Garante que o mapper não foi chamado
    }

    @Test
    void existsByEmail_ShouldReturnFalseWhenNotExists() {
        // Stubs específicos para este teste (mapper não é usado neste método)
        when(springDataRepo.existsByEmail("nonexistent@example.com")).thenReturn(false);

        // Ação: Chamar o método existsByEmail do repositório
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Verificação:
        verify(springDataRepo, times(1)).existsByEmail("nonexistent@example.com");
        assertFalse(exists);
        verifyNoMoreInteractions(mapper); // Garante que o mapper não foi chamado
    }

    @Test
    void deleteById_ShouldCallSpringDataDeleteById() {
        // Stubs específicos para este teste (mapper não é usado neste método)
        // Ação: Chamar o método deleteById do repositório
        userRepository.deleteById("user-id-1");

        // Verificação:
        verify(springDataRepo, times(1)).deleteById("user-id-1");
        verifyNoMoreInteractions(mapper); // Garante que o mapper não foi chamado
    }

    @Test
    void existsById_ShouldReturnTrueWhenExists() {
        // Stubs específicos para este teste (mapper não é usado neste método)
        when(springDataRepo.existsById("user-id-1")).thenReturn(true);

        // Ação: Chamar o método existsById do repositório
        boolean exists = userRepository.existsById("user-id-1");

        // Verificação:
        verify(springDataRepo, times(1)).existsById("user-id-1");
        assertTrue(exists);
        verifyNoMoreInteractions(mapper); // Garante que o mapper não foi chamado
    }

    @Test
    void existsById_ShouldReturnFalseWhenNotExists() {
        // Stubs específicos para este teste (mapper não é usado neste método)
        when(springDataRepo.existsById("non-existent-id")).thenReturn(false);

        // Ação: Chamar o método existsById do repositório
        boolean exists = userRepository.existsById("non-existent-id");

        // Verificação:
        verify(springDataRepo, times(1)).existsById("non-existent-id");
        assertFalse(exists);
        verifyNoMoreInteractions(mapper); // Garante que o mapper não foi chamado
    }
}