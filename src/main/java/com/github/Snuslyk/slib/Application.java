package com.github.Snuslyk.slib;

import com.github.Snuslyk.slib.electives.NewObject;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {

        Controller controller = new Controller();
        controller.setSectionList(List.of(new NewObject("Развлечения"), new NewObject("Просвещение"), new NewObject("Образование")));
        controller.setObjectsList(List.of(new NewObject("Мероприятия"), new NewObject("Виды мероприятий"), new NewObject("Виды заявок")));

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("hello-view.fxml"));
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();


        //SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory()

        User lox = new User();
        lox.name = "lox";
        HibernateUtil.fastSave(lox);

    }

    public static void main(String[] args) {
        launch();
    }
}