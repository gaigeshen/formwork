package work.gaigeshen.formwork.basal.openai.parameters.chat;

import java.util.List;

/**
 *
 * @author gaigeshen
 */
public class OpenAiChatCompletionsCreateParameters {

    public String model;

    public List<Message> messages;

    public static class Message {

        public String role;

        public String content;
    }
}
