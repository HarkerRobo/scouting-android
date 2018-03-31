package akshaym.scouting2018;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by adven on 3/6/2018.
 */

public class Tournament {

    private int year;
    private String tourneyName;
    private String id;

    public Tournament(JSONObject jsonObject){

        try {
            System.out.println(jsonObject.toString(4));
            year = jsonObject.getInt("year");
            tourneyName = jsonObject.getString("name");
            id = jsonObject.getString("id");
        }catch(JSONException joe){
            System.out.println("oops");
        }
    }

    public Tournament(){
        year = 2018;
        tourneyName = "Silicon Valley Regional";
        id = "59b1";
    }

    public int getYear() {
        return year;
    }

    public String getId() {
        return id;
    }

    public String getTourneyName() {
        return tourneyName;
    }
}
