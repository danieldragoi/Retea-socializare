package socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Dictionary;
import java.util.List;

public class Eveniment {
    private long id;
    private String nume;
    private String descriere;
    private List<Utilizator> participanti;
    private Dictionary<Utilizator, Boolean> notificariParticipanti;
    private LocalDateTime data;

    public Eveniment(String nume, String descriere, List<Utilizator> participanti, Dictionary<Utilizator, Boolean> notificariParticipanti, LocalDateTime data) {
        this.nume = nume;
        this.participanti = participanti;
        this.notificariParticipanti = notificariParticipanti;
        this.data = data;
        this.descriere = descriere;
    }

    @Override
    public String toString() {
        return  nume + "\t\t" + descriere + "\t\t" + data.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public List<Utilizator> getParticipanti() {
        return participanti;
    }

    public void setParticipanti(List<Utilizator> participanti) {
        this.participanti = participanti;
    }

    public Dictionary<Utilizator, Boolean> getNotificariParticipanti() {
        return notificariParticipanti;
    }

    public void setNotificariParticipanti(Dictionary<Utilizator, Boolean> notificariParticipanti) {
        this.notificariParticipanti = notificariParticipanti;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }
}
