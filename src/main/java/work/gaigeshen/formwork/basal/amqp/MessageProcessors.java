package work.gaigeshen.formwork.basal.amqp;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

class MessageProcessors implements Iterable<MessageProcessor> {

    private final List<MessageProcessor> processors;

    public MessageProcessors(List<MessageProcessor> processors) {
        if (Objects.isNull(processors)) {
            throw new IllegalArgumentException("message processors cannot be null");
        }
        this.processors = processors;
    }

    public void process(String message, String messageType) throws Exception {
        if (Objects.isNull(message)) {
            throw new IllegalArgumentException("message cannot be null");
        }
        if (!processors.isEmpty()) {
            new InternalProcessorChain(processors).process(message, messageType);
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
        public void process(String message, String messageType) throws Exception {
            while (iterator.hasNext()) {
                iterator.next().process(message, messageType, this);
            }
        }
    }
}
