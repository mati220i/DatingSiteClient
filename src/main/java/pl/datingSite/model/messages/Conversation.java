package pl.datingSite.model.messages;

import java.util.LinkedList;
import java.util.List;

public class Conversation {
    private Long id;

    private String whose, fromWho;

    private List<Message> messages;


    public Conversation() {
        this.messages = new LinkedList<>();
    }

    public Conversation(String whose, String fromWho) {
        this.whose = whose;
        this.fromWho = fromWho;
        this.messages = new LinkedList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWhose() {
        return whose;
    }

    public void setWhose(String whose) {
        this.whose = whose;
    }

    public String getFromWho() {
        return fromWho;
    }

    public void setFromWho(String fromWho) {
        this.fromWho = fromWho;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "id=" + id +
                ", whose='" + whose + '\'' +
                ", fromWho='" + fromWho + '\'' +
                ", messages=" + messages +
                '}';
    }
}
