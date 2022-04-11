package GUI;

import javafx.scene.layout.VBox;

public class PlayerSelection extends VBox {
    private final StartButton startButton;
    private StartScreen startScreen;
    private PlayerSelectionComboBox playerSelectionComboBox;
    private String guard = null;
    private String intruder = null;

    public PlayerSelection(StartScreen startScreen){
        this.startScreen = startScreen;
        this.playerSelectionComboBox = new PlayerSelectionComboBox(this);
        this.startButton = startScreen.getStartButton();

        getChildren().addAll(playerSelectionComboBox, startButton);
    }


    public void setAlgorithm(String type, String selectedItem) {
        if(type.equals("guard")){
            guard = selectedItem;
            startButton.setGuard(selectedItem);
        } else{
            intruder = selectedItem;
            startButton.setIntruder(selectedItem);
        }
    }
}
