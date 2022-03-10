package socialnetwork.domain;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FriendRequest {
    Utilizator from;
    Utilizator to;
    LocalDateTime dateTime;

    public FriendRequest(Utilizator from, Utilizator to, LocalDateTime dateTime) {
        this.from = from;
        this.to = to;
        this.dateTime = dateTime;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public FriendRequest(Utilizator from, Utilizator to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return  "from=" + from.getUsername() +
                ", to=" + to.getUsername() +
                ",   " + dateTime.format(DateTimeFormatter.ofPattern("HH:mm     yyyy-MM-dd"));
    }

    public Utilizator getFrom() {
        return from;
    }

    public void setFrom(Utilizator from) {
        this.from = from;
    }

    public Utilizator getTo() {
        return to;
    }

    public void setTo(Utilizator to) {
        this.to = to;
    }
}
