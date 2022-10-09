package work.gaigeshen.formwork.persistence;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 *
 * @author gaigeshen
 */
@Getter
@Setter(AccessLevel.PACKAGE)
@EntityListeners(EntityListener.class)
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Date createTime;

    @Column
    private Date updateTime;

    @Column
    private Long createUserId;

    @Column
    private Long updateUserId;

    @Version
    private Integer revision;
}
