package com.github.Snuslyk.slib;

import com.github.Snuslyk.slib.electives.Elective;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {

        Controller controller = new Controller();
        controller.setSectionList(List.of(new Elective("Развлечения"), new Elective("Просвещение"), new Elective("Образование")));
        controller.setObjectsList(List.of(
                List.of(new Elective("Мероприятия"), new Elective("Виды мероприятий"), new Elective("Виды заявок"), new Elective("Заявки")),
                List.of(new Elective("Мероприяти"), new Elective("Заявк"), new Elective("Виды мероприяти"), new Elective("Виды заяво")),
                List.of(new Elective("Мероприят"), new Elective("Заяв"), new Elective("Виды мероприят"), new Elective("Виды заяв")),
                List.of(new Elective("Мероприя"), new Elective("Зая"), new Elective("Виды мероприя"), new Elective("Виды зая"))
        ));
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