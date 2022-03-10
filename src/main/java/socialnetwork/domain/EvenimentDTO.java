package socialnetwork.domain;

public class EvenimentDTO {
//    Utilizator utilizator;
    Eveniment eveniment;
    Boolean enrolled;

    public EvenimentDTO(Eveniment eveniment, Boolean enrolled) {
        this.eveniment = eveniment;
        this.enrolled = enrolled;
    }

    @Override
    public String toString() {
        if(enrolled == true)
            return eveniment.toString() + "\t\t" + "particip";
        return eveniment.toString();
    }

    public Boolean getEnrolled() {
        return enrolled;
    }

    public void setEnrolled(Boolean enrolled) {
        this.enrolled = enrolled;
    }

    public Eveniment getEveniment() {
        return eveniment;
    }

    public void setEveniment(Eveniment eveniment) {
        this.eveniment = eveniment;
    }
}
