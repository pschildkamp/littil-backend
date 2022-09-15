package org.littil.api.userSetting.service;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.littil.api.userSetting.repository.UserSettingEntity;
import org.littil.api.userSetting.repository.UserSettingRepository;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@QuarkusTest
class UserSettingServiceTest {

    @Inject
    UserSettingService service;

    @InjectMock
    UserSettingRepository repository;

    @InjectMock
    UserSettingMapper mapper;


    @Test
    void givenFindAllByUserId_thenShouldReturnUserSetting() {
        final UUID userId = UUID.randomUUID();
        final String key = RandomStringUtils.randomAlphabetic(5);
        final String value = RandomStringUtils.randomAlphabetic(10);

        final UserSettingEntity expectedUserSetting = new UserSettingEntity(userId, key, value);
        final UserSetting mappedUserSetting = new UserSetting(key, value);

        doReturn(List.of(expectedUserSetting)).when(repository).findAllByUserId(userId);
        doReturn(mappedUserSetting).when(mapper).toDomain(expectedUserSetting);

        final List<UserSetting> userSettings = service.findAll(userId);

        assertEquals(List.of(mappedUserSetting), userSettings);
    }

    @Test
    void givenWhenGettingUserSettingByKey_thenShouldReturnUserSetting() {
        final UUID userId = UUID.randomUUID();
        final String key = RandomStringUtils.randomAlphabetic(5);
        final String value = RandomStringUtils.randomAlphabetic(10);

        final UserSettingEntity expectedUserSettingEntity = new UserSettingEntity(userId, key, value);
        final UserSetting mappedUserSetting = new UserSetting(key, value);

        doReturn(Optional.of(expectedUserSettingEntity)).when(repository).findByIdOptional(new UserSettingEntity.UserSettingId(userId, key));
        doReturn(mappedUserSetting).when(mapper).toDomain(expectedUserSettingEntity);

        final Optional<UserSetting> userSetting = service.getUserSettingByKey(key, userId);

        assertThat(userSetting).isPresent();
        assertEquals(mappedUserSetting, userSetting.get());
    }

    @Test
    void givenWhenGettingUserSettingByKeyWithNonMatchingUserId_thenShouldNotFound() {
        final UUID userId = UUID.randomUUID();
        final String key = RandomStringUtils.randomAlphabetic(5);

        doReturn(Optional.empty()).when(repository).findByIdOptional(new UserSettingEntity.UserSettingId(userId, key));
        verifyNoMoreInteractions(mapper);

        final Optional<UserSetting> userSetting = service.getUserSettingByKey(key, userId);

        assertThat(userSetting).isNotPresent();
    }

    @Test
    void givenWhenUpdatingUserSetting_thenShouldUpdateAndReturnUpdatedDto() {
        final UUID userId = UUID.randomUUID();
        final String key = RandomStringUtils.randomAlphabetic(5);

        doReturn(Optional.empty()).when(repository).findByIdOptional(new UserSettingEntity.UserSettingId(userId, key));
        verifyNoMoreInteractions(mapper);

        final Optional<UserSetting> userSetting = service.getUserSettingByKey(key, userId);

        assertThat(userSetting).isNotPresent();
    }

    @Test
    void givenDeleteUserSetting_thenShouldDeleteUserSetting() {
        final UUID userId = UUID.randomUUID();
        final String key = RandomStringUtils.randomAlphabetic(5);
        final String value = RandomStringUtils.randomAlphabetic(10);

        final UserSettingEntity userSettingEntity = new UserSettingEntity(userId, key, value);

        doReturn(Optional.of(userSettingEntity)).when(repository)
                .findByIdOptional(new UserSettingEntity.UserSettingId(userId, key));
        service.delete(key, userId);
        then(repository).should().delete(userSettingEntity);
    }

    @Test
    void givenDeleteUnknownUserSetting_thenShouldThrowNotFoundException() {
        final UUID userId = UUID.randomUUID();
        final String key = RandomStringUtils.randomAlphabetic(5);

        doReturn(Optional.empty()).when(repository)
                .findByIdOptional(new UserSettingEntity.UserSettingId(userId, key));
        verifyNoMoreInteractions(repository);

        assertThrows(NotFoundException.class, () -> service.delete(key, userId));
    }

    @Test
    void givenSaveUserSettingPersistenceFails_thenShouldThrowPersistenceException() {
        final UUID userId = UUID.randomUUID();
        final String key = RandomStringUtils.randomAlphabetic(5);
        final String value = RandomStringUtils.randomAlphabetic(10);
        final UserSetting userSetting = new UserSetting(key, value);
        final UserSettingEntity userSettingEntity = new UserSettingEntity(userId, key, value);

        doReturn(userSettingEntity).when(mapper).toEntity(userSetting, userId);
        doReturn(false).when(repository).isPersistent(userSettingEntity);
        verifyNoMoreInteractions(mapper);

        assertThrows(PersistenceException.class, () -> service.save(userSetting, userId));
        verify(repository).persist(userSettingEntity);
    }

}