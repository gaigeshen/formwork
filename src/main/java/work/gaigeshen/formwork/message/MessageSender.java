package work.gaigeshen.formwork.message;

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
}
