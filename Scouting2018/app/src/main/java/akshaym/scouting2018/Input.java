package akshaym.scouting2018;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;

import java.util.ArrayList;

public class Input extends AppCompatActivity {
    public Button rSwitch;
    public Button bSwitch;
    public Button bVault;
    public Button scale;
    public Button done;

    int bSwitchCount;
    int rSwitchCount;
    int scaleCount;
    int bVaultCount;
    String bSwitchBase;
    String rSwitchBase;
    String scaleBase;
    String bVaultBase;

    int redSwitchPos = 1; //0 is red possession, 1 is netural, 2 is blue possession
    int tempRedSwitch = 1;
    int blueSwitchPos = 1; //0 is red possession, 1 is neutral, 2 is blue possession
    int tempBlueSwitch = 1;
    int scalePos = 1; //same schema as above two
    int tempScale = 1;

    public int startingPosition;
    public boolean baselineCrossed;
   // public long[] autonTimeStamps;
    //public ArrayList<String> autonActions;
    public static ArrayList<String> teleopActions;
    public static ArrayList<Long> teleopTimeStamps;


    Tournament currentTournament;
    Scouting currentScouting;
    boolean isSergeant;

    JSONArray data = new JSONArray();


    private static final String TAG = "ScoutingApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        startingPosition = getIntent().getIntExtra("Starting Position", 0);
        baselineCrossed = getIntent().getBooleanExtra("Baseline Crossed", true);
      //  autonActions = getIntent().getStringArrayListExtra("Auton Actions");
       // autonTimeStamps = getIntent().getLongArrayExtra("Auton Timestamps");

        teleopActions = new ArrayList<>();
        teleopTimeStamps = new ArrayList<>();

        currentScouting = LoginActivity.currentScouting;
        currentTournament = LoginActivity.currentTournament;

        setTitle("Team # "+currentScouting.getTeamNumber()+" Color "+ (currentScouting.isBlue() ? "Blue":"Red"));



        if(!currentScouting.isSergeant()) {
            setContentView(R.layout.activity_input);
            isSergeant = false;
        }else{
            setContentView(R.layout.activity_input_sergeant);
            isSergeant = true;
        }

/*
        setContentView(R.layout.activity_input_sergeant);
        isSergeant = true; */

        done();
        findViewById(R.id.teleopUndo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undo();
            }
        });
        if (!isSergeant) {
            switches();
            vault();
            //pileAndLines();
           // portals();
            scale();
        } else {
            setPowerUps();
            setChangeButtons();
        }

    }


    public void done() {
        done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toy = new Intent(Input.this,Final.class);
                toy.putExtra("Starting Position", startingPosition);
                toy.putExtra("Baseline Crossed", baselineCrossed);
             //   toy.putExtra("Auton Timestamps", autonTimeStamps);
              //  toy.putExtra("Auton Actions", autonActions);
                toy.putExtra("Teleop Timestamps", teleopTimeStamps.toArray(new Long[teleopTimeStamps.size()]));
                toy.putExtra("Teleop Actions", teleopActions);
                // System.out.println(list);
                startActivity(toy);
            }
        });
    }



    public void switches(){
        bSwitch = (Button) findViewById(R.id.bswitch);
        rSwitch = (Button) findViewById(R.id.rswitch);
        bSwitchBase = LoginActivity.getButtonColor("Switch", currentScouting.isBlue(), true);
        bSwitchCount = 0;
        bSwitch.setText(bSwitchBase+": "+bSwitchCount);
        rSwitchBase = LoginActivity.getButtonColor("Switch", currentScouting.isBlue(), false);
        rSwitchCount = 0;
        rSwitch.setText(rSwitchBase+": "+rSwitchCount);
        bSwitch.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                teleopActions.add("0_0_0");
                bSwitchCount++;
                bSwitch.setText(bSwitchBase+": "+bSwitchCount);
                teleopTimeStamps.add(System.currentTimeMillis());
            }
        });

        rSwitch.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                teleopActions.add("0_0_2");
                rSwitchCount++;
                rSwitch.setText(rSwitchBase+": "+rSwitchCount);
                teleopTimeStamps.add(System.currentTimeMillis());
            }
        });
    }

    public void scale(){
        scale = (Button) findViewById(R.id.scale);
        scaleBase = "Scale";
        scaleCount = 0;
        scale.setText(scaleBase+": "+scaleCount);
        scale.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                teleopActions.add("0_0_1");
                scaleCount++;
                scale.setText(scaleBase+": "+scaleCount);
                teleopTimeStamps.add(System.currentTimeMillis());
            }
        });
    }
/*
    public void portals(){
        Button lOwnPortal = (Button) findViewById(R.id.button2);
        Button rOwnPortal = (Button) findViewById(R.id.button3);
        Button lOppPortal = (Button) findViewById(R.id.lPortal1);
        Button rOppPortal = (Button) findViewById(R.id.rPortal1);
        View.OnClickListener homePortal = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teleopActions.add("0_1_0");
                teleopTimeStamps.add(System.currentTimeMillis());
            }
        };
        lOwnPortal.setOnClickListener(homePortal);
        rOwnPortal.setOnClickListener(homePortal);
        lOwnPortal.setText(LoginActivity.getButtonColor("L Portal", currentScouting.isBlue(), true));
        rOwnPortal.setText(LoginActivity.getButtonColor("R Portal", currentScouting.isBlue(), true));
        View.OnClickListener awayPortal = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teleopActions.add("0_1_1");
                teleopTimeStamps.add(System.currentTimeMillis());
            }
        };
        lOppPortal.setOnClickListener(awayPortal);
        rOppPortal.setOnClickListener(awayPortal);
        lOppPortal.setText(LoginActivity.getButtonColor("L Portal", currentScouting.isBlue(), false));
        rOppPortal.setText(LoginActivity.getButtonColor("R Portal", currentScouting.isBlue(), false));

    }

    public void pileAndLines(){
        bPile = (Button) findViewById(R.id.bPile);
        bPile.setText(LoginActivity.getButtonColor("Pile", currentScouting.isBlue(), true));
        teleopOwnLine = (Button) findViewById(R.id.TeleopOwnLine);
        teleopOppLine = (Button) findViewById(R.id.TeleopOtherLine);
        teleopOwnLine.setText(LoginActivity.getButtonColor("PCube Line", currentScouting.isBlue(), true));
        teleopOppLine.setText(LoginActivity.getButtonColor("PCube Line", currentScouting.isBlue(), false));
        bPile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teleopActions.add("0_1_2");
                teleopTimeStamps.add(System.currentTimeMillis());
            }
        });
        teleopOppLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teleopActions.add("0_1_4");
                teleopTimeStamps.add(System.currentTimeMillis());
            }
        });
        teleopOwnLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teleopActions.add("0_1_3");
                teleopTimeStamps.add(System.currentTimeMillis());
            }
        });
    }

*/
    public void vault(){
        bVault = (Button) findViewById(R.id.teleopVault);
        bVaultBase = bVault.getText().toString();
        bVaultCount = 0;
        bVault.setText(bVaultBase+": "+bVaultCount);
        bVault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teleopActions.add("0_0_3");
                bVaultCount++;
                bVault.setText(bVaultBase+": "+bVaultCount);
                teleopTimeStamps.add(System.currentTimeMillis());
            }
        });
    }


    public void setPowerUps(){
        Button rForce = (Button) findViewById(R.id.Red_force);
        Button rBlock = (Button) findViewById(R.id.Red_block);
        Button rLev = (Button) findViewById(R.id.Red_levitate);
        Button bForce = (Button) findViewById(R.id.Blue_force);
        Button bBlock = (Button) findViewById(R.id.Blue_block);
        Button bLev = (Button) findViewById(R.id.Blue_levitate);
        rForce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teleopActions.add("10_1_0");
                teleopTimeStamps.add(System.currentTimeMillis());
            }
        });
        rBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teleopActions.add("10_1_2");
                teleopTimeStamps.add(System.currentTimeMillis());
            }
        });
        rLev.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                teleopActions.add("10_1_4");
                teleopTimeStamps.add(System.currentTimeMillis());
            }
        });
        bForce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teleopActions.add("10_1_1");
                teleopTimeStamps.add(System.currentTimeMillis());
            }
        });
        bBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teleopActions.add("10_1_3");
                teleopTimeStamps.add(System.currentTimeMillis());
            }
        });
        bLev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teleopActions.add("10_1_5");
                teleopTimeStamps.add(System.currentTimeMillis());
            }
        });
    }

    public void setChangeButtons(){
        Button redSwitch = (Button) findViewById(R.id.TeleopChangeRedSwitch);
        Button blueSwitch = (Button) findViewById(R.id.TeleopChangeBlueSwitch);
        Button scale = (Button) findViewById(R.id.TeleopChangeScale);
        redSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Input.this);
                String[] redOptions = {"Red Possession", "Neutral", "Blue Possession"};
                builder.setTitle("Set Red Switch Possession")
                        .setSingleChoiceItems(redOptions, 1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tempRedSwitch = which;
                            }
                        });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        redSwitchPos = tempRedSwitch;
                        teleopActions.add("10_0_"+String.valueOf(redSwitchPos));
                        teleopTimeStamps.add(System.currentTimeMillis());
                        tempRedSwitch = 1;
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        blueSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Input.this);
                String[] blueOptions = {"Red Possession", "Neutral", "Blue Possession"};
                builder.setTitle("Set Blue Switch Possession")
                        .setSingleChoiceItems(blueOptions, 1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tempBlueSwitch = which;
                            }
                        });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        blueSwitchPos = tempBlueSwitch;
                        teleopActions.add("10_0_"+String.valueOf(6+blueSwitchPos));
                        teleopTimeStamps.add(System.currentTimeMillis());
                        tempBlueSwitch = 1;
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        scale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Input.this);
                String[] scaleOptions = {"Red Possession", "Neutral", "Blue Possession"};
                builder.setTitle("Set Scale Possession")
                        .setSingleChoiceItems(scaleOptions, 1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tempScale = which;
                            }
                        });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        scalePos = tempScale;
                        teleopActions.add("10_0_"+String.valueOf(scalePos+3));
                        teleopTimeStamps.add(System.currentTimeMillis());
                        tempScale = 1;
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    public  void undo(){
        if(teleopActions.size()==0 || teleopTimeStamps.size()==0) return;
        String action_id = teleopActions.remove(teleopActions.size()-1);
        teleopTimeStamps.remove(teleopTimeStamps.size()-1);
        if(action_id.equals("0_0_0")){
            //own switch
            bSwitchCount--;
            bSwitch.setText(bSwitchBase+": "+bSwitchCount);
        }else if(action_id.equals("0_0_1")){
            //scale
            scaleCount--;
            scale.setText(scaleBase+": "+scaleCount);
        }else if(action_id.equals("0_0_2")){
            //away switch
            rSwitchCount--;
            rSwitch.setText(rSwitchBase+": "+rSwitchCount);
        }else if(action_id.equals("0_0_3")){
            //vault
            bVaultCount--;
            bVault.setText(bVaultBase+": "+bVaultCount);
        }
    }

}
