package GUI;

import javafx.scene.control.ChoiceBox;

public class PlayerChoiceBox extends ChoiceBox<String> {
    private final PlayerSelectionComboBox playerSelectionComboBox;
    private String type;

    public PlayerChoiceBox(PlayerSelectionComboBox playerSelectionComboBox, String type) {
        this.playerSelectionComboBox = playerSelectionComboBox;
        this.type = type;
        setOnAction(event -> {
            playerSelectionComboBox.setAlgorithm(type, getSelectionModel().getSelectedItem());
        });
    }
}
