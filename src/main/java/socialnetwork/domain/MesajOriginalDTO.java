package socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MesajOriginalDTO {
    private Long id_mesaj;
    private List<Utilizator> participanti;
    private LocalDateTime last_date;

    public LocalDateTime getLast_date() {
        return last_date;
    }

    public void setLast_date(LocalDateTime last_date) {
        this.last_date = last_date;
    }

    public MesajOriginalDTO(Long id_mesaj, List<Utilizator> to, LocalDateTime last_date) {
        this.id_mesaj = id_mesaj;
        this.participanti = to;
        this.last_date = last_date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MesajOriginalDTO)) return false;
        MesajOriginalDTO that = (MesajOriginalDTO) o;
        return getId_mesaj().equals(that.getId_mesaj());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId_mesaj());
    }

    @Override
    public String toString() {
        return participanti.stream().map(x->x.getFirstName() + " " + x.getLastName()).collect(Collectors.joining(", "));
    }

    public Long getId_mesaj() {
        return id_mesaj;
    }

    public void setId_mesaj(Long id_mesaj) {
        this.id_mesaj = id_mesaj;
    }

    public List<Utilizator> getParticipanti() {
        return participanti;
    }

    public void setParticipanti(List<Utilizator> participanti) {
        this.participanti = participanti;
    }
}
