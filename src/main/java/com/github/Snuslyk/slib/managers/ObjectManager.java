package com.github.Snuslyk.slib.managers;

import com.github.Snuslyk.slib.electives.ButtonElective;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.util.List;

public class ObjectManager {

    private final VBox objectContainer;
    private final ToggleGroup objectToggleGroup = new ToggleGroup();

    private List<List<ButtonElective>> objects;

    public ObjectManager(VBox objectContainer) {
        this.objectContainer = objectContainer;
    }

    public void setObjects(List<List<ButtonElective>> objects) {
        this.objects = objects;
    }

    public void setupObjects(int sectionIndex) {
        objectContainer.getChildren().clear();
        if (objects == null || sectionIndex >= objects.size()) return;

        List<ButtonElective> sectionObjects = objects.get(sectionIndex);
        boolean isFirst = true;
        for (ButtonElective object : sectionObjects) {
            RadioButton objectButton = ButtonFactory.createRadioButton(objectToggleGroup, object.getDisplayName(), isFirst);
            objectButton.setOnMouseClicked(event -> object.pressed());
            objectContainer.getChildren().add(objectButton);
            isFirst = false;
        }
    }
}


