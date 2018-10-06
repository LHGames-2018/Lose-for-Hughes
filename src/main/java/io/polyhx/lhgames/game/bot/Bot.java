package io.polyhx.lhgames.game.bot;

import io.polyhx.lhgames.game.GameInfo;
import io.polyhx.lhgames.game.Map;
import io.polyhx.lhgames.game.tile.*;
import io.polyhx.lhgames.game.Player;
import io.polyhx.lhgames.game.action.IAction;
import io.polyhx.lhgames.game.point.Point;

import java.util.List;


public class Bot extends BaseBot {

    Boolean returnHomeSess = false;

    public IAction getAction(Map map, Player player, List<Player> others, GameInfo info) {

    System.out.println("PLAYER: "+player.getPosition().getX()+","+player.getPosition().getY());


    ResourceTile closest = map.getResources().get(0);
        for(ResourceTile tile: map.getResources()) {
            double distance = player.getPosition().getDistanceTo(tile.getPosition());
            if (distance < closest.getDistanceTo(player.getPosition())) {
                closest=tile;
            }
        }

      
    return seDeplacerVersUneTile(map,player,closest.getPosition());
    //return createMoveAction(Point.LEFT);
    }

public IAction seDeplacerVersUneTile (Map map,Player player,Point tile){

    //VERIF BESOIN DE RETOURNER A LA MAISON (SAC PLEIN)
    System.out.println("target"+tile.getX()+","+tile.getY());
    if(shouldReturnHome (player)){
        returnHomeSess = true;
        System.out.println("JSUIS PLEIN");
        System.out.println("house"+player.getHousePosition().getX()+","+player.getHousePosition().getY());
        tile = player.getHousePosition();

    }
    //VERIF SI FARM SESS
    System.out.println("Ressources du joueur:"+player.getCarriedResource());
    if(map.getTileLeftOf(player.getPosition()).isResource()==true){
        System.out.println("JE FARM");
   
        return createCollectAction(new Point(-1,0));
       
    }
    if(map.getTileRightOf(player.getPosition()).isResource()==true){
        System.out.println("JE FARM");
        return createCollectAction(new Point(1,0));
    }
    if(map.getTileAboveOf(player.getPosition()).isResource()==true){
        System.out.println("JE FARM UP");
        return createCollectAction(new Point(0,1));
    }
    if(map.getTileBelowOf(player.getPosition()).isResource()==true){
        System.out.println("JE FARM ");
        return createCollectAction(new Point(0,-1));
    }

        if(player.getPosition().getX()-tile.getX()==1){
             return createMoveAction(Point.LEFT);
        }
        if(player.getPosition().getX()-tile.getX()==-1){
            return createMoveAction(Point.RIGHT);
        }
        if(player.getPosition().getX()-tile.getX()>1 ){
            return createMoveAction(Point.LEFT);
        }else if(player.getPosition().getX()-tile.getX()<1 && player.getPosition().getX()-tile.getX()!=0){
            return createMoveAction(Point.RIGHT);
        }


        if(player.getPosition().getX()-tile.getX()==0){
            System.out.println("im here");

            if(player.getPosition().getY()-tile.getY()==0){
                return createMoveAction(new Point(0,0));
            }
            if(player.getPosition().getY()-tile.getY()>=1 ){
                return createMoveAction(Point.UP);
            }else if(player.getPosition().getY()-tile.getY()<=1){
                return createMoveAction(Point.DOWN);
            }
        }
    

        //if(player.getPosition().getX()-TileX==1 && player.getPosition().getY()-TileY==1){
            return createMoveAction(new Point(0,0));
}


private boolean shouldReturnHome (Player player) {
    return player.getResourceCapacity() == player.getCarriedResource();
}
   // }

}



