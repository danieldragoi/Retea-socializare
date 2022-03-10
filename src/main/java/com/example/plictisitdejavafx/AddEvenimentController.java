package com.example.plictisitdejavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;
import socialnetwork.domain.Eveniment;
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class AddEvenimentController {

    private Service service;
    private Utilizator utilizator;
    private List<Utilizator> lista_participanti = new ArrayList<>();
    private MeniuController meniuController;

    public void setMeniuController(MeniuController meniuController) {
        this.meniuController = meniuController;
    }

    @FXML
    TextField textFieldNume;

    @FXML
    TextField textFieldDescriere;

    @FXML
    DatePicker datePicker;

    @FXML
    Button buttonAdauga;

    public void setService(Service service) {
        this.service = service;
    }

    public void setUtilizator(Utilizator utilizator) {
        this.utilizator = utilizator;
    }

    public void start(){
        lista_participanti.add(utilizator);
    }

    public void onClickAdd(){
        //verific daca am introdus ceva in toate casutele
        Boolean ok = true;
        if(textFieldNume.getText().length() == 0)
            ok = false;
        if(textFieldDescriere.getText().length() == 0)
            ok = false;
        if(datePicker.getValue() == null)
            ok = false;
        if(lista_participanti.size() == 0)
            ok = false;

        if(ok == false){ //daca nu am introdus un camp
            Alert alertValidation = new Alert(Alert.AlertType.WARNING, "Nu ai introdus in toate campurile!");
            alertValidation.show();

        }else // daca am introdus toate campurile
        {
            String nume = textFieldNume.getText();
            String descriere = textFieldDescriere.getText();
            LocalDateTime dateTime = LocalDateTime.of(datePicker.getValue(), LocalTime.MIDNIGHT);
            Dictionary<Utilizator, Boolean> notificari_participanti = new Hashtable();
            for(int i=0; i<lista_participanti.size(); i++)
                notificari_participanti.put(lista_participanti.get(i), true);

            Eveniment eveniment = new Eveniment(nume, descriere, lista_participanti, notificari_participanti, dateTime);
            service.addEveniment(eveniment);

            Stage stage = (Stage) buttonAdauga.getScene().getWindow();
            stage.close();

            meniuController.refreshEvenimente();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Eveniment adaugat!");
            alert.show();
        }
    }

}
