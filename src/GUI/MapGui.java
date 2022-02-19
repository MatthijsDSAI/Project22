package GUI;

import agents.HumanPlayer;
import controller.Area;
import controller.GameRunner;
import controller.Map.tiles.Tile;
import controller.Scenario;
import controller.TelePortal;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class MapGui extends Application {

    private Scenario scenario;
    private int mapHeight;
    private int mapWidth;
    private double scaling;
    private GameRunner gr;
    private HumanPlayer player;
    private Tile[][] map;

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
        map = gr.getMap().getMap();

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

        //Setting wall, teleportal, shaded
        Rectangle r1 = null;
        for(Area wall : scenario.getWalls()){
            r1 = wall.createRec();
            r1.setFill(Color.BLACK);
            p.getChildren().add(r1);
        }

        Rectangle r2 = null;
        for(TelePortal t : scenario.getTeleportals()){
            r2 = t.createRec();
            r2.setFill(Color.YELLOW);
            p.getChildren().add(r2);
        }

        Rectangle r3 = null;
        for(Area s : scenario.getShaded()){
            r3 = s.createRec();
            r3.setFill(Color.GREY);
            p.getChildren().add(r3);
        }

        //Setting spawn point and target
        Rectangle spawnAreaGuards = scenario.getSpawnAreaGuards().createRec();
        spawnAreaGuards.setFill(Color.BLUE);
        p.getChildren().add(spawnAreaGuards);

        //The spawn area of intruders have the same location as the guards for testmap
//        Rectangle spawnAreaIntruders = scenario.getSpawnAreaIntruders().createRec(scaling);
//        spawnAreaIntruders.setFill(Color.BROWN);
//        p.getChildren().add(spawnAreaIntruders);

        Rectangle targetArea = scenario.getTargetArea().createRec();
        targetArea.setFill(Color.RED);
        p.getChildren().add(targetArea);

        //Setting Guards
        double[][] spawnGuards = gr.spawnGuards();
        for(int i=0; i< scenario.getNumGuards(); i++){
            Circle c = new Circle();
            c.setCenterX(spawnGuards[i][0]);
            c.setCenterY(spawnGuards[i][1]);
            c.setRadius(5);
            c.setFill(Color.BLACK);
            p.getChildren().add(c);
        }

        //Setting intruders
        double[][] spawnIntruders = gr.spawnIntruders();
        for(int i=0; i< scenario.getNumIntruders(); i++){
            Circle c = new Circle();
            c.setCenterX(spawnIntruders[i][0]);
            c.setCenterY(spawnIntruders[i][1]);
            c.setRadius(5);
            c.setFill(Color.GREEN);
            p.getChildren().add(c);
        }
        //the human player
        //i cant figure out how to repaint when coordinates change
        Circle c = new Circle();
        c.setCenterX(player.getX());
        c.setCenterY(player.getY());
        c.setRadius(5);
        c.setFill(Color.YELLOW);
        p.getChildren().add(c);
        return p;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
