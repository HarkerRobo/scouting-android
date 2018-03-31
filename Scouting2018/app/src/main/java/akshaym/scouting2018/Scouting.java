package akshaym.scouting2018;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by adven on 3/6/2018.
 */

public class Scouting {

    private int round;
    private int rank;
    private boolean isSergeant;
    private boolean isBlue;
    private int teamNumber = -1;

    public Scouting(JSONObject jsonObject){
        try{
            System.out.println(jsonObject.toString(4));

            round = jsonObject.getInt("round");
            rank = jsonObject.getInt("rank");
            if(rank==0){//private
                isSergeant = false;
                isBlue = jsonObject.getBoolean("blue");
                teamNumber = jsonObject.getInt("team");
            }else{
                isSergeant = true;
            }
        }catch(JSONException joe){
            System.out.println("oops");
        }
    }

    public Scouting(){
        round = 1;
        rank = 10;
        isSergeant = true;
        isBlue = true;
        teamNumber = 1072;
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

