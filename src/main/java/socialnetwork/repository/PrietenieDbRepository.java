package socialnetwork.repository;


import socialnetwork.domain.PrietenDTO;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrietenieDbRepository implements RepositoryFriendship<Tuple<Long, Long>, Prietenie> {
    private String url;
    private String username;
    private String password;
    private RepositoryUtilizator<Long, Utilizator> repoUtilizator;

    public PrietenieDbRepository(String url, String username, String password, RepositoryUtilizator<Long, Utilizator> repoUtilizator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.repoUtilizator = repoUtilizator;
    }

    @Override
    public Prietenie findOne(Tuple<Long, Long> id) {
        try (Connection connection = DriverManager.getConnection(url, username, password)){
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships WHERE (id_1= ? and id_2 = ?) or (id_1= ? and id_2 = ?)");
             statement.setLong(1, id.getLeft());
             statement.setLong(2, id.getRight());
             statement.setLong(4, id.getLeft());
             statement.setLong(3, id.getRight());
             ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) { //daca o gasit
                Long id1 = resultSet.getLong("id_1");
                Long id2 = resultSet.getLong("id_2");
                LocalDateTime date = resultSet.getTimestamp("data").toLocalDateTime();

                Prietenie prietenie = new Prietenie(id1, id2, date);
                return prietenie;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public Iterable<Prietenie> findAll() {
        Set<Prietenie> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships order by username");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id_1");
                Long id2 = resultSet.getLong("id_2");
                LocalDateTime date = resultSet.getTimestamp("data").toLocalDateTime();

                Prietenie prietenie = new Prietenie(id1, id2, date);
                friendships.add(prietenie);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Prietenie save(Prietenie entity) {
        String sql = "insert into friendships (id_1, id_2, data) values (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getId1());
            ps.setLong(2, entity.getId2());
            Timestamp date = Timestamp.valueOf(entity.getDate());
            ps.setTimestamp(3, date);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Prietenie delete(Tuple<Long, Long> id) {
        Prietenie prietenie = findOne(id);
        if(prietenie == null)
            return null;

        try (Connection connection = DriverManager.getConnection(url, username, password)){
            PreparedStatement statement = connection.prepareStatement("DELETE FROM friendships WHERE (id_1 = ? and id_2 = ?) or (id_1 = ? and id_2 = ?)");
            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());
            statement.setLong(4, id.getLeft());
            statement.setLong(3, id.getRight());
            int resultSet = statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prietenie;
    }

    @Override
    public Prietenie update(Prietenie entity) {
//
//        Prietenie prietenie = findOne(entity.getId());
//        if(prietenie == null)
//            return entity;
//
//        try (Connection connection = DriverManager.getConnection(url, username, password)){
//            PreparedStatement statement = connection.prepareStatement("UPDATE friendships SET id_1= ? and id_2 = ? WHERE id = ?");
//            statement.setLong(1, entity.getId1());
//            statement.setLong(2, entity.getId2());
////            statement.setLong(3, entity.getId());
//            int resultSet = statement.executeUpdate();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
        return null;
    }

    @Override
    public List<PrietenDTO> findAllFriends(Long id_utilizator){
        List<PrietenDTO> friendships = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)){
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships where id_1=? or id_2=? order by data desc");
             statement.setLong(1, id_utilizator);
             statement.setLong(2, id_utilizator);
             ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id_1");
                Long id2 = resultSet.getLong("id_2");
                LocalDateTime date = resultSet.getTimestamp("data").toLocalDateTime();

                Utilizator utilizator;
                if(id1 == id_utilizator){
                    utilizator = repoUtilizator.findOne(id2);
                }else
                {
                    utilizator = repoUtilizator.findOne(id1);
                }
                PrietenDTO prieten = new PrietenDTO(utilizator, date);
                friendships.add(prieten);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }
}
