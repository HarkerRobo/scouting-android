package akshaym.scouting2018;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE;

public class Final extends AppCompatActivity {

    public Button done;
    public int startingPosition;
    public boolean baselineCrossed;
 // public long[] autonTimeStamps;
 // public ArrayList<String> autonActions;
 // public ArrayList<String> teleopActions;
 // public long[] teleopTimeStamps;

    public int lift = 0;

    public boolean endOnPlaform = false;

    Tournament currentTournament;
    Scouting currentScouting;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        startingPosition = getIntent().getIntExtra("Starting Position", 0);
        baselineCrossed = getIntent().getBooleanExtra("Baseline Crossed", false);
        //autonActions = getIntent().getStringArrayListExtra("Auton Actions");
        //autonTimeStamps = getIntent().getLongArrayExtra("Auton Timestamps");
        //teleopActions = getIntent().getStringArrayListExtra("Teleop Actions");
        //teleopTimeStamps = getIntent().getLongArrayExtra("Teleop Timestamps");
        currentScouting = LoginActivity.currentScouting;
        currentTournament = LoginActivity.currentTournament;

        setTitle("Team # "+currentScouting.getTeamNumber()+" Color "+ (currentScouting.isBlue() ? "Blue":"Red"));
        ToggleButton end_platform = (ToggleButton) findViewById(R.id.end_platform);
        end_platform.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                endOnPlaform = isChecked;
            }
        });
        RadioGroup liftGroup = (RadioGroup) findViewById(R.id.radio_lift);
        liftGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.radio_0){
                    lift = 0;
                }else if(checkedId==R.id.radio_1){
                    lift = 1;
                }else if(checkedId==R.id.radio_2){
                    lift = 2;
                }else if(checkedId==R.id.radio_3){
                    lift = 3;
                }else if(checkedId==R.id.radio_4){
                    lift = 4;
                }else if(checkedId==R.id.radio_5){
                    lift = 5;
                }
            }
        });
        done();

    }
    public void done() {
        done = (Button) findViewById(R.id.doneFromFinal);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new sendFinalInfo().execute();
            }
        });
    }

    private class sendFinalInfo extends AsyncTask<Void, Void, Void>{

        JSONObject finalObject;
        String comments;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            comments = ((EditText) findViewById(R.id.comments)).getText().toString();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(Looper.myLooper()==null) {
                Looper.prepare();
            }
            JSONObject header = new JSONObject();
            JSONObject data = new JSONObject();
            finalObject = new JSONObject();
            JSONArray auton = new JSONArray();
            JSONArray teleop = new JSONArray();
            JSONObject bobject;
            try {
                header.put("email", LoginActivity.email);
                header.put("rank", currentScouting.getRank());
                header.put("blue", currentScouting.isBlue());
                header.put("team", currentScouting.getTeamNumber());
                header.put("round", currentScouting.getRound());
                header.put("tournament_id", currentTournament.getId());
                header.put("forceUpload", true);
                finalObject.put("headers", header);

                data.put("start_position", startingPosition);
                data.put("crossed_line", baselineCrossed);
                data.put("end_platform", endOnPlaform);
                data.put("lift", lift);
                data.put("comments", comments);

                for(int i=0; i<Auton.timeStamps.size(); i++){
                    bobject = new JSONObject();
                    bobject.put("timestamp", Auton.timeStamps.get(i));
                    bobject.put("action", Auton.list.get(i));
                    auton.put(bobject);
                }
                data.put("auton-actions", auton);

                for(int i=0; i<Input.teleopTimeStamps.size(); i++){
                    bobject = new JSONObject();
                    bobject.put("timestamp", Input.teleopTimeStamps.get(i));
                    bobject.put("action", Input.teleopActions.get(i));
                    teleop.put(bobject);
                }
                data.put("teleop-actions", teleop);
                System.out.println(header.toString(4));
                System.out.println(data.toString(4));
                finalObject.put("data", data);
                System.out.println(finalObject.toString(4));
            }catch(JSONException joe){
                Toast.makeText(Final.this, "JSON error at Final", Toast.LENGTH_SHORT).show();
                Log.d("FinalActivity", "JSON Error");
            }



            HttpClient httpClient = LoginActivity.httpClient;
            HttpPost httpPost = new HttpPost("https://robotics.harker.org/member/scouting/upload");
            String TAG = "FinalActivity";
            try {
                System.out.println(finalObject.toString());
                httpPost.setEntity(new StringEntity(finalObject.toString()));
                httpPost.setHeader(new BasicHeader("Content-Type", "application/json"));
                HttpResponse response = httpClient.execute(httpPost);
                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println(statusCode);
                final String responseBody = EntityUtils.toString(response.getEntity());
                Log.i(TAG, "Signed in as: " + responseBody);
            } catch (ClientProtocolException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            } catch (IOException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            }
            Toast.makeText(Final.this, "Scouting data successfully sent to server", Toast.LENGTH_SHORT).show();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent results = new Intent(Final.this, LoginActivity.class);
            results.putExtra("isFinal", true);
            startActivity(results);
        }
    }
}
