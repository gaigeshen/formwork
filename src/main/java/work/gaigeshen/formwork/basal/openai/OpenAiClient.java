package work.gaigeshen.formwork.basal.openai;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import work.gaigeshen.formwork.basal.openai.parameters.chat.OpenAiChatCompletionsCreateParameters;
import work.gaigeshen.formwork.basal.openai.parameters.embedding.OpenAiEmbeddingsCreateParameters;
import work.gaigeshen.formwork.basal.openai.response.chat.OpenAiChatCompletionsCreateResponse;
import work.gaigeshen.formwork.basal.openai.response.embedding.OpenAiEmbeddingsCreateResponse;

/**
 *
 * @author gaigeshen
 */
public interface OpenAiClient {

    @POST("v1/chat/completions")
    Call<OpenAiChatCompletionsCreateResponse> createChatCompletion(@Body OpenAiChatCompletionsCreateParameters parameters);

    @POST("v1/embeddings")
    Call<OpenAiEmbeddingsCreateResponse> createEmbeddings(@Body OpenAiEmbeddingsCreateParameters parameters);
}
