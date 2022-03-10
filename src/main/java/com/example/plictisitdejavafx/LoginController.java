package com.example.plictisitdejavafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.RepositoryUtilizator;
import socialnetwork.repository.UtilizatorDbRepository;
import socialnetwork.service.Service;

import java.io.IOException;

public class LoginController {

    private Service service;

    @FXML
    public Button inainteButton;

    @FXML
    public TextField usernameField;

    @FXML
    public PasswordField parolaField;

    public void setService(Service service) {
        this.service = service;
    }

    @FXML
    protected void onInainteClickAction() {
        //verific daca exista un utilizator cu username si parola data
        String username = usernameField.getCharacters().toString();
        String password = parolaField.getText();
        Utilizator utilizator = service.exitUtilizatorParola(username, password);
        if(utilizator == null) {
            //nu exista utilizatorul cu parola data
            //throw ceva eroare

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Utilizator sau parola gresita!");
            alert.show();
        }else {
            //daca am gasit un utilizator
            //inchid fereastra de login
            Stage loginStage = (Stage) inainteButton.getScene().getWindow();
            loginStage.close();

            //deschid noua fereastra
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("meniu-view.fxml"));
            Scene scene = null;

            try {
                scene = new Scene(fxmlLoader.load(), 650, 470);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setTitle("Meniu");
            stage.setScene(scene);

            //trimit utilizatorul la controllerul de meniu
            MeniuController meniuController = fxmlLoader.getController();
            meniuController.setService(service);
            meniuController.setUtilizator(utilizator);

            stage.show();

            String evenimente;

            evenimente = service.getAllEvenimenteParticipaUtilizator(utilizator);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Evenimente la care participi saptamana viitoare:" + evenimente);
            alert.show();
        }
    }
}
