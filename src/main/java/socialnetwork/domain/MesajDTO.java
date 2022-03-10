package socialnetwork.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MesajDTO {
    private Utilizator from;
    private String text;
    private LocalDateTime data;

    public MesajDTO(Utilizator from, String text, LocalDateTime data) {
        this.from = from;
        this.text = text;
        this.data = data;
    }

    @Override
    public String toString() {
        String str = String.format("%25s %30s %30s", from.getUsername(), text, data.format(DateTimeFormatter.ofPattern("HH:mm, yyyy-MM-dd")));
        return  str;
    }

    public Utilizator getFrom() {
        return from;
    }

    public void setFrom(Utilizator from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }
}
