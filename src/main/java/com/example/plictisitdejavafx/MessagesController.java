package com.example.plictisitdejavafx;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import socialnetwork.domain.MesajDTO;
import socialnetwork.domain.MesajOriginalDTO;
import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;
import socialnetwork.events.MessageSendEvent;
import socialnetwork.observer.Observer;
import socialnetwork.repository.MessageDbRepository;
import socialnetwork.repository.RepositoryMessage;
import socialnetwork.repository.RepositoryUtilizator;
import socialnetwork.repository.UtilizatorDbRepository;
import socialnetwork.service.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessagesController {
    private Utilizator utilizator;
    private RepositoryUtilizator<Long, Utilizator> userFileRepository =
            new UtilizatorDbRepository("jdbc:postgresql://localhost:5432/lab_map","postgres","postgres");
    private RepositoryMessage messageFileRepository =
            new MessageDbRepository("jdbc:postgresql://localhost:5432/lab_map","postgres","postgres", userFileRepository);

    private Service service;

    @FXML
    ListView listViewGrupuriMesaje;

    @FXML
    ListView listViewMesaje;

    @FXML
    Label labelUsername;

    @FXML
    TextField textFieldMesaj;

    @FXML
    Button buttonSendMessage;

    @FXML
    TextField textFieldSearchUtilizatorMessage;

    @FXML
    Button buttonAddReceiver;

    @FXML
    Label labelReceivers;

    private boolean utilizatori_in_view = false;

    private List<Utilizator> receivers = new ArrayList<>();

    private  MesajOriginalDTO lastOriginalMessage = null;

    Observer<MessageSendEvent> messageSendEventObserver = new Observer<MessageSendEvent>() {
        @Override
        public void update(MessageSendEvent messageSendEvent) {
            if(utilizatori_in_view == false){

                List<MesajDTO> mesaje = messageFileRepository.getAllMessagesAfterOriginal(lastOriginalMessage.getId_mesaj());
                ObservableList<MesajDTO> observableList = FXCollections.observableArrayList();
                observableList.addAll(mesaje);
                listViewMesaje.setItems(observableList);
                listViewMesaje.scrollTo(mesaje.size());

            }
            List<MesajOriginalDTO> mesaje2 = messageFileRepository.getAllOriginalMessagesForOne(utilizator.getId());
            ObservableList<MesajOriginalDTO> observableList2 = FXCollections.observableArrayList();
            mesaje2.forEach(x->observableList2.add(x));
            listViewGrupuriMesaje.setItems(observableList2);
        }
    };

    public void setService(Service service) {
        this.service = service;
    }

    public void setUtilizator(Utilizator utilizator) {
        this.utilizator = utilizator;
        labelUsername.setText("logged in as: " + utilizator.getUsername());
    }

    public void onButtonRefresh(){
        if(utilizatori_in_view == false && lastOriginalMessage != null){

            List<MesajDTO> mesaje = messageFileRepository.getAllMessagesAfterOriginal(lastOriginalMessage.getId_mesaj());
            ObservableList<MesajDTO> observableList = FXCollections.observableArrayList();
            observableList.addAll(mesaje);
            listViewMesaje.setItems(observableList);
            listViewMesaje.scrollTo(mesaje.size());

        }
        List<MesajOriginalDTO> mesaje2 = messageFileRepository.getAllOriginalMessagesForOne(utilizator.getId());
        ObservableList<MesajOriginalDTO> observableList2 = FXCollections.observableArrayList();
        mesaje2.forEach(x->observableList2.add(x));
        listViewGrupuriMesaje.setItems(observableList2);
    }

    public void start(){
        service.addObserver(messageSendEventObserver);

        Iterable<MesajOriginalDTO> mesaje = messageFileRepository.getAllOriginalMessagesForOne(utilizator.getId());
        ObservableList<MesajOriginalDTO> observableList = FXCollections.observableArrayList();
        mesaje.forEach(x->observableList.add(x));
        listViewGrupuriMesaje.setItems(observableList);

        buttonSendMessage.setDisable(true);
        buttonAddReceiver.setDisable(true);
    }

    public void onOriginalMessagePressed(){
        utilizatori_in_view = false;
        receivers.clear();

        if(listViewGrupuriMesaje.getSelectionModel().getSelectedItems().size() > 0){ //in caz ca dai click pe altceva din listViewGrupuriMesaje
            MesajOriginalDTO mesajOriginalDTO = (MesajOriginalDTO) listViewGrupuriMesaje.getSelectionModel().getSelectedItems().get(0);

            List<MesajDTO> mesaje = messageFileRepository.getAllMessagesAfterOriginal(mesajOriginalDTO.getId_mesaj());
            ObservableList<MesajDTO> observableList = FXCollections.observableArrayList();
            observableList.addAll(mesaje);
            listViewMesaje.setItems(observableList);

            labelReceivers.setText("Receivers:");
            textFieldSearchUtilizatorMessage.setPromptText("search users");
            textFieldSearchUtilizatorMessage.clear();
            buttonAddReceiver.setDisable(true);
            buttonSendMessage.setDisable(false);
            listViewMesaje.scrollTo(mesaje.size());
            lastOriginalMessage = mesajOriginalDTO;
        }

    }

    public void onButtonSendMessage(){
        if(textFieldMesaj.getText().length() != 0){
            if(utilizatori_in_view == false){
                String text = textFieldMesaj.getText();
                MesajOriginalDTO mesajOriginalDTO = (MesajOriginalDTO) listViewGrupuriMesaje.getSelectionModel().getSelectedItems().get(0);
                Message mesajOriginal = messageFileRepository.findOne(mesajOriginalDTO.getId_mesaj());

                //creez mesajul pentru repo
                //adaug in lista de destinatari
                List<Utilizator> to = new ArrayList<>();
                if(!mesajOriginal.getFrom().equals(utilizator)) //daca mesajul nu e trimis de aceeasi persoana ca mesajul initial
                    to.add(mesajOriginal.getFrom());

                for(int i=0; i<mesajOriginal.getTo().size(); i++) {
                    if(!mesajOriginal.getTo().get(i).equals(utilizator)) //daca mesajul nu e trimis de aceeasi persoana
                        to.add(mesajOriginal.getTo().get(i));

                }
                Message mesaj_repo = new Message(utilizator, to, text, LocalDateTime.now());
                mesaj_repo.setReply(mesajOriginal);

                messageFileRepository.saveMessage(mesaj_repo);

                textFieldMesaj.clear();
                onOriginalMessagePressed();
            }
            else //cand trimite un mesaj nou altor persoane
            {
                if(receivers.size() == 1) {//daca trimit un mesaj unei persoane, atunci nu creez alta conversatie
                    Message messageOriginalBetweenTwo = service.cautaConversatie(utilizator, receivers.get(0));
                    if(messageOriginalBetweenTwo != null) { //daca exista deja mesajul
                        String text = textFieldMesaj.getText();
                        Message mesaj_repo = new Message(utilizator, receivers, text, LocalDateTime.now());
                        mesaj_repo.setReply(messageOriginalBetweenTwo);
                        messageFileRepository.saveMessage(mesaj_repo);
                    }
                    else
                    {
                        String text = textFieldMesaj.getText();
                        Message mesaj_repo = new Message(utilizator, receivers, text, LocalDateTime.now());
                        messageFileRepository.saveMessage(mesaj_repo);
                    }
                }
                textFieldMesaj.clear();
                start();
                labelReceivers.setText("Receivers:");
                textFieldSearchUtilizatorMessage.setPromptText("search users");
            }
            //actualizez mesajele din dreapta
            List<MesajOriginalDTO> mesaje = messageFileRepository.getAllOriginalMessagesForOne(utilizator.getId());
            ObservableList<MesajOriginalDTO> observableList = FXCollections.observableArrayList();
            mesaje.forEach(x->observableList.add(x));
            listViewGrupuriMesaje.setItems(observableList);
            listViewMesaje.scrollTo(observableList.size()-1);

//            service.notifyAllObservers(new MessageSendEvent());
//            Merge mai repede fara observer(si asa nu merge pt ca creeaza alt service cand dau start din nou)
        }
    }

    public void onTypedUtilizatorReceiver(){
        utilizatori_in_view = true;
        buttonSendMessage.setDisable(true);
        Iterable<Utilizator> users = userFileRepository.findAllExceptOne(utilizator.getId());
        ObservableList<Utilizator> observableList = FXCollections.observableArrayList();
        users.forEach(x->observableList.add(x));
        listViewMesaje.setItems(observableList);
    }

    public void onMouseClickedMesajeViewUtilizatori(){
        if(utilizatori_in_view == true){
            buttonAddReceiver.setDisable(false);
        }
    }

    public void onButtonAddReceiver(){
        Utilizator user = (Utilizator) listViewMesaje.getSelectionModel().getSelectedItems().get(0);
        //verific daca exista deja
        if(!receivers.contains(user)){
            receivers.add(user);
            buttonSendMessage.setDisable(false);
            String receivers = labelReceivers.getText();
            labelReceivers.setText(receivers +" " + user.getUsername());
        }
    }
}
