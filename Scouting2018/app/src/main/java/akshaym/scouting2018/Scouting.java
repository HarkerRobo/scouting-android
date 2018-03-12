package akshaym.scouting2018;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by adven on 3/6/2018.
 */

public class Scouting {

    private int round;
    private int rank;
    private boolean isSergeant;
    private boolean isBlue;
    private int teamNumber = -1;

    public Scouting(JSONArray jsonArray){
        try{
            round = jsonArray.getInt(0);
            rank = jsonArray.getInt(1);
            if(rank==0){//private
                isSergeant = false;
                isBlue = jsonArray.getBoolean(2);
                teamNumber = jsonArray.getInt(3);
            }else{
                isSergeant = true;
            }
        }catch(JSONException joe){
            System.out.println("oops");
        }
    }

    public int getRank() {
        return rank;
    }

    public int getRound() {
        return round;
    }

    public int getTeamNumber() {
        return teamNumber;
    }

    public boolean isSergeant() {
        return isSergeant;
    }

    public boolean isBlue() {
        return isBlue;
    }
}

