package socialnetwork.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;


public class Prietenie extends Entity<Tuple<Long, Long>> {

    Tuple<Long, Long> tuple;
    LocalDateTime date;

    public Prietenie(Long id1, Long id2, LocalDateTime date) {
        this.tuple = new Tuple<>(id1, id2);
        this.setId(tuple);
        this.date = date;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Long getId1() {
        return tuple.getLeft();
    }

    public Long getId2() {
        return tuple.getRight();
    }

    @Override
    public String toString() {
        return "Prietenie{" +
                tuple +
                " data: " + date.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prietenie)) return false;
        Prietenie prietenie = (Prietenie) o;
        return (this.tuple.getLeft() == prietenie.tuple.getLeft() || this.tuple.getLeft() == this.tuple.getRight()) &&
                (this.tuple.getRight() == prietenie.tuple.getRight()) || this.tuple.getRight() == this.tuple.getLeft();
    }

    @Override
    public int hashCode() {
        return Objects.hash(tuple);
    }


}
