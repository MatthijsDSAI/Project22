package GUI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class StartButton extends Button {
    public StartButton(StartScreen startScreen){
        setText("Start Exploration");
        setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                startScreen.startExploration();
            }
        });
    }
}
