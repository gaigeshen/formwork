package work.gaigeshen.formwork.message;

import java.util.Map;

/**
 * 消息处理器
 *
 * @author gaigeshen
 */
public interface MessageProcessor {

    /**
     * 处理消息
     *
     * @param message 消息内容
     * @param headers 消息头
     * @param chain 消息处理器链
     * @throws MessageProcessingException 处理消息失败
     */
    void process(String message, Map<String, Object> headers, ProcessorChain chain) throws MessageProcessingException;

    /**
     * 消息处理器链
     *
     * @author gaigeshen
     */
    interface ProcessorChain {

        /**
         * 处理消息
         *
         * @param message 消息内容
         * @param headers 消息头
         * @throws MessageProcessingException 处理消息失败
         */
        void process(String message, Map<String, Object> headers) throws MessageProcessingException;
    }
}
