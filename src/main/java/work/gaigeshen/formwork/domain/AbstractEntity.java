package work.gaigeshen.formwork.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 *
 * @author gaigeshen
 */
@EntityListeners({ EntityListener.class })
@MappedSuperclass
@Data
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    private Long createUserId;

    private Long updateUserId;

    @Version
    private Integer version;
}
