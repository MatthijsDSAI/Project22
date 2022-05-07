package GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class PlayerSelectionComboBox extends HBox {
    private PlayerSelection playerSelection;
    private PlayerChoiceBox guard;
    private PlayerChoiceBox intruder;
    public PlayerSelectionComboBox(PlayerSelection playerSelection) {
        this.playerSelection = playerSelection;
        setPadding(new Insets(20,10,20,10));
        setAlignment(Pos.CENTER);

        this.guard = new PlayerChoiceBox(this, "guard");
        guard.getItems().add("guard");
        this.intruder = new PlayerChoiceBox(this, "intruder");
        getChildren().addAll(guard, intruder);
    }

    public void setAlgorithm(String type, String selectedItem) {
        playerSelection.setAlgorithm(type, selectedItem);
    }
}
