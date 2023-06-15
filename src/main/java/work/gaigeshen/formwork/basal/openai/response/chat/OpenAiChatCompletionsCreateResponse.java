package work.gaigeshen.formwork.basal.openai.response.chat;

import java.util.List;

/**
 *
 * @author gaigeshen
 */
public class OpenAiChatCompletionsCreateResponse {

    public String id;

    public String object;

    public String model;

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
