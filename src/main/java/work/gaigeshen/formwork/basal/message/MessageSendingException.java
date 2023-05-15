package work.gaigeshen.formwork.basal.message;

/**
 * 消息发送异常
 *
 * @author gaigeshen
 */
public class MessageSendingException extends RuntimeException {

    public MessageSendingException(String message) {
        super(message);
    }

    public MessageSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
