package io.polyhx.lhgames.game.bot;

import io.polyhx.lhgames.game.GameInfo;
import io.polyhx.lhgames.game.Map;
import io.polyhx.lhgames.game.Upgrade;
import io.polyhx.lhgames.game.tile.*;
import io.polyhx.lhgames.game.Player;
import io.polyhx.lhgames.game.Upgrade;
import io.polyhx.lhgames.game.action.IAction;
import io.polyhx.lhgames.game.point.Point;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Bot extends BaseBot {

    Boolean returnHomeSess = false;
    boolean isStuck = false;
    private int stuckCounter = 0;
    int lifePrice = 10000;
    private LinkedBlockingQueue<IAction> actionQueue = new LinkedBlockingQueue<>();


    public IAction getAction(Map map, Player player, List<Player> others, GameInfo info) {

        System.out.println("PLAYER: " + player.getPosition().getX() + "," + player.getPosition().getY());
        System.out.println(player.getCarriedResource() + " / " + player.getCapacityLevel());

        if(isHome(player) && player.getTotalResource() >= lifePrice) {
            System.out.println("Maison");
            lifePrice *= 2.5;
            return createUpgradeAction(Upgrade.COLLECTING_SPEED);
        }
        Tile closest = map.getTile(player.getHousePosition());
        if (!map.getResources().isEmpty())
            closest = map.getResources().get(0);
        
        isStuck = true;
        for (Tile tile : map.getResources()) {
            double distance = player.getPosition().getDistanceTo(tile.getPosition());
            if (distance <= closest.getDistanceTo(player.getPosition())) {
                if (dansRayon(player, tile.getPosition())) {
                    closest = tile;
                    isStuck = false;
                }
                    
            }
        }
        // return createMeleeAttackAction(Point.LEFT);
        return goTo(map, player, isStuck ? player.getHousePosition() : closest.getPosition());
        // return seDeplacerVersUneTile(map, player, closest.getPosition());
        // return createMoveAction(Point.LEFT);
    }

    private boolean adjacent (int x,int y) {
        return ((x == 0 && y == 1) || (x == 1 && y == 0));
    }

    private IAction goTo (Map map, Player player, Point tile) {
        if (!actionQueue.isEmpty())
            return actionQueue.poll();
        if (stuckCounter < 2) {
            if (stuckCounter == 0)
                {stuckCounter++; return createMoveAction(new Point(1, 0));}
            else {stuckCounter++; return createMoveAction(new Point(0, -1));}
        }
        Point target = tile;
        if (shouldReturnHome(player) || isStuck)
        {
            System.out.println("fuck");
            target = player.getHousePosition();
        }
        int deltaX = target.getX() - player.getPosition().getX();
        int deltaY = target.getY() - player.getPosition().getY();
        int xDiv = deltaX == 0 ? 1 : deltaX;
        int yDiv = deltaY == 0 ? 1 : deltaY;
        if (map.getTile(target).isResource() && adjacent(Math.abs(deltaX), Math.abs(deltaY))) {
            System.out.println("Farm");
            return createCollectAction(new Point(deltaX / Math.abs(xDiv), deltaY / Math.abs(yDiv)));
        }
        else {
            Point x = new Point(deltaX / Math.abs(xDiv), 0);
            Point y = new Point(0, deltaY / Math.abs(yDiv));
            Tile t = map.getTile(player.getPosition().getX() + x.getX(), player.getPosition().getY());
            Tile ty = map.getTile(player.getPosition().getX(), player.getPosition().getY() + y.getY());
            
            if (deltaX != 0)
            {
                System.out.println("X move: " + deltaX / Math.abs(xDiv) + ", " + 0);
                
                if (!t.isEmpty() && !t.isHouse()) {
                    Tile up = map.getTile(t.getX(), t.getY() + 1);
                    Tile down = map.getTile(t.getX(), t.getY() - 1);
                    Point v = up.isEmpty() ? new Point(0, -1) : new Point(0, 1); 
                    return createMoveAction(v);
                }
                return createMoveAction(x);
            }
            
            else 
            {
                System.out.println("Y move: " + 0 +", "+ deltaY / Math.abs(yDiv));
                if (!ty.isEmpty() && !ty.isHouse()) {
                    Tile right = map.getTile(ty.getX() + 1, ty.getY());
                    Tile left = map.getTile(ty.getX() - 1, ty.getY());
                    Point v1 = new Point(-1, 0);
                    Point v2 = new Point(0, 1);
                    if(!right.isEmpty()) {
                        v1 = new Point(-1, 0);
                        v2 = new Point(0, y.getY());
                    }
                    actionQueue.add(createMoveAction(v2));
                    return createMoveAction(v1);
                }
                return createMoveAction(y);
            }
        }
            
    }

    public IAction seDeplacerVersUneTile(Map map, Player player, Point tile) {
        // VERIF BESOIN DE RETOURNER A LA MAISON (SAC PLEIN)
        System.out.println("target" + tile.getX() + "," + tile.getY());
        if (shouldReturnHome(player)) {
            returnHomeSess = true;
            System.out.println("JSUIS PLEIN");
            System.out.println("house" + player.getHousePosition().getX() + "," + player.getHousePosition().getY());
            tile = player.getHousePosition();
        }
        // VERIF SI FARM SESS
        System.out.println("Ressources du joueur:" + player.getCarriedResource());
        if (map.getTileLeftOf(player.getPosition()).isResource() == true) {
            System.out.println("JE FARM");

            return createCollectAction(new Point(-1, 0));

        }
        if (map.getTileRightOf(player.getPosition()).isResource() == true) {
            System.out.println("JE FARM");
            return createCollectAction(new Point(1, 0));
        }
        if (map.getTileAboveOf(player.getPosition()).isResource() == true) {
            System.out.println("JE FARM UP");
            return createCollectAction(new Point(0, 1));
        }
        if (map.getTileBelowOf(player.getPosition()).isResource() == true) {
            System.out.println("JE FARM ");
            return createCollectAction(new Point(0, -1));
        }

        if (player.getPosition().getX() - tile.getX() == 1) {
            return createMoveAction(Point.LEFT);
        }
        if (player.getPosition().getX() - tile.getX() == -1) {
            return createMoveAction(Point.RIGHT);
        }
        if (player.getPosition().getX() - tile.getX() > 1) {
            return createMoveAction(Point.LEFT);
        } else if (player.getPosition().getX() - tile.getX() < 1 && player.getPosition().getX() - tile.getX() != 0) {
            return createMoveAction(Point.RIGHT);
        }

        if (player.getPosition().getX() - tile.getX() == 0) {
            System.out.println("im here");

            if (player.getPosition().getY() - tile.getY() == 0) {
                return createMoveAction(new Point(0, 0));
            }
            if (player.getPosition().getY() - tile.getY() >= 1) {
                return createMoveAction(Point.UP);
            } else if (player.getPosition().getY() - tile.getY() <= 1) {
                return createMoveAction(Point.DOWN);
            }
        }

        // if(player.getPosition().getX()-TileX==1 &&
        // player.getPosition().getY()-TileY==1){
        System.out.println("Fuck");
        return createMoveAction(Point.UP);
    }

    private boolean shouldReturnHome(Player player) {
        return player.getResourceCapacity() == player.getCarriedResource();
    }
    
    private boolean dansRayon (Player player, Point poi) {
        if (player.getHousePosition().getDistanceTo(poi) < 10.0) {
            System.out.println("dans rayon");
            return true;
        }
        System.out.println("pas dans rayon");
        return false;
    }

    private boolean isHome(Player player){

        int distanceX=player.getHousePosition().getX()-player.getPosition().getX();
        if(distanceX<0){
            distanceX=distanceX*-1;
        }
        int distanceY=player.getHousePosition().getY()-player.getPosition().getY();
        if(distanceY<0){
            distanceY=distanceY*-1;
        }
        if(distanceX<1){
            if(distanceY<1){
                return true;
            }
        }
        return false;
    }

}
