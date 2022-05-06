package GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.BorderPane;

public class StartScreen extends BorderPane {
    private final MapGui mapGui;
    private final StartButton startButton;
    private final PlayerSelection playerSelection;
    public StartScreen(MapGui mapGui){
        this.mapGui = mapGui;
        this.startButton = new StartButton(this);

        playerSelection = new PlayerSelection(this);
        setCenter(playerSelection);
    }

    public void startExploration(String guard, String intruder){
        mapGui.startExploration(guard, intruder);
    }

    public StartButton getStartButton(){
        return startButton;
    }
}
