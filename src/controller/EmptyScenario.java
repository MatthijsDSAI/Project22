package controller;

import java.util.ArrayList;

public class EmptyScenario extends Scenario {

    public EmptyScenario(Scenario s){
        //terrible, but I would say we really do not want to stick with storing everything on txt files but just in memory
        //TODO: fix this bad inheritance
        super(null);
        walls = new ArrayList<>(); // create list of walls
        shaded = new ArrayList<>(); // create list of low-visibility areas
        teleports = new ArrayList<>(); // create list of teleports e.g. stairs
    }


}
