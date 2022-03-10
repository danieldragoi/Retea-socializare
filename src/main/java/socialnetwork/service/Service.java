package socialnetwork.service;

import socialnetwork.domain.*;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.events.MessageSendEvent;
import socialnetwork.observer.Observable;
import socialnetwork.observer.Observer;
import socialnetwork.repository.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Service {
    private RepositoryUtilizator<Long, Utilizator> repoUtilizator;
    private RepositoryFriendship<Tuple<Long, Long>, Prietenie> repoPrietenie;
    private RepositoryMessage repoMesaje;
    private FriendRequestDbRepository friendRequestDbRepository;
    private EvenimentDbRepository evenimentDbRepository;

    private Validator<Utilizator> validatorUtilizator;
    private Validator<Prietenie> validatorPrietenie;
    private Validator<Message> validatorMesaj;

    public Service(RepositoryUtilizator<Long, Utilizator> repoUtilizator, RepositoryFriendship<Tuple<Long, Long>, Prietenie> repoPrietenie,
                   RepositoryMessage repoMesaje, FriendRequestDbRepository friendRequestDbRepository, EvenimentDbRepository evenimentDbRepository,
                   Validator<Utilizator> validatorUtilizator, Validator<Prietenie> validatorPrietenie, Validator<Message> validatorMesaj) {
        this.repoUtilizator = repoUtilizator;
        this.repoPrietenie = repoPrietenie;
        this.repoMesaje = repoMesaje;
        this.friendRequestDbRepository = friendRequestDbRepository;
        this.evenimentDbRepository = evenimentDbRepository;
        this.validatorUtilizator = validatorUtilizator;
        this.validatorPrietenie = validatorPrietenie;
        this.validatorMesaj = validatorMesaj;
    }

    public List<Message> getAllMessageReceivedByUtilizator(Utilizator utilizator, LocalDateTime dateInceput, LocalDateTime dateFinal){
        return repoMesaje.getAllMessageReceivedByUtilizator(utilizator, dateInceput, dateFinal);
    }

    public Boolean cautaPrietenie(Utilizator utilizator1, Utilizator utilizator2){
        Prietenie prietenie = repoPrietenie.findOne(new Tuple<>(utilizator1.getId(), utilizator2.getId()));
        if(prietenie == null)
            return false;
        return true;
    }
    public List<FriendRequest> getAllFriendRequestsForOne(Utilizator utilizator){
        return friendRequestDbRepository.getAllFriendRequestsForOne(utilizator);
    }

    public List<FriendRequestDTO> getAllFriendRequestsDTOForOne(Utilizator utilizator){
        List<FriendRequest> friendRequests = friendRequestDbRepository.getAllFriendRequestsForOne(utilizator);
        List<FriendRequestDTO> friendRequestDTOs = new ArrayList<>();
        for(int i=0; i<friendRequests.size(); i++){
            friendRequestDTOs.add(new FriendRequestDTO(utilizator, friendRequests.get(i)));
        }
        return friendRequestDTOs;
    }

    public List<Utilizator> getUtilizatoriPrieteniCu(Utilizator utilizator){//todo getAllUSeriPrieteni
        List<PrietenDTO> prietenii = repoPrietenie.findAllFriends(utilizator.getId());
        List<Utilizator> prieteniUtilizatori = new ArrayList<>();
        for(int i=0; i<prietenii.size(); i++){
            prieteniUtilizatori.add(prietenii.get(i).getUtilizator());
        }
        return prieteniUtilizatori;
    }

    public List<UtilizatorFriendDTO> getAllUtilizatoriDTOExcept(Utilizator utilizator){
        List<UtilizatorFriendDTO> utilizatorFriendDTOS = new ArrayList<>();
        List<Utilizator> utilizators = repoUtilizator.findAllExceptOne(utilizator.getId());
        for(int i=0; i<utilizators.size(); i++){
            utilizatorFriendDTOS.add(new UtilizatorFriendDTO(utilizator, utilizators.get(i), cautaPrietenie(utilizator, utilizators.get(i))));
        }

        return utilizatorFriendDTOS;
    }

    public Utilizator exitUtilizatorParola(String username, String password){
        return  repoUtilizator.existaUtilizator(username, password);
    }

    public List<PrietenDTO> findAllFriends(Utilizator utilizator){
        return repoPrietenie.findAllFriends(utilizator.getId());
    }

    public Message cautaConversatie(Utilizator utilizator1, Utilizator utilizator2){
        return repoMesaje.cautConversatieBetweenTwo(utilizator1, utilizator2);
    }

    private Observable<MessageSendEvent> observable = new Observable<MessageSendEvent>() {
        private List<Observer<MessageSendEvent>> observerList = new ArrayList<>();

        @Override
        public void addObserver(Observer<MessageSendEvent> e) {
            if(!observerList.contains(e)){
                observerList.add(e);
            }
        }

        @Override
        public void removeObserver(Observer<MessageSendEvent> e) {
            observerList.remove(e);
        }

        @Override
        public void notifyObservers(MessageSendEvent t) {
            observerList.forEach(x->x.update(t));
        }
    };

    public void addObserver(Observer<MessageSendEvent> e) {
        observable.addObserver(e);
    }

    public void notifyAllObservers(MessageSendEvent t){
        observable.notifyObservers(t);
    }

    public void removeObserver(Observer<MessageSendEvent> e) {
        observable.removeObserver(e);
    }

    public void addEveniment(Eveniment eveniment){
        evenimentDbRepository.addEveniment(eveniment);
    }

    public String getAllEvenimenteParticipaUtilizator(Utilizator utilizator){
        String evenimente_string="";
        List<Eveniment> eveniments = evenimentDbRepository.getAllEvenimente();
        for(int i=0; i<eveniments.size(); i++){
            Eveniment eveniment_i = eveniments.get(i);
            if(eveniment_i.getNotificariParticipanti().get(utilizator) != null)
                if(eveniment_i.getNotificariParticipanti().get(utilizator))
                    evenimente_string += "\n" + eveniment_i.getNume();

        }

        return evenimente_string;
    }

    public void stergeEveniment(Eveniment eveniment){
        evenimentDbRepository.stergeEveniment(eveniment);
    }

    public void deleteParticipantEveniment(Utilizator utilizator, Eveniment eveniment){
        evenimentDbRepository.deleteParticipantEveniment(utilizator, eveniment);
    }

    public void addParticipantEveniment(Utilizator utilizator, Eveniment eveniment){
        evenimentDbRepository.addParticipantEveniment(utilizator, eveniment);
    }

    public void changeNotificariEvenimentUtilizator(Utilizator utilizator, Eveniment eveniment, Boolean notificari){
        evenimentDbRepository.changeNotificariEvenimentParticipant(utilizator, eveniment, notificari);
    }

    public int getNrNotificariEvenimenteUtilizator(Utilizator utilizator){
        int nr=0;
        List<Eveniment> evenimente = evenimentDbRepository.getAllEvenimente();
        for(int i=0; i<evenimente.size(); i++){
            if(evenimente.get(i).getNotificariParticipanti().get(utilizator) != null){
                if(evenimente.get(i).getNotificariParticipanti().get(utilizator) == true)
                    nr++;
            }
        }

        return nr;
    }

    public List<Utilizator> getAllUtilizatoriExcept(List<Utilizator> utilizatorExceptList){
        List<Utilizator> allUtilizatori = repoUtilizator.findAll();
        List<Utilizator> utilizatoriSortati = new ArrayList<>();
        for(int i=0; i<allUtilizatori.size(); i++){
            if(!utilizatorExceptList.contains(allUtilizatori.get(i)))
                utilizatoriSortati.add(allUtilizatori.get(i));
        }
        return utilizatoriSortati;
    }

    public List<Eveniment> getAllEvenimente(){
        return evenimentDbRepository.getAllEvenimente();
    }

    public List<Utilizator> getAllUsersExceptOne(Long id_utilizator){
        return repoUtilizator.findAllExceptOne(id_utilizator);
    }

    public Utilizator addUtilizator(Utilizator u) {
        validatorUtilizator.validate(u);
        Utilizator task;
        task = repoUtilizator.save(u);
        return task;

    }

    public Utilizator removeUtilizator(Long id_u){
        Utilizator raspuns = repoUtilizator.findOne(id_u);

        if(raspuns != null) {
            List<Prietenie> prietenii = new ArrayList<>();

            //sterg prieteniile cu id-ul dat

            repoPrietenie.findAll().forEach(prietenie -> {
                if (prietenie.getId1() == id_u || prietenie.getId2() == id_u) {
                    prietenii.add(prietenie);
                }
            });

            for (Prietenie p : prietenii) {
                repoPrietenie.delete(p.getId());
            }

            Utilizator utilizator = repoUtilizator.delete(id_u);

            return utilizator;
        }
        return null;
    }

    public Prietenie addPrietenie(Long id1_u, Long id2_u, LocalDateTime date){

        Utilizator raspuns1 = repoUtilizator.findOne(id1_u);
        Utilizator raspuns2 = repoUtilizator.findOne(id2_u);
        if(raspuns1 == null)
            throw new ValidationException("Utilizatorul 1 nu exista!");
        if(raspuns2 == null)
            throw new ValidationException("Utilizatorul 2 nu exista!");

        Prietenie p = new Prietenie(raspuns1.getId(), raspuns2.getId(), date);
        validatorPrietenie.validate(p);

        if(repoPrietenie.findOne(new Tuple(p.getId1(), p.getId2())) == null){
            Prietenie prietenie = repoPrietenie.save(p);
            return prietenie;
        }
        return p;
    }

    public Prietenie removePrietenie(Long id1, Long id2){
        Utilizator u1 = repoUtilizator.findOne(id1);
        Utilizator u2 = repoUtilizator.findOne(id2);
        if(u1 == null)
            throw new ValidationException("Utilizatorul 1 nu exista!");
        if(u2 == null)
            throw new ValidationException("Utilizatorul 2 nu exista!");

        Prietenie raspuns = repoPrietenie.findOne(new Tuple(id1, id2));
        if(raspuns != null)
            return repoPrietenie.delete(new Tuple(id1, id2));

        return null;
    }

    public List<EvenimentDTO> getEvenimenteDTOforUser(Utilizator utilizator){
        List<Eveniment> eveniments = evenimentDbRepository.getAllEvenimente();
        List<EvenimentDTO> evenimentDTOList = new ArrayList<>();
        for(int i=0; i<eveniments.size(); i++){
            if(eveniments.get(i).getParticipanti().contains(utilizator))
                evenimentDTOList.add(new EvenimentDTO(eveniments.get(i), true));
            else
                evenimentDTOList.add(new EvenimentDTO(eveniments.get(i), false));
        }
        return evenimentDTOList;
    }

    public List<Utilizator> getAllUtilizatori(){
        return repoUtilizator.findAll();
    }

    public Iterable<Prietenie> getAllPrietenii() {
        return repoPrietenie.findAll();
    }


    public int getNumarComponenteConexe(){
        int nr = 0;
        for(Object obj : repoUtilizator.findAll()){
            nr++;
        }
        Graph g = new Graph(nr);

        for(Prietenie obj : repoPrietenie.findAll()){
            g.addEdge(obj.getId1().intValue()-1, obj.getId2().intValue()-1);
        }

        return g.ConnecetedComponents();
    }

    public ArrayList<Integer> getCeaMaiSociabilaComunitate(){
        int nr = 0;
        for(Object obj : repoUtilizator.findAll()){
            nr++;
        }
        Graph g = new Graph(nr);

        for(Prietenie obj : repoPrietenie.findAll()){
            g.addEdge(obj.getId1().intValue()-1, obj.getId2().intValue()-1);
        }

        return g.LongestConnectedComponents();
    }

    public Utilizator getUtilizator(Long id){
        return  repoUtilizator.findOne(id);
    }

    public Iterable<String> getAllPrieteniUtilizator(Long id_utilizator){

        Iterable<Prietenie> prietenii = repoPrietenie.findAll();

        return StreamSupport.stream(prietenii.spliterator(), false)
                .filter(prietenie -> prietenie.getId1().equals(id_utilizator) || prietenie.getId2().equals(id_utilizator))
                .map(a->{
                    if(a.getId1() == id_utilizator)
                        return repoUtilizator.findOne(a.getId2()).getFirstName() + ", " +
                                repoUtilizator.findOne(a.getId2()).getLastName() + ", " + a.getDate().toString();
                    else
                        return repoUtilizator.findOne(a.getId1()).getFirstName() +  ", " +
                                repoUtilizator.findOne(a.getId1()).getLastName() + ", " + a.getDate().toString();
                }).collect(Collectors.toList());
    }

    public Iterable<Message> getAllMesaje() { return repoMesaje.findAll(); }

    public Message adaugaMesaj(Message mesaj){
        validatorMesaj.validate(mesaj);
        //verific daca exista utilizatorii dati
        if(mesaj.getFrom() == null)
            throw new ValidationException("Id-ul from nu exista!");
        mesaj.getTo().forEach(x->{
            if(x == null)
                throw new ValidationException("Id-ul to nu exista!");
        });
        repoMesaje.saveMessage(mesaj);
        return mesaj;
    }

    public Message adaugaReply(Message mesaj){
        //verific daca exista utilizatorii dati
        if(mesaj.getFrom() == null)
            throw new ValidationException("Id-ul from nu exista!");
        if(mesaj.getReply() == null)
            throw new ValidationException("Id-ul mesajul initial nu exista!");
        //verfic daca cel care a trimis o primit mesajul
        Utilizator gasit = mesaj.getReply().getTo().stream().filter(utilizator -> mesaj.getFrom().equals(utilizator)).
                findAny().
                orElse(null);
        if(gasit == null)
            throw new ValidationException("Cel care a trimis mesajul reply nu a primit acel mesaj!");

        //creez mesajul pentru repo
        List<Utilizator> to = new ArrayList<>();
        to.add(mesaj.getReply().getFrom());
        Message mesaj_repo = new Message(mesaj.getFrom(), to, mesaj.getMessage(), mesaj.getData());
        mesaj_repo.setReply(mesaj.getReply());

        return repoMesaje.saveMessage(mesaj_repo);
    }

    public Message getMesaj(Long id){ return repoMesaje.findOne(id); }

    public Iterable<Message> toateMesajele(Long id1, Long id2){
        return repoMesaje.getAllMessagesBetweenTwo(id1, id2);
    }

    public List<Prietenie> getPrieteniiUtilizatorLuna(String utilizator, String luna)
    {
        Long id = Long.parseLong(utilizator);
        Integer value = Integer.parseInt(luna);
        Month month = Month.values()[value-1];

        List<Prietenie> l = new ArrayList<Prietenie>();
        Iterable<Prietenie> li = getAllPrietenii();
        li.forEach(l::add);

        Map<Month, List<Prietenie>> collected = l.stream().collect(Collectors.groupingBy(x -> x.getDate().getMonth()));

        if (collected.get(month)!=null) {
            Map<Long, List<Prietenie>> collect1 = collected.get(month).stream().collect(Collectors.groupingBy(Prietenie::getId1));
            Map<Long, List<Prietenie>> collect2 = collected.get(month).stream().collect(Collectors.groupingBy(Prietenie::getId2));
            collect1.putAll(collect2);
            return collect1.get(id);
        }
        return null;
    }

    public Message adaugaReplyAll(Message mesaj){
        //verific daca exista utilizatorii dati
        if(mesaj.getFrom() == null)
            throw new ValidationException("Id-ul from nu exista!");
        if(mesaj.getReply() == null)
            throw new ValidationException("Id-ul mesajul initial nu exista!");

        //verific daca cel care a trimis a facut parte din mesajul anterior
        //verfic daca cel care a trimis o primit mesajul
        Utilizator gasit1 = mesaj.getReply().getTo().stream().filter(utilizator -> mesaj.getFrom().equals(utilizator)).
                findAny().
                orElse(null);

        //verific daca cel care trimite a trimis si mesajul anterior
        boolean gasit2 = mesaj.getFrom().equals(mesaj.getReply().getFrom());

        if(gasit1 == null && gasit2 == false)
            throw new ValidationException("Cel care a trimis mesajul reply nu a facut parte din mesajul anterior!");

        //creez mesajul pentru repo
        //adaug in lista de destinatari
        List<Utilizator> to = new ArrayList<>();
        if(gasit2 == false) //daca mesajul nu e trimis de aceeasi persoana
            to.add(mesaj.getReply().getFrom());

        for(int i=0; i<mesaj.getReply().getTo().size(); i++) {
           if(!mesaj.getReply().getTo().get(i).equals(mesaj.getFrom())) //daca mesajul nu e trimis de aceeasi persoana
               to.add(mesaj.getReply().getTo().get(i));

        }
        Message mesaj_repo = new Message(mesaj.getFrom(), to, mesaj.getMessage(), mesaj.getData());
        mesaj_repo.setReply(mesaj.getReply());

        return repoMesaje.saveMessage(mesaj_repo);
    }

}
