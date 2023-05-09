package work.gaigeshen.formwork.message;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 默认的消息处理器集合
 *
 * @author gaigeshen
 */
public class DefaultMessageProcessors implements MessageProcessors, Iterable<MessageProcessor> {

    private final List<MessageProcessor> processors;

    public DefaultMessageProcessors(List<MessageProcessor> processors) {
        if (Objects.isNull(processors)) {
            throw new IllegalArgumentException("message processors cannot be null");
        }
        this.processors = processors;
    }

    @Override
    public void process(String message, Map<String, Object> headers) throws MessageProcessingException {
        if (Objects.isNull(message) || Objects.isNull(headers)) {
            throw new IllegalArgumentException("message and headers cannot be null");
        }
        if (!processors.isEmpty()) {
            new InternalProcessorChain(processors).process(message, headers);
        }
    }

    @Override
    public Iterator<MessageProcessor> iterator() {
        return processors.iterator();
    }

    private static class InternalProcessorChain implements MessageProcessor.ProcessorChain {

        private final Iterator<MessageProcessor> iterator;

        private InternalProcessorChain(List<MessageProcessor> processors) {
            this.iterator = processors.iterator();
        }

        @Override
        public void process(String message, Map<String, Object> headers) throws MessageProcessingException {
            while (iterator.hasNext()) {
                iterator.next().process(message, headers, this);
            }
        }
    }
}
