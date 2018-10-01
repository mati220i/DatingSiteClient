package pl.datingSite.model.messages;

public class MessageHelper {
    private String from, to;
    private Message message;


    public MessageHelper() {
    }

    public MessageHelper(String from, String to, Message message) {
        this.from = from;
        this.to = to;
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessageHelper{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", message=" + message +
                '}';
    }
}
