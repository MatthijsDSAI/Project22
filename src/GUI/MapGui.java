package GUI;

import controller.GameRunner;
import controller.GraphicsConnector;
import controller.Map.tiles.Tile;
import controller.Scenario;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
    private BorderPane root;

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
        map = graphicsConnector.getMapOfColors();

        row = map.length;
        col = map[0].length;
        mapHeight = 810;
        mapWidth = 1210;

        root = createPane();
        Canvas c = new Canvas();
        Scene scene = new Scene(root, mapWidth, mapHeight);
        stage.setTitle("MAP");
        stage.setScene(scene);
        stage.show();
    }

    public BorderPane createPane(){
        BorderPane p = new BorderPane();

//        ArrayList<Tile> guardSpawn = new ArrayList<>();
//        ArrayList<Tile> intruderSpawn = new ArrayList<>();

        drawMap(p);
//        initTimeLine(p);
//        Random r = new Random();
//        for(int i=0; i< scenario.getNumGuards(); i++){
//            Tile t = guardSpawn.get(r.nextInt(guardSpawn.size()));
////            t.addAgent();
//            t.setColor(Color.BLACK);
//        }

//        for(int i=0; i< scenario.getNumIntruders(); i++){
//            Tile t = intruderSpawn.get(r.nextInt(intruderSpawn.size()));
////            t.addAgent();
//            t.setColor(Color.BLACK);
//        }
        return p;
    }

    public void drawMap(BorderPane p){
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
        t.setCycleCount(Timeline.INDEFINITE);
        KeyFrame k = new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //need update
                drawMap(p);
//                update();
            }
        });
        t.getKeyFrames().add(k);
    }

    public void update(){
        Random r = new Random();
        map[r.nextInt(map.length)][r.nextInt(map[0].length)] = Color.BLACK;
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

    public void keyBoardPressed(KeyEvent event){
        if(event.getCode() == KeyCode.LEFT){

        }else if(event.getCode() == KeyCode.RIGHT){

        }else if(event.getCode() == KeyCode.UP){

        }else if(event.getCode() == KeyCode.DOWN){

        }
    }

    public void launchGUI(GraphicsConnector graphicsConnector){
        MapGui.graphicsConnector = graphicsConnector;
        String[] args  = new String[0];
        launch(args);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
