package com.example.plictisitdejavafx;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import socialnetwork.domain.Eveniment;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.Service;

import java.util.Dictionary;
import java.util.List;
import java.util.Optional;

public class EvenimentInfoController {
    private Utilizator utilizator;
    private Eveniment eveniment;
    private Service service;
    private MeniuController meniuController;

    @FXML
    private Label labelNume;

    @FXML
    private Label labelDescriere;

    @FXML
    private CheckBox checkBoxNotificari;

    @FXML
    private ListView listViewParticipanti;

    @FXML
    private Button butonParticipare;

    @FXML
    private TextField textFieldSearch;

    @FXML
    private Label labelCreator;

    @FXML
    private Button butonStergeEveniment;

    public void setEveniment(Eveniment eveniment) {
        this.eveniment = eveniment;
    }

    public void setUtilizator(Utilizator utilizator) {
        this.utilizator = utilizator;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void setMeniuController(MeniuController meniuController) {
        this.meniuController = meniuController;
    }

    public void start(){
        labelNume.setText(eveniment.getNume());
        labelDescriere.setText(eveniment.getDescriere());

        List<Utilizator> users = eveniment.getParticipanti();
        ObservableList<Utilizator> observableList = FXCollections.observableArrayList();
        observableList.addAll(users);
        listViewParticipanti.setItems(observableList);

        checkBoxNotificari.setSelected(false);
        if(eveniment.getNotificariParticipanti().get(utilizator) != null){
            if(eveniment.getNotificariParticipanti().get(utilizator))
                checkBoxNotificari.setSelected(true);
        }

        if(eveniment.getParticipanti().contains(utilizator)){
            butonParticipare.setText("Iesi din eveniment");
        }
        else
            butonParticipare.setText("Participa la eveniment");

        butonStergeEveniment.setVisible(false);
        butonStergeEveniment.setDisable(true);
        if(eveniment.getParticipanti().size() > 0) {
            labelCreator.setText("Creat de: " + eveniment.getParticipanti().get(0).getFirstName() + " " + eveniment.getParticipanti().get(0).getLastName());
            if(eveniment.getParticipanti().get(0).equals(utilizator)){
                butonStergeEveniment.setVisible(true);
                butonStergeEveniment.setDisable(false);
            }
        }
    }

    public void onActionButonStergeEveniment(){
        service.stergeEveniment(eveniment);

        Stage stage = (Stage)butonParticipare.getScene().getWindow();
        stage.close();

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Eveniment sters!");
        alert.show();

        meniuController.refreshEvenimente();
    }

    public void onActionButonParticipare(){
        if(eveniment.getParticipanti().contains(utilizator)){ //daca utilizatorul deja participa
            List<Utilizator> participanti = eveniment.getParticipanti();
            participanti.remove(utilizator);
            eveniment.setParticipanti(participanti);
            service.deleteParticipantEveniment(utilizator, eveniment);
            start();
        }
        else{
            List<Utilizator> participanti = eveniment.getParticipanti();
            participanti.add(utilizator);
            eveniment.setParticipanti(participanti);
            service.addParticipantEveniment(utilizator, eveniment);
            start();
        }
    }

    public void filterSearchParticipanti(){
        String filter = textFieldSearch.getText();
        List<Utilizator> users = eveniment.getParticipanti();
        ObservableList<Utilizator> observableList = FXCollections.observableArrayList();
        users.forEach(x->observableList.add(x));
        ObservableList<Utilizator> observableFiltered = observableList.filtered(x->x.getLastName().contains(filter) ||
                x.getFirstName().contains(filter) || x.getUsername().contains(filter));
        listViewParticipanti.setItems(observableFiltered);
    }


    public void onNotificariChanged(){
        if(eveniment.getNotificariParticipanti().get(utilizator) != null){
            service.changeNotificariEvenimentUtilizator(utilizator, eveniment, !eveniment.getNotificariParticipanti().get(utilizator));
            Dictionary<Utilizator, Boolean> notificari = eveniment.getNotificariParticipanti();
            Boolean notificare = notificari.get(utilizator);
            notificari.remove(utilizator);
            notificari.put(utilizator, !notificare);
            meniuController.setNotificariEvenimente();
        }
    }

    //todo de facut rapoarte
    //todo de facut paginare (la final)

}
