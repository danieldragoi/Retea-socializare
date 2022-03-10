package socialnetwork.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long>{
    private Utilizator from;
    private List<Utilizator> to;
    private String message;
    private LocalDateTime data;
    private Message reply;

    public Message(Utilizator from, List<Utilizator> to, String message, LocalDateTime data) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = data;
        this.reply = null;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from=" + from +
                ", to=" + to +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", reply=" + reply +
                '}';
    }

    public void setFrom(Utilizator from) {
        this.from = from;
    }

    public void setTo(List<Utilizator> to) {
        this.to = to;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    public Utilizator getFrom() {
        return from;
    }

    public List<Utilizator> getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getData() {
        return data;
    }

    public Message getReply() {return reply; }
}