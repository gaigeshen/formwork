package work.gaigeshen.formwork.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 默认的消息处理器
 *
 * @author gaigeshen
 */
public class DefaultMessageProcessor implements MessageProcessor {

    private static final Logger log = LoggerFactory.getLogger(DefaultMessageProcessor.class);

    @Override
    public void process(String message, Map<String, Object> headers, ProcessorChain chain) throws MessageProcessingException {
        log.info("default process message: [{}], headers: [{}]", message, headers);
    }
}
