package GUI;

import javafx.application.Application;
import javafx.scene.layout.BorderPane;

public class StartScreen extends BorderPane {
    private final MapGui mapGui;
    private final StartButton startButton;
    public StartScreen(MapGui mapGui){
        this.mapGui = mapGui;
        this.startButton = new StartButton(this);
        setCenter(startButton);
    }

    public void startExploration(){
        mapGui.startExploration();
    }
}
