package GUI;

import controller.Area;
import controller.Scenario;
import controller.TelePortal;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.awt.*;

public class MapGui extends Application {

    private Scenario scenario = new Scenario("testmap.txt");
    private int mapHeight;
    private int mapWidth;
    private double scaling;

    public MapGui(){
    }

    public MapGui(Scenario scenario){
        this.scenario = scenario;
        mapHeight = scenario.getMapHeight();
        mapWidth = scenario.getMapWidth();
        scaling = scenario.getScaling();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Pane root = createPane();
        Scene scene = new Scene(root, 1200, 700);
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
        double[][] spawnGuards = scenario.spawnGuards();
        for(int i=0; i< scenario.getNumGuards(); i++){
            Circle c = new Circle();
            c.setCenterX(spawnGuards[i][0]);
            c.setCenterY(spawnGuards[i][1]);
            c.setRadius(5);
            c.setFill(Color.BLACK);
            p.getChildren().add(c);
        }

        //Setting intruders
        double[][] spawnIntruders = scenario.spawnIntruders();
        for(int i=0; i< scenario.getNumIntruders(); i++){
            Circle c = new Circle();
            c.setCenterX(spawnIntruders[i][0]);
            c.setCenterY(spawnIntruders[i][1]);
            c.setRadius(5);
            c.setFill(Color.GREEN);
            p.getChildren().add(c);
        }

        return p;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
