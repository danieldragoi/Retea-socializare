package com.example.plictisitdejavafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.w3c.dom.Document;
import socialnetwork.domain.Message;
import socialnetwork.domain.PrietenDTO;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.Service;


import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class RapoarteController {

    private Service service;
    File fileSelected=null;
    public void setService(Service service) {
        this.service = service;
    }

    @FXML
    private Button buttonSearchFolder;

    @FXML
    private Label labelFolder;

    @FXML
    private SplitMenuButton splitMenuButton;

    @FXML
    private DatePicker datePickerInceput;

    @FXML
    private DatePicker datePickerFinal;

    @FXML
    private ComboBox comboBoxUsers;

    @FXML
    private ComboBox comboBoxFriends;

    @FXML
    private Button buttonGenereaza;

    enum Raport{
        R1, R2, R
    }
    private Raport raport= Raport.R;

    public void start(){
        comboBoxUsers.setVisible(false);
        comboBoxFriends.setVisible(false);
        MenuItem item1 = new MenuItem("Activitatea unui utilizator (prieteni noi si mesaje primite)");
        MenuItem item2 = new MenuItem("Mesajele primite de un utilizatorul de la un prieten");
        item1.setOnAction(event -> {
            splitMenuButton.setText("Activitatea unui utilizator (prieteni noi si mesaje primite)");
            comboBoxUsers.setVisible(true);
            comboBoxFriends.setVisible(false);
            raport = Raport.R1;
        });
        item2.setOnAction(event -> {
            splitMenuButton.setText("Mesajele primite de un utilizatorul de la un prieten");
            comboBoxUsers.setVisible(true);
            comboBoxFriends.setVisible(true);
            raport = Raport.R2;
        });
        splitMenuButton.getItems().clear();
        splitMenuButton.getItems().addAll(item1, item2);

        //adauga toti useri in comboBox
        List<Utilizator> utilizatori = service.getAllUtilizatori();
        for(int i=0; i<utilizatori.size(); i++){
            comboBoxUsers.getItems().add(utilizatori.get(i));
        }

        comboBoxUsers.setOnAction(event -> {
            Utilizator utilizator_selectat = (Utilizator) comboBoxUsers.getSelectionModel().getSelectedItem();
            //adaug toti prietenii
            List<Utilizator> utilizatoriPrieteni = service.getUtilizatoriPrieteniCu(utilizator_selectat);
            for(int i=0; i<utilizatoriPrieteni.size(); i++){
                comboBoxFriends.getItems().add(utilizatoriPrieteni.get(i));
            }
        });

    }

    private void genereazaPDFRaport1(LocalDate dateInceput, LocalDate dateFinal, Utilizator utilizator_selectat){
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        PDPageContentStream pageContentStream = null;
        try {
            pageContentStream = new PDPageContentStream(document, page);
            pageContentStream.beginText();
            pageContentStream.setFont( PDType1Font.TIMES_ROMAN , 16 );
            pageContentStream.newLineAtOffset(100,  700);
            pageContentStream.showText("Prieteni noi creati in perioada: " + dateInceput + " - " + dateFinal);
            pageContentStream.endText();

            int ty = 670;
            //adauga fiecare prietenie noua din perioada data
            List<PrietenDTO> prietenDTOS = service.findAllFriends(utilizator_selectat);
            for(int i=0; i<prietenDTOS.size(); i++){
                PrietenDTO prietenDTO = prietenDTOS.get(i);
                if(prietenDTO.getDate().isAfter(LocalDateTime.of(dateInceput, LocalTime.MIDNIGHT)) && prietenDTO.getDate().isBefore(LocalDateTime.of(dateFinal, LocalTime.MIDNIGHT))){
                    pageContentStream.beginText();
                    pageContentStream.setFont( PDType1Font.TIMES_ROMAN , 12 );
                    pageContentStream.newLineAtOffset(100,  ty);
                    pageContentStream.showText(prietenDTO.getUtilizator().getFirstName() + " " + prietenDTO.getUtilizator().getLastName() + " " + prietenDTO.getUtilizator().getUsername());
                    pageContentStream.endText();
                    ty -= 30;
                }
            }
            //dupa adauga toate mesajele PRIMITE in perioada asta
            ty -= 20;
            pageContentStream.beginText();
            pageContentStream.setFont( PDType1Font.TIMES_ROMAN , 16 );
            pageContentStream.newLineAtOffset(100,  ty);
            pageContentStream.showText("Mesajele primite in perioada: " + dateInceput + " - " + dateFinal);
            pageContentStream.endText();
            ty -=30;
            List<Message> messages = service.getAllMessageReceivedByUtilizator(utilizator_selectat, LocalDateTime.of(dateInceput, LocalTime.MIDNIGHT), LocalDateTime.of(dateFinal, LocalTime.MIDNIGHT));
            for(int i=0; i<messages.size() && ty > 10; i++){
                Message message = messages.get(i);
                pageContentStream.beginText();
                pageContentStream.setFont( PDType1Font.TIMES_ROMAN , 12 );
                pageContentStream.newLineAtOffset(100,  ty);
                pageContentStream.showText(message.getFrom().getUsername() + ": " + message.getMessage() + message.getData().format(DateTimeFormatter.ofPattern(" (HH:mm, yyyy-MM-dd)")));
                pageContentStream.endText();
                ty -= 30;
            }

            pageContentStream.close();
            document.save(fileSelected.getAbsolutePath() + "\\PDFgenerat1.pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void genereazaPDFRaport2(LocalDate dateInceput, LocalDate dateFinal, Utilizator utilizator_selectat, Utilizator utilizator_selectat_prieten){
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        PDPageContentStream pageContentStream = null;
        try {
            pageContentStream = new PDPageContentStream(document, page);

            //adaug toate mesajele PRIMITE in perioada asta dintre cei doi
            int ty = 700;
            pageContentStream.beginText();
            pageContentStream.setFont( PDType1Font.TIMES_ROMAN , 16 );
            pageContentStream.newLineAtOffset(100,  ty);
            pageContentStream.showText("Mesajele primite de " + utilizator_selectat.getFirstName() + " " + utilizator_selectat.getLastName() + " " + utilizator_selectat.getUsername());
            pageContentStream.endText();
            ty -= 30;
            pageContentStream.beginText();
            pageContentStream.setFont( PDType1Font.TIMES_ROMAN , 16 );
            pageContentStream.newLineAtOffset(70,  ty);
            pageContentStream.showText("de la " +utilizator_selectat_prieten.getFirstName() + " " + utilizator_selectat_prieten.getLastName() + " " + utilizator_selectat_prieten.getUsername() + " ("
                    + dateInceput + " - " + dateFinal + ")");
            pageContentStream.endText();
            ty -= 50;
            List<Message> messages = service.getAllMessageReceivedByUtilizator(utilizator_selectat, LocalDateTime.of(dateInceput, LocalTime.MIDNIGHT), LocalDateTime.of(dateFinal, LocalTime.MIDNIGHT));
            messages = messages.stream().filter(x->x.getFrom().equals(utilizator_selectat_prieten)).collect(Collectors.toList());
            for(int i=0; i<messages.size() && ty > 10; i++){
                Message message = messages.get(i);
                pageContentStream.beginText();
                pageContentStream.setFont( PDType1Font.TIMES_ROMAN , 12 );
                pageContentStream.newLineAtOffset(100,  ty);
                pageContentStream.showText(message.getFrom().getUsername() + ": " + message.getMessage() + " " + message.getData().format(DateTimeFormatter.ofPattern("(HH:mm, yyyy-MM-dd)")));
                pageContentStream.endText();
                ty -= 30;
            }

            pageContentStream.close();
            document.save(fileSelected.getAbsolutePath() + "\\PDFgenerat2.pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void inchideFereastra(){
        Stage stage = (Stage) buttonGenereaza.getScene().getWindow();
        stage.close();
    }

    public void onButtonGenereaza(){
        //mai intai validez toate datele introduse
        LocalDate dateInceput = datePickerInceput.getValue();
        LocalDate dateFinal = datePickerFinal.getValue();
        Utilizator utilizator_selectat = (Utilizator) comboBoxUsers.getSelectionModel().getSelectedItem();
        Utilizator utilizator_selectat_prieten = (Utilizator) comboBoxFriends.getSelectionModel().getSelectedItem();
        if(raport.equals(Raport.R2)){
            if(dateInceput != null && dateFinal != null && fileSelected !=null && utilizator_selectat != null && utilizator_selectat_prieten != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Raport generat!");
                alert.show();
                genereazaPDFRaport2(dateInceput, dateFinal, utilizator_selectat, utilizator_selectat_prieten);
                inchideFereastra();
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR, "Nu ai introdus toate datele!");
                alert.show();
            }
        }else{
            if(dateInceput != null && dateFinal != null && fileSelected !=null && utilizator_selectat != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Raport generat!");
                alert.show();
                genereazaPDFRaport1(dateInceput, dateFinal, utilizator_selectat);
                inchideFereastra();
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR, "Nu ai introdus toate datele!");
                alert.show();
            }
        }
    }

    public void onButtonSearchFolder(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Cauta folderul destinatie");
        fileSelected = directoryChooser.showDialog(new Stage());

        if(fileSelected != null)
            labelFolder.setText("folderul: " + fileSelected.getAbsolutePath());
    }
}
