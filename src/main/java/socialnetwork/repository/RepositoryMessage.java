package socialnetwork.repository;


import socialnetwork.domain.MesajDTO;
import socialnetwork.domain.MesajOriginalDTO;
import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;

import java.time.LocalDateTime;
import java.util.List;

public interface RepositoryMessage {

    /**
     *
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     *          or null - if there is no entity with the given id
     * @throws IllegalArgumentException
     *                  if id is null.
     */
    Message findOne(Long id);

    /**
     *
     * @return all entities
     */
    Iterable<Message> findAll();

    /**
     *
     * @param mesaj
     *         entity must be not null
     * @return null
     *
     * @throws ValidationException
     *            if the entity is not valid
     * @throws IllegalArgumentException
     *             if the given entity is null.     *
     */
    Message saveMessage(Message mesaj);


    /**
     *
     * @return all entities
     */
    Iterable<Message> getAllMessagesBetweenTwo(Long id1, Long id2);


    /**
     *  removes the entity with the specified id
     * @param id
     *      id must be not null
     * @return the removed entity or null if there is no entity with the given id
     * @throws IllegalArgumentException
     *                   if the given id is null.
     */
//    Message delete(Long id);
//
    /**
     *
     * @param entity
     *          entity must not be null
     * @return null - if the entity is updated,
     *                otherwise  returns the entity  - (e.g id does not exist).
     * @throws IllegalArgumentException
     *             if the given entity is null.
     * @throws ValidationException
     *             if the entity is not valid.
     */
//    Message update(Message mesaj);

    /**
     * id_utilizator
     * @return o lista de mesaje initiale pentru un utilizator
     */
    List<MesajOriginalDTO> getAllOriginalMessagesForOne(Long id_utilizator);

    /**
     *
     * @param id_mesaj_original
     * @return lista de mesaje
     */
    List<MesajDTO> getAllMessagesAfterOriginal(Long id_mesaj_original);


    /**
     * Retuneaza conversatia originala dintre doi utilizatori
     * Sau null daca nu exista
     * @param utilizator1
     * @param utilizator2
     * @return
     */
    Message cautConversatieBetweenTwo(Utilizator utilizator1, Utilizator utilizator2);

    /**
     * Returneaza o lista cu mesajele primite de un utilizator intr-o perioada data
     * @param utilizator
     * @param dateInceput
     * @param dateFinal
     * @return
     */
    List<Message> getAllMessageReceivedByUtilizator(Utilizator utilizator, LocalDateTime dateInceput, LocalDateTime dateFinal);
}
