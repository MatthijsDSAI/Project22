package GUI;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import controller.GameRunner;
import controller.GraphicsConnector;
import controller.Scenario;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.Utils;

import java.util.ArrayList;
import java.util.Random;

public class MapGui extends Application {

    private Scenario scenario;
    private int mapHeight;
    private int mapWidth;
    private int col;
    private int row;
    private double scaling;
    private GameRunner gr;
    private Color[][] map;
    private static GraphicsConnector graphicsConnector;
    private Timeline t;
    private BorderPane mapPane;
    private BorderPane root;
    private BorderPane p;
    private Stage stage;
    private double width = 1210;
    private double height = 810;
    private StartScreen mainMenu;
    public MapGui(){
    }

    public MapGui(Scenario scenario){
        mapHeight = scenario.getMapHeight();
        mapWidth = scenario.getMapWidth();
        scaling = scenario.getScaling();
    }

    @Override
    public void start(Stage stage) throws Exception {
        graphicsConnector.setGui(this);
        this.stage = stage;
        map = graphicsConnector.getMapOfColors();

        row = map.length;
        col = map[0].length;
        mapHeight = 810;
        mapWidth = 1210;
        //root = createPane();
        //Scene scene = new Scene(root, mapWidth, mapHeight);
        this.mainMenu = new StartScreen(this);
        Scene startMenu = new Scene(mainMenu, width, height);


//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Information Dialog");
//        alert.setHeaderText(null);
//        alert.setContentText("Game is over");
//        alert.showAndWait();

        stage.setTitle("MAP");
        stage.setScene(startMenu);
        stage.show();
//        graphicsConnector.setGui(this);
//        this.stage = stage;
//        this.mainMenu = new StartScreen(this);
//
//        stage.setResizable(false);
//        stage.setScene(startMenu);
//
//        stage.show();
    }

    public BorderPane createPane(){
        p = new BorderPane();

        // initial paint
        drawMap(p);
        //update();

        //initTimeLine(p);
        //t.play();

        return p;
    }


    //Main draw method
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

    //Animation for future
    public void initTimeLine(BorderPane p){
        t = new Timeline();
        //We could set step count here, for now is infinite
        t.setCycleCount(Timeline.INDEFINITE);
//        t.setCycleCount(10);
        KeyFrame k = new KeyFrame(Duration.millis(985), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                drawMap(p);
                //stage.getScene().setRoot(p);
                update();
                graphicsConnector.getMapOfColors();

            }
        });
        t.getKeyFrames().add(k);
        t.play();

    }

    //Update for testing
    //We could use getColorMap method here for updating
    public void update(){
        //map = graphicsConnector.getMapOfColors();
        Random r = new Random();
        map[r.nextInt(map.length)][r.nextInt(map[0].length)] = Color.BLACK;
    }

    public void updateGraphics(){
        drawMap(mapPane);
    }

    public void start(){
        t.play();
    }

    public void pause(){
        t.pause();
    }

    public void stop(){
        t.stop();
    }
    public void launchGUI(GraphicsConnector graphicsConnector){
        MapGui.graphicsConnector = graphicsConnector;
        String[] args  = new String[0];
        launch(args);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void startExploration() {
        mapPane = createPane();
        stage.getScene().setRoot(mapPane);
        graphicsConnector.run();
        initTimeLine(mapPane);
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }
}
