package work.gaigeshen.formwork.basal.openai.response.embedding;

import java.util.List;

/**
 *
 * @author gaigeshen
 */
public class OpenAiEmbeddingsCreateResponse {

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
