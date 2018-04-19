package akshaym.scouting2018;

import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Activity to demonstrate basic retrieval of the Google user's ID, email address, and basic
 * profile.
 */
public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    GoogleSignInAccount currentAccount = null;

    private String studentID;

    private GoogleSignInClient mGoogleSignInClient;
    private TextView mStatusTextView;

    public static String email = "";

    static Task<GoogleSignInAccount> completedTask;
    //static Intent data;

    protected AlertDialog alertDialog;

    static String currentRound;

    static Tournament currentTournament;

     static Scouting currentScouting;

     public static HttpClient httpClient;

    EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Views
        mStatusTextView = findViewById(R.id.status);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);
        findViewById(R.id.button_request_spot).setOnClickListener(this);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id)) 
                .requestScopes(new Scope(Scopes.PROFILE))
                .requestEmail()
                .build();
        //Toast.makeText(this, getString(R.string.server_client_id), Toast.LENGTH_SHORT).show();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END build_client]


        currentAccount = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(currentAccount);
        if(currentAccount!=null){
            if(!getIntent().getBooleanExtra("isFinal", false)) {
                Log.d(TAG, "current account recognized");
                new AsyncGet().execute();
            }
        }else{
            Log.d(TAG, "no current acct");
        }

        // [START customize_button]
        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);
        // [END customize_button]
    }

    @Override
    public void onStart() {
        super.onStart();

        // [START on_start_sign_in]
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        // [END on_start_sign_in]
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            completedTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            new AsyncGet().execute();
            //handleSignInResult(task);
        }
    }
    // [END onActivityResult]
/*
        // [START handleSignInResult]
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        Toast.makeText(this, "in handle sign in", Toast.LENGTH_SHORT).show();
        GoogleSignInAccount account = completedTask.getResult();

        String idToken = account.getIdToken();
        ((TextView) findViewById(R.id.status)).setText(("Welcome, "+account.getDisplayName()+"!"));

        studentID = account.getEmail().substring(0, account.getEmail().indexOf("@"));

        Toast.makeText(this, "Welcome, "+studentID+"!", Toast.LENGTH_SHORT ).show();

        updateUI(account);

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("https://robotics.harker.org/member/token");

        try {
            List nameValuePairs = new ArrayList(1);
            nameValuePairs.add(new BasicNameValuePair("idToken", idToken));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

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
        // Signed in successfully, show authenticated UI.
        updateUI(account);
            /*
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
            */ /*

    }
    */
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            mStatusTextView.setText(getString(R.string.signed_in_fmt, account.getDisplayName()));

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            findViewById(R.id.button_request_spot).setVisibility(View.VISIBLE);
        } else {
            //Toast.makeText(this, "it's still null", Toast.LENGTH_SHORT).show();
            mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
            findViewById(R.id.button_request_spot).setVisibility(View.INVISIBLE);
        }
    }

    private void requestSpot(){
        input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add round number")
                .setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                currentRound = input.getText().toString();
                //Toast.makeText(LoginActivity.this, currentRound, Toast.LENGTH_SHORT).show();
                new AsyncRequestRound().execute();
                //requestSpotWithRound(input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }
/*
    private void requestSpotWithRound(String round){
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("https://robotics.harker.org/member/scouting/request/"+round);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode==200){ //means everything's gucci - get the necessary information
                InputStream is = response.getEntity().getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String json = "";
                String data = br.readLine();
                while(data!=null){
                    json+=data+"\n";
                    data = br.readLine();
                }
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(json);
                    currentTournament = new Tournament(jsonObject.getJSONArray("tournament"));
                    currentScouting = new Scouting(jsonObject.getJSONArray("scouting"));
                    if(currentScouting.isSergeant()){

                    }
                }catch(JSONException joe) {
                    System.out.println("You done messed up David");
                }
            }else if(statusCode==404){ //round not valid
                Toast.makeText(this, "Round number does not exist", Toast.LENGTH_SHORT).show();
                requestSpot();
            }else if(statusCode==422){ //round is not an integer
                Toast.makeText(this, "Round number not valid", Toast.LENGTH_SHORT).show();
                requestSpot();
            }
        }catch(IOException ioe){
            //suck it up buttercup
            ioe.printStackTrace();
        }
    }
*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                //Toast.makeText(this, "ur signed in", Toast.LENGTH_SHORT).show();
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
            case R.id.button_request_spot:
                requestSpot();
                //new AsyncRequest().execute();
        }
    }
    private class AsyncGet extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pdLoading = new ProgressDialog(LoginActivity.this);
        GoogleSignInAccount account;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.setCanceledOnTouchOutside(false);
            pdLoading.show();
        }

        protected Void doInBackground(Void... params) {
            if(Looper.myLooper()==null) {
                Looper.prepare();
            }
            if(currentAccount==null) {
               // Toast.makeText(LoginActivity.this, "in handle sign in", Toast.LENGTH_SHORT).show();
                try {
                    account = completedTask.getResult(ApiException.class);
                } catch (ApiException e) {
                    System.out.println("API EXCEPTION");
                    e.printStackTrace();
                }
            }else{
                account = currentAccount;
            }
            String idToken = account.getIdToken();
            System.out.println(idToken);
            Toast.makeText(LoginActivity.this, "Welcome, "+account.getDisplayName()+"!", Toast.LENGTH_SHORT).show();

            studentID = account.getEmail().substring(0, account.getEmail().indexOf("@"));
            email = account.getEmail();
            System.out.println(studentID);

            //Toast.makeText(LoginActivity.this, "Welcome, "+studentID+"!", Toast.LENGTH_SHORT ).show();


            if(httpClient == null) {
                httpClient = new DefaultHttpClient();
            }
            HttpPost httpPost = new HttpPost("https://robotics.harker.org/member/token");
            try {
                //List nameValuePairs = new ArrayList(1);
                //nameValuePairs.add(new BasicNameValuePair("android", "true"));
                //nameValuePairs.add(new BasicNameValuePair("idtoken", idToken));
                //System.out.println(nameValuePairs);
                //UrlEncodedFormEntity blah = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
                //System.out.println(nameValuePairs);
                //System.out.println(blah);
                /*
                JSONObject jobject = new JSONObject();
                try {
                    jobject.put("idtoken", idToken);
                    jobject.put("android", true);
                    System.out.println(jobject.toString(4));
                }catch(JSONException joe){
                    System.out.println("error at putting in tokens");
                }
                */
                String value = "idtoken="+idToken;

                StringEntity blah = new StringEntity(value);
                blah.setContentType("application/x-www-form-urlencoded");
                httpPost.setHeader(new BasicHeader("X-Requested-With", "XMLHttpRequest"));
                httpPost.setEntity(blah);

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
            // Signed in successfully, show authenticated UI.
            //updateUI(account);
            /*
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
            */
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            updateUI(account);
            pdLoading.dismiss();
        }

    }

    private class AsyncRequestRound extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pdLoading = new ProgressDialog(LoginActivity.this);

        boolean error = false;
        boolean full = false;
        int statusCode;

        //Task<GoogleSignInAccount> completedTask;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.setCanceledOnTouchOutside(false);
            //completedTask = LoginActivity.completedTask;
            pdLoading.show();
        }

        protected Void doInBackground(Void... params) {
            if(Looper.myLooper()==null) {
                Looper.prepare();
            }
            if(getIntent().getBooleanExtra("isFinal", false)){
                //Looper.prepare();
            }
            String url = "https://robotics.harker.org/member/scouting/request/"+String.valueOf(currentRound);
            System.out.println(url);
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));

            LoginActivity.currentTournament = new Tournament();
            LoginActivity.currentScouting = new Scouting();
            try {
                HttpResponse response = httpClient.execute(httpGet);
                statusCode = response.getStatusLine().getStatusCode();
                System.out.println(statusCode);
                if(statusCode==409){
                   full = true;
                }else if(statusCode==500){
                    InputStream is = response.getEntity().getContent();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String json = "";
                    String data = br.readLine();
                    while(data!=null){
                        json+=data+"\n";
                        data = br.readLine();
                    }
                    JSONObject jobject = new JSONObject(json);
                    System.out.println(jobject.toString(4));
                    String message = jobject.getJSONObject("error").getString("message");
                    if(message.equals("No spots available")){
                        full = true;
                    }
                }
                if(statusCode==200){ //means everything's gucci - get the necessary information
                    InputStream is = response.getEntity().getContent();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String json = "";
                    String data = br.readLine();
                    while(data!=null){
                        json+=data+"\n";
                        data = br.readLine();
                    }
                    JSONObject jsonObject = null;

                    try {

                        System.out.println(json);


                        jsonObject = new JSONObject(json);

                        System.out.println(jsonObject.toString(4));

                        LoginActivity.currentTournament = new Tournament(jsonObject.getJSONObject("tournament"));
                        System.out.println(jsonObject.getJSONObject("tournament").toString(4));
                        LoginActivity.currentScouting = new Scouting(jsonObject.getJSONObject("scouting"));
                        System.out.println(jsonObject.getJSONObject("scouting").toString(4));

                        Log.d(TAG, "just passed current scouting");
                        if(LoginActivity.currentScouting.isSergeant()){
                            Toast.makeText(LoginActivity.this, "You are a Sergeant!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(LoginActivity.this, "You are a Private!", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(LoginActivity.this, "You are scouting Team# "+currentScouting.getTeamNumber()+" and color "+currentScouting.isBlue(), Toast.LENGTH_LONG).show();
                    }catch(JSONException joe) {
                        System.out.println("You done messed up David");
                    }
                }else if(statusCode==404){ //round not valid
                    Toast.makeText(LoginActivity.this, "Round number does not exist", Toast.LENGTH_SHORT).show();
                    error = true;
                    return null;
                }else if(statusCode==422){ //round is not an integer
                    Toast.makeText(LoginActivity.this, "Round number not valid", Toast.LENGTH_SHORT).show();
                    error = true;
                    return null;
                }
            }catch(IOException ioe){
                //suck it up buttercup
                System.out.println("Input output exception");
                ioe.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pdLoading.dismiss();
            if(statusCode==404){ //round not valid
                Toast.makeText(LoginActivity.this, "Round number does not exist", Toast.LENGTH_SHORT).show();
            }else if(statusCode==422){ //round is not an integer
                Toast.makeText(LoginActivity.this, "Round number not valid", Toast.LENGTH_SHORT).show();
            }
            if(error){
                requestSpot();
                error = false;
            }else{
                if(!full) {
                    Intent toAuton = new Intent(LoginActivity.this, Auton.class);
                    if(LoginActivity.currentScouting.isSergeant()){
                        Toast.makeText(LoginActivity.this, "You are a Sergeant!", Toast.LENGTH_SHORT).show();
                    }else{
                        //Toast.makeText(LoginActivity.this, "You are a Private!", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(LoginActivity.this, "You are scouting Team# "+currentScouting.getTeamNumber()+" and color "+(currentScouting.isBlue()?"Blue":"Red"), Toast.LENGTH_LONG).show();
                    startActivity(toAuton);
                }else{
                    Toast.makeText(LoginActivity.this, "Round is full.\nTry again next round.", Toast.LENGTH_LONG).show();
                    full = false;
                }
            }


        }

    }

    public static String getButtonColor(String name, boolean isBlue, boolean own){
        return (own?"Home "+name:"Away "+name);
       // if(!own) isBlue = !isBlue;
        //return (isBlue ? "Blue "+name: "Red "+name);
    }



}
