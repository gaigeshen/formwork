package work.gaigeshen.formwork.basal.openai.response.embeddings;

import work.gaigeshen.formwork.basal.openai.response.OpenAiResponse;

import java.util.List;

/**
 *
 * @author gaigeshen
 */
public class OpenAiEmbeddingsCreateResponse extends OpenAiResponse {

    public String object;

    public List<DataItem> data;

    public String model;

    public Usage usage;

    public static class DataItem {

        public String object;

        public List<Float> embedding;

        public Integer index;
    }

    public static class Usage {

        public Long prompt_tokens;

        public Long total_tokens;
    }
}
