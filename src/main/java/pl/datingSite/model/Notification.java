package pl.datingSite.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Notification {
    private Long id;

    private String topic, content;
    private String receiveDate;

    private boolean isReaded;

    public Notification() {
    }

    public Notification(String topic, String content) {
        this.topic = topic;
        this.content = content;
        this.isReaded = false;
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        this.receiveDate = format.format(new Date());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isReaded() {
        return isReaded;
    }

    public void setReaded(boolean readed) {
        isReaded = readed;
    }

    public String getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(String receiveDate) {
        this.receiveDate = receiveDate;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", topic='" + topic + '\'' +
                ", content='" + content + '\'' +
                ", receiveDate=" + receiveDate +
                ", isReaded=" + isReaded +
                '}';
    }
}
