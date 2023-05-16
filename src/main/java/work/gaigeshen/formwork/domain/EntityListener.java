package work.gaigeshen.formwork.domain;

import work.gaigeshen.formwork.basal.security.SecurityUtils;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;
import java.util.Optional;

/**
 *
 * @author gaigeshen
 */
public class EntityListener {

    @PrePersist
    public void prePersist(AbstractEntity entity) {

        entity.setCreateTime(new Date());
        entity.setUpdateTime(entity.getCreateTime());

        Optional<Long> userIdAsNumber = SecurityUtils.getUserIdAsNumber();
        if (userIdAsNumber.isPresent()) {
            entity.setCreateUserId(userIdAsNumber.get());
            entity.setUpdateUserId(entity.getCreateUserId());
        }
    }

    @PreUpdate
    public void preUpdate(AbstractEntity entity) {

        entity.setUpdateTime(new Date());

        SecurityUtils.getUserIdAsNumber().ifPresent(entity::setUpdateUserId);
    }
}
