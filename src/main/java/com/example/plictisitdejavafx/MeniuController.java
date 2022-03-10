package com.example.plictisitdejavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import socialnetwork.domain.*;
import socialnetwork.repository.*;
import socialnetwork.service.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class MeniuController {
    private RepositoryUtilizator<Long, Utilizator> userFileRepository =
            new UtilizatorDbRepository("jdbc:postgresql://localhost:5432/lab_map","postgres","postgres");
    private RepositoryFriendship<Tuple<Long, Long>, Prietenie> friendshipFileRepository =
            new PrietenieDbRepository("jdbc:postgresql://localhost:5432/lab_map","postgres","postgres", userFileRepository);
    private FriendRequestDbRepository friendRequestDbRepository =
            new FriendRequestDbRepository("jdbc:postgresql://localhost:5432/lab_map","postgres","postgres", userFileRepository);

    private Service service;

    public void setService(Service service) {

        this.service = service;
        butonAddEveniment.setVisible(false);
    }

    private Utilizator utilizator;

    @FXML
    public Label label_username;

    @FXML
    public Button buttonShowFriends;

    @FXML
    public ListView listView;

    @FXML
    public TextField textFieldFilter;

    @FXML
    public Label labelTip;

    @FXML
    public Label labelNume;

    @FXML
    public Button butonAddEveniment;

    @FXML
    public Label labelNotificari;


    enum Type{
        USERS, PRIETENDTO, REQUESTS, EVENIMENT
    }

    Type tip;

    public void setNotificariEvenimente(){
        int nr = service.getNrNotificariEvenimenteUtilizator(utilizator);
        if(nr == 0)
            labelNotificari.setVisible(false);
        else{
            labelNotificari.setVisible(true);
            labelNotificari.setText(String.valueOf(nr));
        }
    }

    public void setUtilizator(Utilizator utilizator) {
        this.utilizator = utilizator;
        label_username.setText("logged in as: " + utilizator.getUsername());
        labelNume.setText(utilizator.getFirstName() + " " + utilizator.getLastName());
        setNotificariEvenimente();

    }

    public void onButtonShowFriends(){
        List<PrietenDTO> prieteni = service.findAllFriends(utilizator);
        ObservableList<PrietenDTO> observableList = FXCollections.observableArrayList();
        observableList.addAll(prieteni);
        listView.setItems(observableList);
        tip = Type.PRIETENDTO;
        labelTip.setText("Prieteni");
        butonAddEveniment.setVisible(false);
    }

    public void onButtonShowUsers(){
//        List<UtilizatorFriendDTO> users = service.getAllUsersExceptOne(utilizator.getId());
        List<UtilizatorFriendDTO> users = service.getAllUtilizatoriDTOExcept(utilizator);
        ObservableList<UtilizatorFriendDTO> observableList = FXCollections.observableArrayList();
        observableList.addAll(users);
        listView.setItems(observableList);
        tip = Type.USERS;
        labelTip.setText("Users");
        butonAddEveniment.setVisible(false);
    }

    public void onButtonShowFriendRequests(){
        List<FriendRequestDTO> friendRequests = service.getAllFriendRequestsDTOForOne(utilizator);
        ObservableList<FriendRequestDTO> observableList = FXCollections.observableArrayList();
        observableList.addAll(friendRequests);
        listView.setItems(observableList);
        tip = Type.REQUESTS;
        labelTip.setText("Cereri prietenie");
        butonAddEveniment.setVisible(false);
    }

    public void onFilterSearch(){
        String filter = textFieldFilter.getText();
        if(tip == Type.USERS){
            List<UtilizatorFriendDTO> users = service.getAllUtilizatoriDTOExcept(utilizator);
            ObservableList<UtilizatorFriendDTO> observableList = FXCollections.observableArrayList();
            users.forEach(x->observableList.add(x));
            ObservableList<UtilizatorFriendDTO> observableFiltered = observableList.filtered(x->x.getUtilizator().getLastName().contains(filter) ||
                                                        x.getUtilizator().getFirstName().contains(filter) || x.getUtilizator().getUsername().contains(filter));
            listView.setItems(observableFiltered);
        }
        if(tip == Type.PRIETENDTO){
            List<PrietenDTO> prieteni = service.findAllFriends(utilizator);
            ObservableList<PrietenDTO> observableList = FXCollections.observableArrayList();
            prieteni.forEach(x->observableList.add(x));
            ObservableList<PrietenDTO> observableFiltered = observableList.filtered(x->x.getUtilizator().getLastName().contains(filter)
                                                || x.getUtilizator().getFirstName().contains(filter) || x.getUtilizator().getUsername().contains(filter));
            listView.setItems(observableFiltered);
        }
        if(tip == Type.REQUESTS){
//            List<FriendRequestDTO> friendRequests = friendRequestDbRepository.getAllFriendRequestsForOne(utilizator);
            List<FriendRequestDTO> friendRequests = service.getAllFriendRequestsDTOForOne(utilizator);
            ObservableList<FriendRequestDTO> observableList = FXCollections.observableArrayList();
            observableList.addAll(friendRequests);
            ObservableList<FriendRequestDTO> observableListFiltered = observableList.filtered(x->x.getFriendRequest().getFrom().getUsername().contains(filter)
                                                            || x.getFriendRequest().getTo().getUsername().contains(filter));
            listView.setItems(observableListFiltered);
        }
        if(tip == Type.EVENIMENT){
            List<EvenimentDTO> eveniments = service.getEvenimenteDTOforUser(utilizator);
            ObservableList<EvenimentDTO> observableList = FXCollections.observableArrayList();
            observableList.addAll(eveniments);
            ObservableList<EvenimentDTO> observableListFiltered = observableList.filtered(x->x.getEveniment().getNume().contains(filter));
            listView.setItems(observableListFiltered);

        }
    }

    public void onListViewPressed(){
        if(tip == Type.USERS){
            UtilizatorFriendDTO utilizator_selectat = (UtilizatorFriendDTO)listView.getSelectionModel().getSelectedItems().get(0);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Vrei sa ii trimiti cerere de prietenie?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.YES){
                //validat daca este deja prieten
                Tuple<Long, Long> tuple = new Tuple<>(utilizator_selectat.getUtilizator().getId(), utilizator.getId());
                if(friendshipFileRepository.findOne(tuple) != null){
                    Alert alertExista = new Alert(Alert.AlertType.WARNING, "Este deja prieten!");
                    alertExista.show();
                }else

                    //verific daca am trimis deja cerere de prietenie
                    if(friendRequestDbRepository.findOne(utilizator.getId(), utilizator_selectat.getUtilizator().getId()) != null){
                        Alert alertExista = new Alert(Alert.AlertType.WARNING, "Ai trimis deja cerere de prietenie!");
                        alertExista.show();
                    }else{
                        FriendRequest friendRequest = new FriendRequest(utilizator, utilizator_selectat.getUtilizator(), LocalDateTime.now());
                        friendRequestDbRepository.addFriendRequest(friendRequest);
                    }
            }
        }
        if(tip == Type.PRIETENDTO){
            PrietenDTO prietenDto = (PrietenDTO)listView.getSelectionModel().getSelectedItems().get(0);
            Utilizator utilizator_selectat = prietenDto.getUtilizator();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Vrei sa il stergi de la prieteni?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.YES){
                Tuple<Long, Long> tuple = new Tuple<>(utilizator_selectat.getId(), utilizator.getId());
                friendshipFileRepository.delete(tuple);
                onButtonShowFriends();
            }
        }
        if(tip == Type.REQUESTS){
            FriendRequestDTO friendRequest = (FriendRequestDTO) listView.getSelectionModel().getSelectedItems().get(0);

            //daca a trimis cererea o poate doar sterge
            if(friendRequest.getFriendRequest().getFrom().getId() == utilizator.getId()){
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Vrei sa stergi cererea de prietenie?", ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == ButtonType.YES){
                    friendRequestDbRepository.removeFriendRequest(friendRequest.getFriendRequest());
                    onButtonShowFriendRequests();
                }
            }

            //daca a primit o poate accepta/sterge
            if(friendRequest.getFriendRequest().getTo().getId() == utilizator.getId()){
                ButtonType buttonAccepta = new ButtonType("Accepta");
                ButtonType buttonSterge = new ButtonType("Sterge");
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Vrei sa accepti/stergi cererea de prietenie?", buttonAccepta, buttonSterge, ButtonType.CLOSE);
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == buttonAccepta){
                    friendRequestDbRepository.removeFriendRequest(friendRequest.getFriendRequest());
                    Prietenie prietenie = new Prietenie(utilizator.getId(), friendRequest.getFriendRequest().getFrom().getId(), LocalDateTime.now());
                    friendshipFileRepository.save(prietenie);
                    onButtonShowFriendRequests();
                }
                if(result.get() == buttonSterge){
                    friendRequestDbRepository.removeFriendRequest(friendRequest.getFriendRequest());
                    onButtonShowFriendRequests();
                }
            }
        }

        if(tip == Type.EVENIMENT){
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("eveniment-info-view.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load(), 600, 350);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setTitle("Eveniment Info");
            stage.setScene(scene);

            //trimit utilizatorul la controllerul de mesaje

            EvenimentDTO evenimentDTO = (EvenimentDTO) listView.getSelectionModel().getSelectedItems().get(0);
            EvenimentInfoController evenimentInfoController = fxmlLoader.getController();
            evenimentInfoController.setUtilizator(utilizator);
            evenimentInfoController.setEveniment(evenimentDTO.getEveniment());
            evenimentInfoController.setService(service);
            evenimentInfoController.setMeniuController(this);
            evenimentInfoController.start();

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    refreshEvenimente();
                }
            });
            stage.show();
        }
    }

    public void onButtonShowMessages(){
        //Deschid fereastra de mesaje
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("mesaje-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 720, 400);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Mesaje");
        stage.setScene(scene);

        //trimit utilizatorul la controllerul de mesaje
        MessagesController mesajeController = fxmlLoader.getController();
        mesajeController.setUtilizator(utilizator);
        mesajeController.setService(service);
        mesajeController.start();

        stage.show();
        butonAddEveniment.setVisible(false);
    }

    public void onButtonShowEvenimente(){
        List<EvenimentDTO> eveniments = service.getEvenimenteDTOforUser(utilizator);
        ObservableList<EvenimentDTO> observableList = FXCollections.observableArrayList();
        observableList.addAll(eveniments);
        listView.setItems(observableList);

        tip = Type.EVENIMENT;
        labelTip.setText("Evenimente");
        butonAddEveniment.setVisible(true);

    }

    public void onButtonAddEveniment(){
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("add-eveniment-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 300, 230);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Adauga eveniment");
        stage.setScene(scene);

        //trimit utilizatorul la controllerul de mesaje
        AddEvenimentController addEvenimentController = fxmlLoader.getController();
        addEvenimentController.setUtilizator(utilizator);
        addEvenimentController.setService(service);
        addEvenimentController.start();
        addEvenimentController.setMeniuController(this);

        stage.show();
    }

    public void refreshEvenimente(){
        if(tip == Type.EVENIMENT){
            List<EvenimentDTO> eveniments = service.getEvenimenteDTOforUser(utilizator);
            ObservableList<EvenimentDTO> observableList = FXCollections.observableArrayList();
            observableList.addAll(eveniments);
            listView.setItems(observableList);
        }
        setNotificariEvenimente();
    }

    public void onButtonRapoarte(){
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("rapoarte-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 400, 200);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Rapoarte");
        stage.setScene(scene);
        RapoarteController rapoarteController = (RapoarteController)fxmlLoader.getController();
        rapoarteController.setService(service);
        rapoarteController.start();

        stage.show();
    }
}
