package work.gaigeshen.formwork.persistence;

import work.gaigeshen.formwork.security.SecurityContextUtils;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author gaigeshen
 */
public class EntityListener {

    @PrePersist
    public void prePersist(BaseEntity entity) {
        entity.setCreateTime(new Date());
        entity.setUpdateTime(entity.getCreateTime());

        String authorizationUserId = SecurityContextUtils.getAuthorizationUserId();
        if (Objects.nonNull(authorizationUserId)) {
            entity.setCreateUserId(Long.valueOf(authorizationUserId));
            entity.setUpdateUserId(entity.getCreateUserId());
        }
    }

    @PreUpdate
    public void preUpdate(BaseEntity entity) {
        entity.setUpdateTime(entity.getCreateTime());

        String authorizationUserId = SecurityContextUtils.getAuthorizationUserId();
        if (Objects.nonNull(authorizationUserId)) {
            entity.setUpdateUserId(Long.valueOf(authorizationUserId));
        }
    }
}
