package work.gaigeshen.formwork.basal.openai;

import java.net.Proxy;

/**
 *
 * @author gaigeshen
 */
public interface OpenAiClientCreator {

    OpenAiClient create(OpenAiProperties properties, Proxy proxy);

    default OpenAiClient create(OpenAiProperties properties) {
        return create(properties, null);
    }
}
