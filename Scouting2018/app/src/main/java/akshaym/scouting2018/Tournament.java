package akshaym.scouting2018;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by adven on 3/6/2018.
 */

public class Tournament {

    private int year;
    private String tourneyName;
    private String id;

    public Tournament(JSONArray jsonArray){
        try {
            year = jsonArray.getInt(0);
            tourneyName = jsonArray.getString(1);
            id = jsonArray.getString(2);
        }catch(JSONException joe){
            System.out.println("oops");
        }
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
