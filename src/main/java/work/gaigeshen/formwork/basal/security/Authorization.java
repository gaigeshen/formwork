package work.gaigeshen.formwork.basal.security;

import java.io.Serializable;
import java.util.Set;

/**
 * 授权信息
 *
 * @author gaigeshen
 */
public interface Authorization extends Serializable {

    /**
     * 返回用户标识符
     *
     * @return 用户标识符
     */
    String getUserId();

    /**
     * 返回用户名称
     *
     * @return 用户名称
     */
    String getUsername();

    /**
     * 返回用户授权信息明细
     *
     * @return 用户授权信息明细
     */
    Set<String> getAuthorities();

    /**
     * 返回此授权信息的用途，可以用来标识不同的认证来源
     *
     * @return 此授权信息的用途
     */
    default String getPurpose() {
        return "default";
    }
}
