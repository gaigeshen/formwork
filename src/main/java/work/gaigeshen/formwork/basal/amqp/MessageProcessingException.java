package work.gaigeshen.formwork.basal.amqp;

/**
 * 消息处理异常
 *
 * @author gaigeshen
 */
public class MessageProcessingException extends RuntimeException {

    public MessageProcessingException(String message) {
        super(message);
    }

    public MessageProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
