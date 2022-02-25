package GUI;

import controller.GameRunner;
import controller.Map.tiles.Tile;
import controller.Scenario;
import controller.TelePortal;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class MapGui extends Application {

    private Scenario scenario;
    private int mapHeight;
    private int mapWidth;
    private int col;
    private int row;
    private double scaling;
    private GameRunner gr;
    private Tile[][] map;
    private static TelePortal.GraphicsConnector graphicsConnector;
    public MapGui(){
    }

    public MapGui(Scenario scenario){
        mapHeight = scenario.getMapHeight();
        mapWidth = scenario.getMapWidth();
        scaling = scenario.getScaling();
    }

    @Override
    public void start(Stage stage) throws Exception {


        scenario = new Scenario("testmap.txt");
        gr = new GameRunner(scenario);
        map = gr.getMap().getTiles();
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

//        ArrayList<Tile> guardSpawn = new ArrayList<>();
//        ArrayList<Tile> intruderSpawn = new ArrayList<>();

        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                Rectangle r = new Rectangle(map[i][j].getX(), map[i][j].getY(), 10, 10);
                if(map[i][j].getTypeAsString().equals("floor")){
                    r.setStroke(Color.WHITE);
                    r.setFill(Color.GREEN);
                    p.getChildren().add(r);
                }else if(map[i][j].getTypeAsString().equals("wall")){
                    r.setStroke(Color.WHITE);
                    r.setFill(Color.BLACK);
                    p.getChildren().add(r);
                }else if(map[i][j].getTypeAsString().equals("shaded")){
                    r.setFill(Color.GRAY);
                    p.getChildren().add(r);
                }else if(map[i][j].getTypeAsString().equals("teleportals")){
                    r.setFill(Color.YELLOW);
                    p.getChildren().add(r);
                }else if(map[i][j].getTypeAsString().equals("spawnAreaGuards")){
//                    guardSpawn.add(map[i][j]);
                    r.setFill(Color.BLUE);
                    p.getChildren().add(r);
                }else if(map[i][j].getTypeAsString().equals("spawnAreaIntruders")){
//                    intruderSpawn.add(map[i][j]);
                    r.setFill(Color.BROWN);
                    p.getChildren().add(r);
                }else if(map[i][j].getTypeAsString().equals("targetArea")){
                    r.setFill(Color.RED);
                    p.getChildren().add(r);
                }
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
    
    public void launchGUI(TelePortal.GraphicsConnector graphicsConnector){
        MapGui.graphicsConnector = graphicsConnector;
        String[] args  = new String[0];
        launch(args);
    }
    public static void main(String[] args) {
        
        launch(args);
    }
}
