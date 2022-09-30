package work.gaigeshen.formwork.commons.persistence;

import java.util.Date;

/**
 *
 * @author gaigeshen
 */
public class BaseModel implements Model {

    private Long id;

    private Date createTime;

    private Date updateTime;

    private Long createUserId;

    private Long updateUserId;

    private Integer revision;

    private Boolean deleted;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public Long getCreateUserId() {
        return createUserId;
    }

    @Override
    public Long getUpdateUserId() {
        return updateUserId;
    }

    @Override
    public Integer getRevision() {
        return revision;
    }

    @Override
    public Boolean getDeleted() {
        return deleted;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
