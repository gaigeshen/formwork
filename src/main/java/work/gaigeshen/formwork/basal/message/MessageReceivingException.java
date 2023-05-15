package work.gaigeshen.formwork.basal.message;

/**
 * 消息接收异常
 *
 * @author gaigeshen
 */
public class MessageReceivingException extends RuntimeException {

    public MessageReceivingException(String message) {
        super(message);
    }

    public MessageReceivingException(String message, Throwable cause) {
        super(message, cause);
    }
}
