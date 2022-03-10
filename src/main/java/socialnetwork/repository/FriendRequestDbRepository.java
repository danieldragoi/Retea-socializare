package socialnetwork.repository;


import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.MesajOriginalDTO;
import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FriendRequestDbRepository {
    private String url;
    private String username;
    private String password;
    private RepositoryUtilizator<Long, Utilizator> repoUtilizator;

    public FriendRequestDbRepository(String url, String username, String password, RepositoryUtilizator<Long, Utilizator> repoUtilizator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.repoUtilizator = repoUtilizator;
    }


    /**
     * Adauga in tabelul friend_requests o noua cerere de prietenie
     * Cererea trebuie sa fie validata
     * @param friendRequest
     */
    public void addFriendRequest(FriendRequest friendRequest){
        try (Connection connection = DriverManager.getConnection(url, username, password)){

            //adaug in tabelul friend_requests
            PreparedStatement ps = connection.prepareStatement("insert into friend_requests (id_from, id_to, data) values (?, ?, ?)");
            Timestamp date = Timestamp.valueOf(friendRequest.getDateTime());
            ps.setTimestamp(3, date);
            ps.setLong(1, friendRequest.getFrom().getId());
            ps.setLong(2, friendRequest.getTo().getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sterge o cerere de prietenie din tabelul friend_requests
     * Cerere trebuie sa existe
     * @param friendRequest
     */
    public void removeFriendRequest(FriendRequest friendRequest){

        try (Connection connection = DriverManager.getConnection(url, username, password)){
            PreparedStatement statement = connection.prepareStatement("DELETE FROM friend_requests WHERE id_from= ? and id_to= ?");
            statement.setLong(1, friendRequest.getFrom().getId());
            statement.setLong(2, friendRequest.getTo().getId());
            int resultSet = statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returneaza toate cererile de prietenie pe care le-a trimis sau primit un utilizator dat
     * @param utilizator - utilizatorul dat
     * @return
     */
    public List<FriendRequest> getAllFriendRequestsForOne(Utilizator utilizator){
        List<FriendRequest> friendRequests = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)){

            PreparedStatement statement = connection.prepareStatement("select * from friend_requests where id_from= ? or id_to= ? order by data desc");
            statement.setLong(1, utilizator.getId());
            statement.setLong(2, utilizator.getId());

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) { //cat timp o gasit
                Long id_from = resultSet.getLong("id_from");
                Long id_to = resultSet.getLong("id_to");
                LocalDateTime localDateTime = resultSet.getTimestamp("data").toLocalDateTime();

                FriendRequest friendRequest  = new FriendRequest(repoUtilizator.findOne(id_from), repoUtilizator.findOne(id_to), localDateTime);

                friendRequests.add(friendRequest);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendRequests;
    }

    /**
     * Cauta un friend request in db
     * Daca gaseste il returneaza
     * Altfel returneaza null
     * @param id_from
     * @param id_to
     * @return
     */
    public FriendRequest findOne(Long id_from, Long id_to){
        FriendRequest friendRequest = null;
        try (Connection connection = DriverManager.getConnection(url, username, password)){

            PreparedStatement statement = connection.prepareStatement("select * from friend_requests where (id_from= ? and id_to= ?) or (id_from= ? and id_to= ?) order by data desc");
            statement.setLong(1, id_from);
            statement.setLong(2, id_to);
            statement.setLong(4, id_from);
            statement.setLong(3, id_to);

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) { //daca o gasit
                LocalDateTime localDateTime = resultSet.getTimestamp("data").toLocalDateTime();

                friendRequest  = new FriendRequest(repoUtilizator.findOne(id_from), repoUtilizator.findOne(id_to), localDateTime);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return friendRequest;
    }

}
