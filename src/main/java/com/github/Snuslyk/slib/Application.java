package com.github.Snuslyk.slib;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {
        HibernateUtil.addAnnotatedClass(User.class);

        Controller controller = getController();
        options(controller);

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("hello-view.fxml"));
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load());

        stageSettings(stage, scene);

        HibernateUtil.getSessionFactory().openSession().close();
    }

    public Controller getController(){
        return Controller.instance();
    }

    // Этот метод нужен для добавления кастомных опций контроллеру, чтобы изменять его - @override
    public void options(Controller controller) {
        /* Для примера:
         controller.setSectionList(List.of(new Elective("Развлечения"), new Elective("Просвещение")));
         controller.setObjectsList(List.of(
                List.of(new Elective("Мероприятия"), new Elective("Виды мероприятий"), new Elective("Виды заявок"), new Elective("Заявки")),
                List.of(new Elective("Памятники"), new Elective("Музеи"))
        )); */
    }

    public void stageSettings(Stage stage, Scene scene){
        /* Для примера:
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

         */
    }

    /* Этот метод нужен для запуска приложения!
    public static void main(String[] args) {
        launch();
    } */
}