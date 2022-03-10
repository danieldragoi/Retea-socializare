package socialnetwork.domain;

import socialnetwork.repository.RepositoryFriendship;
import socialnetwork.service.Service;

public class UtilizatorFriendDTO {
    private Utilizator utilizator_logat;
    private Utilizator utilizator;
    private Boolean friends;

    public UtilizatorFriendDTO(Utilizator utilizator_logat, Utilizator utilizator, Boolean friends) {
        this.utilizator_logat = utilizator_logat;
        this.utilizator = utilizator;
        this.friends = friends;
    }

    public Utilizator getUtilizator_logat() {
        return utilizator_logat;
    }

    public void setUtilizator_logat(Utilizator utilizator_logat) {
        this.utilizator_logat = utilizator_logat;
    }

    public Utilizator getUtilizator() {
        return utilizator;
    }

    public void setUtilizator(Utilizator utilizator) {
        this.utilizator = utilizator;
    }

    @Override
    public String toString() {
        if(friends)
            return utilizator.getFirstName() + "\t" +
                    utilizator.getLastName() + "\t\t" +
                    utilizator.getUsername() + "\t\t" +
                    "prieten";
        return utilizator.getFirstName() + "\t" +
                utilizator.getLastName() + "\t\t" +
                utilizator.getUsername() + "\t\t";
    }
}
