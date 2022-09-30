package work.gaigeshen.formwork.commons.persistence;

import java.util.Date;

/**
 *
 * @author gaigeshen
 */
public interface Model {

    Long getId();

    Date getCreateTime();

    Date getUpdateTime();

    Long getCreateUserId();

    Long getUpdateUserId();

    Integer getRevision();

    Boolean getDeleted();
}
