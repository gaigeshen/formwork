package work.gaigeshen.formwork.basal.openai;

/**
 *
 * @author gaigeshen
 */
public class OpenAiProperties {

    private final String serverHost;

    private final String apiKey;

    public OpenAiProperties(String serverHost, String apiKey) {
        this.serverHost = serverHost;
        this.apiKey = apiKey;
    }

    public String getServerHost() {
        return serverHost;
    }

    public String getApiKey() {
        return apiKey;
    }
}
