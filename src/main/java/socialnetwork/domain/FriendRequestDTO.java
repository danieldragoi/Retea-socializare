package socialnetwork.domain;

import java.time.format.DateTimeFormatter;

public class FriendRequestDTO {
    Utilizator utilizator;
    FriendRequest friendRequest;

    public FriendRequestDTO(Utilizator utilizator, FriendRequest friendRequest) {
        this.utilizator = utilizator;
        this.friendRequest = friendRequest;
    }

    public Utilizator getUtilizator() {
        return utilizator;
    }

    public void setUtilizator(Utilizator utilizator) {
        this.utilizator = utilizator;
    }

    public FriendRequest getFriendRequest() {
        return friendRequest;
    }

    public void setFriendRequest(FriendRequest friendRequest) {
        this.friendRequest = friendRequest;
    }

    @Override
    public String toString() {
        if(friendRequest.getFrom().equals(utilizator))
            return "Ai trimis o cerere de prietenie lui " + friendRequest.getTo().getFirstName() + " " + friendRequest.getTo().getLastName() +
                    " " + friendRequest.getDateTime().format(DateTimeFormatter.ofPattern("(HH:mm, yyyy-MM-dd)"));
        else
            return "Ai primit o cerere de prietenie de la " + friendRequest.getFrom().getFirstName() + " " + friendRequest.getFrom().getLastName() +
                    " " + friendRequest.getDateTime().format(DateTimeFormatter.ofPattern("(HH:mm, yyyy-MM-dd)"));
    }
}
