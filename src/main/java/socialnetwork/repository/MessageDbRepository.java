package socialnetwork.repository;


import socialnetwork.domain.MesajDTO;
import socialnetwork.domain.MesajOriginalDTO;
import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class MessageDbRepository implements RepositoryMessage {
    private String url;
    private String username;
    private String password;
    private RepositoryUtilizator<Long, Utilizator> repoUtilizator;

    public MessageDbRepository(String url, String username, String password, RepositoryUtilizator<Long, Utilizator> repoUtilizator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.repoUtilizator = repoUtilizator;
    }

    /**
     * @param id_message id-ul mesajului
     * @return  o lista cu toti care primesc mesajul dat
     *          null daca nu o gasit mesajul
     */
    private List<Utilizator> getAllReceivers(Long id_message){
        try(Connection connection = DriverManager.getConnection(url, username, password)){
            PreparedStatement statement1 = connection.prepareStatement("SELECT * from message_to WHERE id_message=?");
            statement1.setLong(1, id_message);
            ResultSet resultSet1 = statement1.executeQuery();
            List<Utilizator> to = new ArrayList<>();
            while (resultSet1.next()) { //cat timp o gasit
                Long id_to = resultSet1.getLong("id_to");
                to.add(repoUtilizator.findOne(id_to));
            }
            return to;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Message findOne(Long id_message) {

        try (Connection connection = DriverManager.getConnection(url, username, password)){

            //Caut id-ul mesajului in tabelul message
            PreparedStatement statement = connection.prepareStatement("select * from message where id=?");
            statement.setLong(1, id_message);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) { //daca o gasit
                Long id = resultSet.getLong("id");
                String text = resultSet.getString("text");
                LocalDateTime date = resultSet.getTimestamp("data").toLocalDateTime();
                Long fromId = resultSet.getLong("id_from");
                Long replyId = resultSet.getLong("original_message");

                //Caut lista de utilizatori la care se trimite mesajul (in tabelul message_to)
                List<Utilizator> to = getAllReceivers(id_message);

                Utilizator from = repoUtilizator.findOne(fromId);
                Message mesaj = new Message(from, to, text, date);
                mesaj.setId(id);
                Message reply = null;
                if(replyId != null)
                    reply = findOne(replyId);
                mesaj.setReply(reply);
                return mesaj;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Iterable<Message> findAll() {
        Set<Message> mesaje = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password)){

            PreparedStatement statement = connection.prepareStatement("select * from message");
            ResultSet resultSet = statement.executeQuery();
            //Iau toate mesajele pe rand din tabelul message
            while (resultSet.next()) { //cat timp o gasit
                Long id = resultSet.getLong("id");
                String text = resultSet.getString("text");
                LocalDateTime date = resultSet.getTimestamp("data").toLocalDateTime();
                Long from_id = resultSet.getLong("id_from");
                Long reply_id = resultSet.getLong("original_message");

                //Caut lista de utilizatorii la care se trimite (in tabelul message_to)
                List<Utilizator> to = getAllReceivers(id);

                //Adaug mesajul
                Message mesaj = new Message(repoUtilizator.findOne(from_id), to, text, date);
                mesaj.setId(id);
                mesaj.setReply(findOne(reply_id));
                mesaje.add(mesaj);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mesaje.stream().sorted(Comparator.comparing(Message::getData)).collect(Collectors.toList());

    }

    @Override
    public Message saveMessage(Message mesaj) {

        //Am modificat pentru a fi universala (si pentru reply/replyAll)
        try (Connection connection = DriverManager.getConnection(url, username, password)){

            //adaug in tabelul message
            PreparedStatement ps = connection.prepareStatement("insert into message (data, id_from, text, original_message) values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            Timestamp date = Timestamp.valueOf(mesaj.getData());
            ps.setTimestamp(1, date);
            ps.setLong(2, mesaj.getFrom().getId());
            ps.setString(3, mesaj.getMessage());
            if(mesaj.getReply() != null)
                ps.setLong(4, mesaj.getReply().getId());
            else
                ps.setNull(4, Types.INTEGER);
            ps.executeUpdate();

            //Caut id-ul mesajului (id-ul mesajului este pus de baza de date)
            Long id = null;
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next())
            {
                id = rs.getLong(1);
            }

            //adaug in tabelul message_to
            for(int i=0; i<mesaj.getTo().size(); i++) {
                PreparedStatement ps2 = connection.prepareStatement("insert into message_to (id_message, id_to) values (?, ?)");
                ps2.setLong(1, id);
                ps2.setLong(2, mesaj.getTo().get(i).getId());
                ps2.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Message> getAllMessagesBetweenTwo(Long id1, Long id2) {
        Set<Message> mesaje = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)){

            PreparedStatement statement = connection.prepareStatement("select distinct * from message inner join message_to on message.id=message_to.id_message " +
                    "where (message.id_from=? and message_to.id_to=?) or (message.id_from=? and message_to.id_to=?)");
            statement.setLong(1, id1);
            statement.setLong(2, id2);
            statement.setLong(3, id2);
            statement.setLong(4, id1);

            ResultSet resultSet = statement.executeQuery();
            //Iau toate mesajele pe rand din tabelul message care apartin de id1 sau id2
            while (resultSet.next()) { //cat timp o gasit
                Long id = resultSet.getLong("id");
                String text = resultSet.getString("text");
                LocalDateTime date = resultSet.getTimestamp("data").toLocalDateTime();
                Long from_id = resultSet.getLong("id_from");
                Long reply_id = resultSet.getLong("original_message");

                //Caut lista de utilizatorii la care se trimite (in tabelul message_to)
                List<Utilizator> to = getAllReceivers(id);

                //Adaug mesajul
                Message mesaj = new Message(repoUtilizator.findOne(from_id), to, text, date);
                mesaj.setId(id);
                mesaj.setReply(findOne(reply_id));
                mesaje.add(mesaj);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mesaje.stream().sorted(Comparator.comparing(Message::getData)).collect(Collectors.toList());
    }

    public LocalDateTime getLastDateForOriginalMessage(Long id_message){

        LocalDateTime date=LocalDateTime.now();
        try (Connection connection = DriverManager.getConnection(url, username, password)){

            PreparedStatement statement = connection.prepareStatement("select  data from message where original_message=? or id=? order by data desc");
            statement.setLong(1, id_message);
            statement.setLong(2, id_message);

            ResultSet resultSet = statement.executeQuery();
            //Iau toate mesajele pe rand din tabelul message care apartin de id_utilizator
            if (resultSet.next()) { //cat timp o gasit
                date = resultSet.getTimestamp("data").toLocalDateTime();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return date;
    }

    @Override
    public List<MesajOriginalDTO> getAllOriginalMessagesForOne(Long id_utilizator) {
        List<MesajOriginalDTO> mesajeDTO = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)){

            PreparedStatement statement = connection.prepareStatement("select distinct * from message inner join message_to on message.id=message_to.id_message " +
                    "where (message.id_from=? or message_to.id_to=?) and original_message is null order by data desc");
            statement.setLong(1, id_utilizator);
            statement.setLong(2, id_utilizator);

            ResultSet resultSet = statement.executeQuery();
            //Iau toate mesajele pe rand din tabelul message care apartin de id_utilizator
            while (resultSet.next()) { //cat timp o gasit
                Long id = resultSet.getLong("id");
                String text = resultSet.getString("text");
                LocalDateTime date = resultSet.getTimestamp("data").toLocalDateTime();
                Long from_id = resultSet.getLong("id_from");
                Long reply_id = resultSet.getLong("original_message");

                //Caut lista de utilizatorii la care se trimite (in tabelul message_to)
                List<Utilizator> to = getAllReceivers(id);

                //Adaug mesajul
                List<Utilizator> participanti = new ArrayList<>();
                if(id_utilizator != from_id)
                    participanti.add(repoUtilizator.findOne(from_id));

                for(int i=0; i<to.size(); i++){
                    if(to.get(i).getId() != id_utilizator){
                        participanti.add(to.get(i));
                    }
                }
                participanti.sort(Comparator.comparingLong(Utilizator::getId));

                LocalDateTime last_date = getLastDateForOriginalMessage(id);

                MesajOriginalDTO mesajDTO = new MesajOriginalDTO(id, participanti, last_date);

                //verific daca exista deja id-ul mesajului
                if(!mesajeDTO.contains(mesajDTO))
                    mesajeDTO.add(mesajDTO);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        mesajeDTO.sort(Comparator.comparing(MesajOriginalDTO::getLast_date).reversed());
        return mesajeDTO;
    }

    @Override
    public List<MesajDTO> getAllMessagesAfterOriginal(Long id_mesaj_original) {
        Set<Message> mesaje = new HashSet<>();
        List<MesajDTO> mesajeDTO = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)){

            PreparedStatement statement = connection.prepareStatement("select * from message where original_message=? or id=? order by data");
            statement.setLong(1, id_mesaj_original);
            statement.setLong(2, id_mesaj_original);

            ResultSet resultSet = statement.executeQuery();
            //Iau toate mesajele pe rand din tabelul message care apartin de id_utilizator
            while (resultSet.next()) { //cat timp o gasit
                Long id = resultSet.getLong("id");
                String text = resultSet.getString("text");
                LocalDateTime date = resultSet.getTimestamp("data").toLocalDateTime();
                Long from_id = resultSet.getLong("id_from");
                Long reply_id = resultSet.getLong("original_message");

                //Caut lista de utilizatorii la care se trimite (in tabelul message_to)
                List<Utilizator> to = getAllReceivers(id);

                //Adaug mesajul
                Message mesaj = new Message(repoUtilizator.findOne(from_id), to, text, date);
                mesaj.setId(id);
                mesaj.setReply(findOne(reply_id));
                mesaje.add(mesaj);

                MesajDTO mesajDTO = new MesajDTO(repoUtilizator.findOne(from_id), text, date);
                mesajeDTO.add(mesajDTO);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mesajeDTO;
    }

    @Override
    public Message cautConversatieBetweenTwo(Utilizator utilizator1, Utilizator utilizator2) {
        try (Connection connection = DriverManager.getConnection(url, username, password)){

            PreparedStatement statement = connection.prepareStatement("select * from message where original_message is null and (id_from=? or id_from=?)");
            statement.setLong(1, utilizator1.getId());
            statement.setLong(2, utilizator2.getId());

            ResultSet resultSet = statement.executeQuery();
            //Iau toate mesajele pe rand din tabelul message care apartin de id_utilizator
            while (resultSet.next()) { //cat timp o gasit
                Long id = resultSet.getLong("id");
                String text = resultSet.getString("text");
                LocalDateTime date = resultSet.getTimestamp("data").toLocalDateTime();
                Long from_id = resultSet.getLong("id_from");
                Long reply_id = resultSet.getLong("original_message");

                //Caut lista de utilizatorii la care se trimite (in tabelul message_to)
                List<Utilizator> to = getAllReceivers(id);

                Message mesaj = new Message(repoUtilizator.findOne(from_id), to, text, date);
                mesaj.setId(id);
                mesaj.setReply(findOne(reply_id));

                //verific daca conversatia gasita e intre cei doi utilizatori dati
                if(to.size() == 1){
                    if((to.get(0).equals(utilizator1) && from_id.equals(utilizator2.getId())) ||
                            (to.get(0).equals(utilizator2) && from_id.equals(utilizator1.getId())))
                        return mesaj;
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Message> getAllMessageReceivedByUtilizator(Utilizator utilizator, LocalDateTime dateInceput, LocalDateTime dateFinal) {
        List<Message> mesaje = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)){

            PreparedStatement statement = connection.prepareStatement("select distinct * from message inner join message_to on message.id=message_to.id_message " +
                    "where message_to.id_to=? order by data");
            statement.setLong(1, utilizator.getId());

            ResultSet resultSet = statement.executeQuery();
            //Iau toate mesajele pe rand din tabelul message care apartin de id1 sau id2
            while (resultSet.next()) { //cat timp o gasit
                Long id = resultSet.getLong("id");
                String text = resultSet.getString("text");
                LocalDateTime date = resultSet.getTimestamp("data").toLocalDateTime();
                Long from_id = resultSet.getLong("id_from");
                Long reply_id = resultSet.getLong("original_message");

                //Caut lista de utilizatorii la care se trimite (in tabelul message_to)
                List<Utilizator> to = getAllReceivers(id);

                //Adaug mesajul
                Message mesaj = new Message(repoUtilizator.findOne(from_id), to, text, date);
                mesaj.setId(id);
                mesaj.setReply(findOne(reply_id));
                mesaje.add(mesaj);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mesaje.stream().filter(x->x.getData().isAfter(dateInceput) && x.getData().isBefore(dateFinal)).collect(Collectors.toList());
    }

}
