package work.gaigeshen.formwork.message;

import java.util.Map;

/**
 * 消息接收器
 *
 * @author gaigeshen
 */
public interface MessageReceiver {

    /**
     * 接收消息
     *
     * @param message 消息内容
     * @param headers 消息头
     * @throws MessageReceivingException 消息接收失败
     */
    void receiveMessage(String message, Map<String, Object> headers) throws MessageReceivingException;
}
