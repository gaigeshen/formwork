package work.gaigeshen.formwork.commons.identity;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * 标识符生成器
 *
 * @author gaigeshen
 */
public interface IdentityCreator {

    /**
     * 默认的标识符生成器
     */
    IdentityCreator DEFAULT_CREATOR = props -> UUID.randomUUID().toString().replace("-", "");

    /**
     * 直接调用此方法生成默认的标识符
     *
     * @return 生成的标识符
     */
    static String createDefault() {
        return DEFAULT_CREATOR.create();
    }

    String create(Map<String, Object> properties);

    default String create() {
        return create(Collections.emptyMap());
    }
}
