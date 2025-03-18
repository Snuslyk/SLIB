package com.github.Snuslyk.slib;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

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
         HibernateUtil.addAnnotatedClass(ForestProducts.class);
         HibernateUtil.getSessionFactory();

         Button forestButton = new Button("Виды лесопродукции", forestProducts());

        controller.setSectionList("Коммерческая служба","Служба производства","Служба технолога","Персонал");
        controller.addObjectList(forestButton);
        controller.addObjectList(forestButton);
        controller.addObjectList(forestButton);
        controller.addObjectList();
        )); */
    }

    public void stageSettings(Stage stage, Scene scene){
        /* Для примера:
        stage.setTitle("lesopilka");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png"))));
        stage.setMinWidth(400);
        stage.setMinHeight(400);
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