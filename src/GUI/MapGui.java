package nl.maastrichtuniversity.dke.explorer;

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

    public MapGui(){
    }

    public MapGui(Scenario scenario){
        this.scenario = scenario;
        mapHeight = scenario.mapHeight;
        mapWidth = scenario.mapWidth;
        scaling = scenario.getScaling();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Pane root = createPane();
        Scene scene = new Scene(root, 600, 600);
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
            r1.setFill(Color.WHEAT);
            p.getChildren().add(r1);
        }

        Rectangle r2 = null;
        for(TelePortal t : scenario.getTeleportals()){
            r2 = t.createRec();
            r2.setFill(Color.NAVY);
            p.getChildren().add(r2);
        }

        Rectangle r3 = null;
        for(Area s : scenario.getShaded()){
            r3 = s.createRec();
            r3.setFill(Color.GREEN);
            p.getChildren().add(r3);
        }

        //Setting spawn point and target
        Rectangle spawnAreaGuards = scenario.spawnAreaGuards.createRec();
        p.getChildren().add(spawnAreaGuards);

        Rectangle spawnAreaIntruders = scenario.spawnAreaIntruders.createRec();
        p.getChildren().add(spawnAreaIntruders);

        Rectangle targetArea = scenario.targetArea.createRec();
        p.getChildren().add(targetArea);

        //Setting Guards
        double[][] spawnGuards = scenario.spawnGuards();
        for(int i=0; i< scenario.getNumGuards(); i++){
            Circle c = new Circle((int) spawnGuards[i][0], (int)spawnGuards[i][1], (int) spawnGuards[i][2]);
            p.getChildren().add(c);
        }

        //Setting intruders
        double[][] spawnIntruders = scenario.spawnIntruders();
        for(int i=0; i< scenario.numIntruders; i++){
            Circle c = new Circle((int) spawnIntruders[i][0], (int)spawnIntruders[i][1], (int) spawnIntruders[i][2]);
            p.getChildren().add(c);
        }

        return p;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
