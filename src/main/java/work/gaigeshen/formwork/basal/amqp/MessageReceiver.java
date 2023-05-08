package work.gaigeshen.formwork.basal.amqp;

public interface MessageReceiver {

    void receive(String message, String messageType) throws Exception;
}
