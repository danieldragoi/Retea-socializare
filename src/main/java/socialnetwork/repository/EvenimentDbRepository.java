package socialnetwork.repository;

import socialnetwork.domain.Eveniment;
import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class EvenimentDbRepository {
    private String url;
    private String username;
    private String password;
    private RepositoryUtilizator<Long, Utilizator> repoUtilizator;

    public EvenimentDbRepository(String url, String username, String password, RepositoryUtilizator<Long, Utilizator> repoUtilizator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.repoUtilizator = repoUtilizator;
    }

    /**
     * Adauga un eveniment in baza de date
     * eveniment - trebuie sa fie VALIDAT
     * @param eveniment
     */
    public void addEveniment(Eveniment eveniment){
        try (Connection connection = DriverManager.getConnection(url, username, password)){

            //adaug in tabelul evenimente
            PreparedStatement ps = connection.prepareStatement("insert into evenimente (nume, data, descriere) values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            Timestamp date = Timestamp.valueOf(eveniment.getData());
            ps.setString(1, eveniment.getNume());
            ps.setTimestamp(2, date);
            ps.setString(3, eveniment.getDescriere());
            ps.executeUpdate();

            //Caut id-ul evenimentului (id-ul evenimentului este pus de baza de date)
            Long id = null;
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next())
            {
                id = rs.getLong(1);
            }

            //adaug in tabelul evenimente_participanti
            for(int i=0; i<eveniment.getParticipanti().size(); i++) {
                PreparedStatement ps2 = connection.prepareStatement("insert into evenimente_participanti (id_eveniment, id_utilizator, notificari) values (?, ?, ?)");
                ps2.setLong(1, id);
                ps2.setLong(2, eveniment.getParticipanti().get(i).getId());
                ps2.setBoolean(3, eveniment.getNotificariParticipanti().get(eveniment.getParticipanti().get(i)));
                ps2.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Sterge evenimentul din baza de date
     * eveniment - trebuie sa fie valid
     * @param eveniment
     */
    public void stergeEveniment(Eveniment eveniment){
        try (Connection connection = DriverManager.getConnection(url, username, password)){

            PreparedStatement statement = connection.prepareStatement("delete from evenimente_participanti where id_eveniment= ?");
            statement.setLong(1, eveniment.getId());
            statement.executeUpdate();

            PreparedStatement statement2 = connection.prepareStatement("delete from evenimente where id= ?");
            statement2.setLong(1, eveniment.getId());
            statement2.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private Dictionary<Utilizator, Boolean> getAllParticipanti(Long id_eveniment){
        try(Connection connection = DriverManager.getConnection(url, username, password)){
            PreparedStatement statement1 = connection.prepareStatement("SELECT * from evenimente_participanti WHERE id_eveniment=?");
            statement1.setLong(1, id_eveniment);
            ResultSet resultSet1 = statement1.executeQuery();
            Dictionary<Utilizator, Boolean> participanti = new Hashtable<>();
            while (resultSet1.next()) { //cat timp o gasit
                Long id_user = resultSet1.getLong("id_utilizator");
                Boolean notificari = resultSet1.getBoolean("notificari");
                participanti.put(repoUtilizator.findOne(id_user), notificari);
            }
            return participanti;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returneza toate evenimentele din baza de date
     * @return o lista de evenimente sortate dupa data
     */
    public List<Eveniment> getAllEvenimente(){
        List<Eveniment> evenimente = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password)){

            PreparedStatement statement = connection.prepareStatement("select * from evenimente order by data desc");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) { //cat timp o gasit
                Long id = resultSet.getLong("id");
                String nume = resultSet.getString("nume");
                String descriere = resultSet.getString("descriere");
                LocalDateTime date = resultSet.getTimestamp("data").toLocalDateTime();

                //Caut lista de utilizatorii la care participa la eveniment (tabelul evenimente_participanti)
                Dictionary<Utilizator, Boolean> participanti_notificari = getAllParticipanti(id);
                List<Utilizator> participanti = Collections.list(participanti_notificari.keys());

                //Adaug mesajul
                Eveniment eveniment = new Eveniment(nume, descriere, participanti, participanti_notificari, date);
                eveniment.setId(id);
                evenimente.add(eveniment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evenimente;
    }

    public void deleteParticipantEveniment(Utilizator utilizator, Eveniment eveniment){
        try (Connection connection = DriverManager.getConnection(url, username, password)){

            PreparedStatement statement = connection.prepareStatement("delete from evenimente_participanti where id_eveniment= ? and id_utilizator= ?");
            statement.setLong(1, eveniment.getId());
            statement.setLong(2, utilizator.getId());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addParticipantEveniment(Utilizator utilizator, Eveniment eveniment){
        try (Connection connection = DriverManager.getConnection(url, username, password)){

            PreparedStatement statement = connection.prepareStatement("insert into evenimente_participanti (id_eveniment, id_utilizator, notificari) values (?, ?, ?)");
            statement.setLong(1, eveniment.getId());
            statement.setLong(2, utilizator.getId());
            statement.setBoolean(3, true);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeNotificariEvenimentParticipant(Utilizator utilizator, Eveniment eveniment, Boolean notificari){
        try (Connection connection = DriverManager.getConnection(url, username, password)){

            PreparedStatement statement = connection.prepareStatement("update evenimente_participanti set notificari= ? where id_eveniment= ? and id_utilizator= ?");
            statement.setBoolean(1, notificari);
            statement.setLong(2, eveniment.getId());
            statement.setLong(3, utilizator.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
