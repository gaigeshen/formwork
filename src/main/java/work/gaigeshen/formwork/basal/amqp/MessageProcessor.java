package work.gaigeshen.formwork.basal.amqp;

public interface MessageProcessor {

    void process(String message, String messageType, ProcessorChain chain) throws Exception;

    interface ProcessorChain {

        void process(String message, String messageType) throws Exception;

    }
}
