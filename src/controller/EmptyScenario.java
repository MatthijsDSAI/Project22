package controller;

import java.util.ArrayList;

public class EmptyScenario extends Scenario {

    public EmptyScenario(Scenario s){
        //terrible, but I would say we really do not want to stick with storing everything on txt files but just in memory
        //TODO: need to have a scenario that ideally has all of the functions without having the terrible path structure that scenario itself has
        //thinking about rewriting scneario completely
        super(s.mapDoc);
        walls = new ArrayList<>(); // create list of walls
        shaded = new ArrayList<>(); // create list of low-visibility areas
        teleports = new ArrayList<>(); // create list of teleports e.g. stairs
    }

    //TODO: define when map is explored
    public boolean isExplored(){
        return false;
    }
}
