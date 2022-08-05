package org.littil.api.userSetting.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import java.util.List;
import java.util.UUID;

public class UserSettingRepository implements PanacheRepositoryBase<UserSettingEntity, UserSettingEntity.UserSettingId> {

    public List<UserSettingEntity> findAllByUserId(final UUID userId) {
        return find("user_id", userId).list();
    }
}
