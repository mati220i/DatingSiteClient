package pl.datingSite.model.messages;

import java.util.Date;

public class Message implements Comparable<Message>{

    private Long id;

    private String messageFrom, message;
    private Date whenSent;
    private boolean isReaded;


    public Message() {
    }

    public Message(String from, String message) {
        this.messageFrom = from;
        this.whenSent = new Date();
        this.isReaded = false;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    public Date getWhenSent() {
        return whenSent;
    }

    public void setWhenSent(Date whenSent) {
        this.whenSent = whenSent;
    }

    public boolean isReaded() {
        return isReaded;
    }

    public void setReaded(boolean readed) {
        isReaded = readed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", messageFrom='" + messageFrom + '\'' +
                ", message='" + message + '\'' +
                ", whenSent=" + whenSent +
                ", isReaded=" + isReaded +
                '}';
    }

    @Override
    public int compareTo(Message o) {
        return getWhenSent().compareTo(o.getWhenSent());
    }
}
