package GUI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class StartButton extends Button {
    private String guard = "FrontierBasedExploration";
    private String intruder = null;
    public StartButton(StartScreen startScreen){
        setText("Start Exploration");
        setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                startScreen.startExploration(guard, intruder);
            }
        });
    }

    public void setGuard(String selectedItem) {
        this.guard = selectedItem;
    }

    public void setIntruder(String selectedItem) {
        this.intruder = selectedItem;
    }
}
