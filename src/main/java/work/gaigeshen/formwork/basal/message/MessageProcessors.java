package work.gaigeshen.formwork.basal.message;

import java.util.Map;

/**
 * 消息处理器集合
 *
 * @author gaigeshen
 */
public interface MessageProcessors {

    /**
     * 处理消息
     *
     * @param message 消息内容
     * @param headers 消息头
     * @throws MessageProcessingException 处理消息失败
     */
    void process(String message, Map<String, Object> headers) throws MessageProcessingException;
}
