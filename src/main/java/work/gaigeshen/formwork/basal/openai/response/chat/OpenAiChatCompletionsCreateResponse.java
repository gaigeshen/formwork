package work.gaigeshen.formwork.basal.openai.response.chat;

import work.gaigeshen.formwork.basal.openai.response.OpenAiResponse;

import java.util.List;

/**
 *
 * @author gaigeshen
 */
public class OpenAiChatCompletionsCreateResponse extends OpenAiResponse {

    public String id;

    public String object;

    public Long created;

    public List<Choice> choices;

    public Usage usage;

    public static class Choice {

        public Integer index;

        public ChoiceMessage message;

        public String finish_reason;
    }

    public static class ChoiceMessage {

        public String role;

        public String content;
    }

    public static class Usage {

        public Long prompt_tokens;

        public Long completion_tokens;

        public Long total_tokens;
    }
}
