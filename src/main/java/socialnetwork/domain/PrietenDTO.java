package socialnetwork.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PrietenDTO {
    private Utilizator utilizator;
    private LocalDateTime date;

    public PrietenDTO(Utilizator utilizator, LocalDateTime date) {
        this.utilizator = utilizator;
        this.date = date;
    }

    public Utilizator getUtilizator() {
        return utilizator;
    }

    public void setUtilizator(Utilizator utilizator) {
        this.utilizator = utilizator;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return  utilizator.getFirstName() +
                "\t" + utilizator.getLastName() +
                "\t\t" + utilizator.getUsername() +
                "\t\t" + date.format(DateTimeFormatter.ofPattern("HH:mm, yyyy-MM-dd"));
    }
}
