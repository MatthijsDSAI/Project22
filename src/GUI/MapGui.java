package GUI;

import controller.GraphicsConnector;
import controller.Scenario;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.Utils;

import java.util.Arrays;
import java.util.Random;

public class MapGui extends Application {

    private int col;
    private int row;
    private Color[][] map;
    private static GraphicsConnector graphicsConnector;
    private Timeline t;
    private BorderPane mapPane;
    private BorderPane p;
    private Stage stage;
    private double width;
    private double height;
    private StartScreen mainMenu;

    @Override
    public void start(Stage stage) throws Exception {
        graphicsConnector.setGui(this);
        this.stage = stage;
        map = graphicsConnector.getMapOfColors();
        row = map.length;
        col = map[0].length;
        height = row*5;
        width = col*5;
        this.mainMenu = new StartScreen(this);
        Scene startMenu = new Scene(mainMenu, width, height);
        stage.setTitle("MAP");
        stage.setScene(startMenu);
        stage.show();
    }



    public void drawMap(BorderPane p){
        map = graphicsConnector.getMapOfColors();
        p.getChildren().clear();
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                Rectangle r = new Rectangle(j * 10, i * 10, 10, 10);
                r.setStroke(Color.BLACK);
                r.setFill(map[i][j]);
                p.getChildren().add(r);
            }
        }
    }

    public void initTimeLine(BorderPane p){
        t = new Timeline();
        t.setCycleCount(Timeline.INDEFINITE);

        KeyFrame k = new KeyFrame(Duration.millis(Scenario.config.getSleep()-50), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                drawMap(p);
            }
        });
        t.getKeyFrames().add(k);
        t.play();
    }


    public void startExploration(String guard, String intruder) {
        mapPane = createPane();
        stage.getScene().setRoot(mapPane);
        graphicsConnector.initGameRunner(guard, intruder);
        graphicsConnector.run();
        initTimeLine(mapPane);
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public BorderPane createPane(){
        p = new BorderPane();
        drawMap(p);
        return p;
    }

    public void launchGUI(GraphicsConnector graphicsConnector){
        MapGui.graphicsConnector = graphicsConnector;
        String[] args  = new String[0];
        launch(args);
    }
}
