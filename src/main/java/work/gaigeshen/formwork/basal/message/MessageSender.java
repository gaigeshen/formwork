package work.gaigeshen.formwork.basal.message;

import java.time.Duration;
import java.util.Map;

/**
 * 消息发送器
 *
 * @author gaigeshen
 */
public interface MessageSender {

    /**
     * 发送消息
     *
     * @param queue 目标队列
     * @param message 消息内容
     * @param headers 消息头
     * @throws MessageSendingException 消息发送失败
     */
    void sendMessage(String queue, String message, Map<String, Object> headers) throws MessageSendingException;

    /**
     * 发送延迟消息
     *
     * @param queue 目标队列
     * @param message 消息内容
     * @param headers 消息头
     * @param duration 延迟时间
     * @throws MessageSendingException 消息发送失败
     */
    void sendDelayMessage(String queue, String message, Map<String, Object> headers, Duration duration) throws MessageSendingException;
}
