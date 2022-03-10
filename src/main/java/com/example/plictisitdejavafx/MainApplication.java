package com.example.plictisitdejavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.PrietenieValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.*;
import socialnetwork.service.Service;

import java.io.IOException;

public class MainApplication extends Application {
    private Service service;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 260, 150);
        ((LoginController)fxmlLoader.getController()).setService(service);
        stage.setTitle("Log in");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void init() throws Exception {
        super.init();
        RepositoryUtilizator<Long,Utilizator> userFileRepository = new UtilizatorDbRepository("jdbc:postgresql://localhost:5432/lab_map","postgres","postgres");
        RepositoryFriendship<Tuple<Long, Long>, Prietenie> friendshipFileRepository = new PrietenieDbRepository("jdbc:postgresql://localhost:5432/lab_map","postgres","postgres", userFileRepository);
        RepositoryMessage messagesFileRepository = new MessageDbRepository("jdbc:postgresql://localhost:5432/lab_map","postgres","postgres", userFileRepository);
        FriendRequestDbRepository friendRequestDbRepository = new FriendRequestDbRepository("jdbc:postgresql://localhost:5432/lab_map","postgres","postgres", userFileRepository);
        EvenimentDbRepository evenimentDbRepository = new EvenimentDbRepository("jdbc:postgresql://localhost:5432/lab_map","postgres","postgres", userFileRepository);

        Validator<Utilizator> validatorUtilizator = new UtilizatorValidator();
        Validator<Prietenie> validatorPrietenie = new PrietenieValidator();
        Validator<Message> validatorMesaj = new MessageValidator();

        service = new Service(userFileRepository, friendshipFileRepository, messagesFileRepository, friendRequestDbRepository,
                evenimentDbRepository, validatorUtilizator, validatorPrietenie, validatorMesaj);
    }

    public static void main(String[] args) {
        launch();
    }
}