package work.gaigeshen.formwork.basal.openai.parameters.chat;

import work.gaigeshen.formwork.basal.openai.parameters.OpenAiParameters;

import java.util.List;

/**
 *
 * @author gaigeshen
 */
public class OpenAiChatCompletionsCreateParameters extends OpenAiParameters {

    public String model;

    public List<Message> messages;

    public static class Message {

        public String role;

        public String content;
    }
}
