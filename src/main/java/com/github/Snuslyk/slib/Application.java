package com.github.Snuslyk.slib;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {
        Controller controller = new Controller();
        options(controller);

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("hello-view.fxml"));
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load());

        stageSettings(stage, scene);
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
        //System.out.println(HibernateUtil.getObjectWithFilter(User.class, new Filter("name", "lox22", Filter.Type.ONLY), new Filter("name", "lox42", Filter.Type.ADD)).toString());
//
        //System.out.println(HibernateUtil.getObjectWithFilter(User.class).toString());

        /* Для примера:
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        User lox = new User();
        lox.name = "lox";
        HibernateUtil.fastSave(lox);

         */
    }

    /* Этот метод нужен для запуска приложения!
    public static void main(String[] args) {
        launch();
    } */
}