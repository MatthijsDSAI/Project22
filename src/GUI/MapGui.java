package GUI;

import controller.GameRunner;
import controller.GraphicsConnector;
import controller.Map.tiles.Tile;
import controller.Scenario;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import utils.Utils;

import java.util.ArrayList;

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
        mapHeight = 800;
        mapWidth = 1200;

        Pane root = createPane();
        Scene scene = new Scene(root, mapWidth, mapHeight);
        stage.setTitle("MAP");
        stage.setScene(scene);
        stage.show();
    }

    public Pane createPane(){
        Pane p = new Pane();

        ArrayList<Tile> guardSpawn = new ArrayList<>();
        ArrayList<Tile> intruderSpawn = new ArrayList<>();

        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                Rectangle r = new Rectangle(j*10, i*10, 10, 10);

                //if(map[i][j].hasAgent()) {
                    r.setStroke(Color.WHITE);
                    r.setFill(map[i][j]);
                    p.getChildren().add(r);
//                }else if(map[i][j].toString().equals("Floor")){
//                    r.setStroke(Color.WHITE);
//                    r.setFill(Color.GREEN);
//                    p.getChildren().add(r);
//                }else if(map[i][j].toString().equals("Wall")){
//                    r.setStroke(Color.WHITE);
//                    r.setFill(Color.BLACK);
//                    p.getChildren().add(r);
//                }else if(map[i][j].toString().equals("Shaded")){
//                    r.setFill(Color.GRAY);
//                    p.getChildren().add(r);
//                }else if(map[i][j].toString().equals("Teleportals")){
//                    r.setFill(Color.YELLOW);
//                    p.getChildren().add(r);
//                }else if(map[i][j].toString().equals("SpawnAreaGuards")){
//                    guardSpawn.add(map[i][j]);
//                    r.setFill(Color.BLUE);
//                    p.getChildren().add(r);
//                }else if(map[i][j].toString().equals("SpawnAreaIntruders")){
//                    intruderSpawn.add(map[i][j]);
//                    r.setFill(Color.BROWN);
//                    p.getChildren().add(r);
//                }else if(map[i][j].toString().equals("TargetArea")){
//                    r.setFill(Color.RED);
//                    p.getChildren().add(r);
//                }
            }
        }

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

    //Animation for future
    public void autoMove(){
//        ParallelTransition p = new ParallelTransition();
//        for(Circle c : guards){
//            TranslateTransition tt = new TranslateTransition(new Duration(10000), c);
//            tt.setByX(500);
//            tt.setCycleCount(5);
//            tt.setAutoReverse(true);
//            p.getChildren().add(tt);
//        }
//        for(Circle c : intruders){
//            TranslateTransition tt = new TranslateTransition(new Duration(10000), c);
//            tt.setByX(500);
//            tt.setAutoReverse(true);
//            p.getChildren().add(tt);
//        }
//        p.play();
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
