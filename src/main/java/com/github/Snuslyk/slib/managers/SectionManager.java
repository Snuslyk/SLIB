package com.github.Snuslyk.slib.managers;

import com.github.Snuslyk.slib.electives.ManageableElectives;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.util.List;

public class SectionManager {

    private final VBox sectionsContainer;
    private final ToggleGroup sectionToggleGroup = new ToggleGroup();
    private final SectionSelectionListener listener;

    private List<ManageableElectives> sections;

    public SectionManager(VBox sectionsContainer, SectionSelectionListener listener) {
        this.sectionsContainer = sectionsContainer;
        this.listener = listener;
    }

    public void setSections(List<ManageableElectives> sections) {
        this.sections = sections;
    }

    public void setupSections() {
        if (sections == null) return;

        boolean isFirst = true;
        for (int i = 0; i < sections.size(); i++) {
            ManageableElectives section = sections.get(i);
            RadioButton sectionButton = ButtonFactory.createRadioButton(sectionToggleGroup, section.getDisplayName(), isFirst);
            int finalI = i;
            sectionButton.setOnAction(event -> listener.onSectionSelected(finalI));
            sectionsContainer.getChildren().add(sectionButton);
            isFirst = false;
        }
    }

    public interface SectionSelectionListener {
        void onSectionSelected(int sectionIndex);
    }
}

