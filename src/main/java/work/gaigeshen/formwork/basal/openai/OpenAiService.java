package work.gaigeshen.formwork.basal.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import work.gaigeshen.formwork.basal.openai.parameters.chat.OpenAiChatCompletionsCreateParameters;
import work.gaigeshen.formwork.basal.openai.parameters.embeddings.OpenAiEmbeddingsCreateParameters;
import work.gaigeshen.formwork.basal.openai.response.chat.OpenAiChatCompletionsCreateResponse;
import work.gaigeshen.formwork.basal.openai.response.embeddings.OpenAiEmbeddingsCreateResponse;

import java.util.ArrayList;

public class OpenAiService {

    private final String serverHost;

    private final String apiKey;

    private final String organization;

    private final OpenAiClient openAiClient;

    public OpenAiService(String serverHost, String apiKey, String organization) {
        this.serverHost = serverHost;
        this.apiKey = apiKey;
        this.organization = organization;
        this.openAiClient = createOpenAiClient();
    }

    public static void main(String[] args) throws Exception {
        OpenAiChatCompletionsCreateParameters parameters = new OpenAiChatCompletionsCreateParameters();
        parameters.messages = new ArrayList<>();
        parameters.model = "";
        OpenAiClient openAiClient1 = new OpenAiService("https://api.openai.com/v1/", "xxx", "yyy").openAiClient;
        Call<OpenAiChatCompletionsCreateResponse> chatCompletion = openAiClient1.createChatCompletion(parameters);
        System.out.println(chatCompletion.execute().body());
    }

    private OpenAiClient createOpenAiClient() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(chain -> {
            Request request = chain.request().newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("OpenAI-Organization", organization)
                    .build();
            return chain.proceed(request);
        });
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(System.out::println);
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClientBuilder.addInterceptor(loggingInterceptor);


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverHost)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(httpClientBuilder.build())
                .build();

        return retrofit.create(OpenAiClient.class);
    }

    interface OpenAiClient {

        @POST("chat/completions")
        Call<OpenAiChatCompletionsCreateResponse> createChatCompletion(@Body OpenAiChatCompletionsCreateParameters parameters);

        @POST("chat/completions")
        Call<OpenAiEmbeddingsCreateResponse> createEmbeddings(@Body OpenAiEmbeddingsCreateParameters parameters);
    }
}
