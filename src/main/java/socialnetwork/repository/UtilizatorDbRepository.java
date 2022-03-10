package socialnetwork.repository;


import socialnetwork.domain.Utilizator;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UtilizatorDbRepository implements RepositoryUtilizator<Long, Utilizator> {
    private String url;
    private String username;
    private String password;

    public UtilizatorDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    @Override
    public Utilizator findOne(Long aLong) {

        try (Connection connection = DriverManager.getConnection(url, username, password)){
             PreparedStatement statement = connection.prepareStatement("SELECT * from users WHERE id= ?");
             statement.setLong(1, aLong);
             ResultSet resultSet = statement.executeQuery();
             if (resultSet.next()) { //daca o gasit
                 Long id = resultSet.getLong("id");
                 String firstName = resultSet.getString("first_name");
                 String lastName = resultSet.getString("last_name");
                 String username_utilizator = resultSet.getString("username");
                 String password_utilizator = resultSet.getString("password");
                 Utilizator utilizator = new Utilizator(firstName, lastName, username_utilizator, password_utilizator);
                 utilizator.setId(id);
                 return utilizator;
             }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Utilizator> findAll() {
        List<Utilizator> users = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username_utilizator = resultSet.getString("username");
                String password_utilizator = resultSet.getString("password");

                Utilizator utilizator = new Utilizator(firstName, lastName, username_utilizator, password_utilizator);
                utilizator.setId(id);
                users.add(utilizator);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public Utilizator save(Utilizator entity) {

        String sql = "insert into users (first_name, last_name ) values (?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Utilizator delete(Long aLong) {
        Utilizator u = findOne(aLong);
        if(u == null)
            return null;

        try (Connection connection = DriverManager.getConnection(url, username, password)){
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id = ?");
             statement.setLong(1, u.getId());
             int resultSet = statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return u;
    }

    @Override
    public Utilizator update(Utilizator entity) {
        Utilizator u = findOne(entity.getId());
        if(u == null)
            return entity;

        try (Connection connection = DriverManager.getConnection(url, username, password)){
            PreparedStatement statement = connection.prepareStatement("UPDATE users SET first_name= ? , last_name = ? WHERE id = ?");
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setLong(3, entity.getId());
            int resultSet = statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Utilizator existaUtilizator(String username_utilizator, String password_utilizator) {

        try (Connection connection = DriverManager.getConnection(url, username, password)){
            PreparedStatement statement = connection.prepareStatement("SELECT * from users WHERE username= ? and password= ?");
            statement.setString(1, username_utilizator);
            statement.setString(2, password_utilizator);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) { //daca o gasit
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                Utilizator utilizator = new Utilizator(firstName, lastName, username_utilizator, password_utilizator);
                utilizator.setId(id);
                return utilizator;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Utilizator> findAllExceptOne(Long id_utilizator_except) {
        List<Utilizator> users = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)){
             PreparedStatement statement = connection.prepareStatement("SELECT * from users where id != ? order by first_name, last_name");
             statement.setLong(1, id_utilizator_except);
             ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username_utilizator = resultSet.getString("username");
                String password_utilizator = resultSet.getString("password");

                Utilizator utilizator = new Utilizator(firstName, lastName, username_utilizator, password_utilizator);
                utilizator.setId(id);
                users.add(utilizator);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}

