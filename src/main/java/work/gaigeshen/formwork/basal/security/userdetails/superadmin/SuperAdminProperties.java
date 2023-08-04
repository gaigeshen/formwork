package work.gaigeshen.formwork.basal.security.userdetails.superadmin;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 超级管理员配置
 *
 * @author gaigeshen
 */
@ConfigurationProperties("spring.security.admin")
public class SuperAdminProperties {

    private Boolean enabled;

    private String userName;

    private String password;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
