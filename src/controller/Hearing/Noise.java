package controller.Hearing;

import controller.Map.Map;
import controller.Map.tiles.Tile;

public class Noise {

    public static void removeNoises(Map map, Tile newTile) {
        boolean debug = false;
        double numOfLoops = 2;
        int x = newTile.getX();
        int y = newTile.getY();

        if(debug) System.out.println("Current tile x:" + (x) + ", y:" + (y));
        newTile.setSound(0); // set agent's current position to 0, no sound

        for (int i=1; i <= numOfLoops; i++) {
            int forX = -i;
            int forY = 0;

            while (forY != -i) {
                if(debug) System.out.println("at x:" + (x + forX) + ", y:" + (y + forY));
                if ((x + forX >= 0) && (y + forY >= 0) && (x + forX) < map.getHorizontalSize() && (y + forY) < map.getVerticalSize()) { // to avoid out of bounds error
                    if (map.getTile(x + forX, y + forY).isWalkable()) {
                        map.getTile(x + forX, y + forY).setSound(0);
                    }
                }
                forX++;
                forY--;
            }

            while (forX != i) {
                if(debug) System.out.println("at x:" + (x + forX) + ", y:" + (y + forY));
                if ((x + forX >= 0) && (y + forY >= 0) && (x + forX) < map.getHorizontalSize() && (y + forY) < map.getVerticalSize()) { // to avoid out of bounds error
                    if (map.getTile(x + forX, y + forY).isWalkable()) {
                        if (map.getTile(x + forX, y + forY).isWalkable()) {
                            map.getTile(x + forX, y + forY).setSound(0);
                        }
                    }
                }
                forX++;
                forY++;
            }

            while (forY != i) {
                if(debug) System.out.println("at x:" + (x + forX) + ", y:" + (y + forY));
                if ((x + forX >= 0) && (y + forY >= 0) && (x + forX) < map.getHorizontalSize() && (y + forY) < map.getVerticalSize()) { // to avoid out of bounds error
                    if (map.getTile(x + forX, y + forY).isWalkable()) {
                        if (map.getTile(x + forX, y + forY).isWalkable()) {
                            map.getTile(x + forX, y + forY).setSound(0);
                        }
                    }
                }
                forX--;
                forY++;
            }

            while (forX != -i) {
                if(debug) System.out.println("at x:" + (x + forX) + ", y:" + (y + forY));
                if ((x + forX >= 0) && (y + forY >= 0) && (x + forX) < map.getHorizontalSize() && (y + forY) < map.getVerticalSize()) { // to avoid out of bounds error
                    if (map.getTile(x + forX, y + forY).isWalkable()) {
                        if (map.getTile(x + forX, y + forY).isWalkable()) {
                            map.getTile(x + forX, y + forY).setSound(0);
                        }
                    }
                }
                forX--;
                forY--;
            }
        }
        if(debug) System.out.println("finished");
    }

    public static void addNoises(Map map, Tile newTile) {
        boolean debug = false;
        double numOfLoops = 2;
        int x = newTile.getX();
        int y = newTile.getY();
        double decreasingVal = 1 / (numOfLoops + 1);

        if(debug) System.out.println("Current tile x:" + (x) + ", y:" + (y));
        newTile.setSound(1); // set agent's current position to 1, max sound

        for (int i=1; i <= numOfLoops; i++) {
            int forX = -i;
            int forY = 0;

            while (forY != -i) {
                if(debug) System.out.println("at x:" + (x + forX) + ", y:" + (y + forY));
                if ((x + forX >= 0) && (y + forY >= 0) && (x + forX) < map.getHorizontalSize() && (y + forY) < map.getVerticalSize()) { // to avoid out of bounds error
                    if (map.getTile(x + forX, y + forY).isWalkable()) {

                        if (map.getTile(x + forX, y + forY).getSound() < 1 - decreasingVal * i) { // if that tile's current sound is higher skip
                            map.getTile(x + forX, y + forY).setSound(1 - decreasingVal * i);
                        }
                    }
                }
                forX++;
                forY--;
            }

            while (forX != i) {
                if(debug) System.out.println("at x:" + (x + forX) + ", y:" + (y + forY));
                if ((x + forX >= 0) && (y + forY >= 0) && (x + forX) < map.getHorizontalSize() && (y + forY) < map.getVerticalSize()) { // to avoid out of bounds error
                    if (map.getTile(x + forX, y + forY).isWalkable()) {

                        if (map.getTile(x + forX, y + forY).getSound() < 1) { // if that tile's current sound is higher skip
                            map.getTile(x + forX, y + forY).setSound(1 - decreasingVal * i);
                        }
                    }
                }
                forX++;
                forY++;
            }

            while (forY != i) {
                if(debug) System.out.println("at x:" + (x + forX) + ", y:" + (y + forY));
                if ((x + forX >= 0) && (y + forY >= 0) && (x + forX) < map.getHorizontalSize() && (y + forY) < map.getVerticalSize()) { // to avoid out of bounds error
                    if (map.getTile(x + forX, y + forY).isWalkable()) {

                        if (map.getTile(x + forX, y + forY).getSound() < 1) { // if that tile's current sound is higher skip
                            map.getTile(x + forX, y + forY).setSound(1 - decreasingVal * i);
                        }
                    }
                }
                forX--;
                forY++;
            }

            while (forX != -i) {
                if(debug) System.out.println("at x:" + (x + forX) + ", y:" + (y + forY));
                if ((x + forX >= 0) && (y + forY >= 0) && (x + forX) < map.getHorizontalSize() && (y + forY) < map.getVerticalSize()) { // to avoid out of bounds error
                    if (map.getTile(x + forX, y + forY).isWalkable()) {

                        if (map.getTile(x + forX, y + forY).getSound() < 1) { // if that tile's current sound is higher skip
                            map.getTile(x + forX, y + forY).setSound(1 - decreasingVal * i);
                        }
                    }
                }
                forX--;
                forY--;
            }
        }
        if(debug) System.out.println("finished");

    }
}
