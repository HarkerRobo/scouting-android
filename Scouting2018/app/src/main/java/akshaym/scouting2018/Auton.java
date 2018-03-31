package akshaym.scouting2018;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class Auton extends AppCompatActivity {

    Button teleop;
    ToggleButton baseline;
    Boolean baselineCrossed;
    Tournament currentTournament;
    Scouting currentScouting;
    Button ownSwitch;
    Button oppSwitch;
    Button vault;
    Button scale;
    int ownSwitchCount;
    int oppSwitchCount;
    int scaleCount;
    int vaultCount;
    String ownSwitchBase;
    String oppSwitchBase;
    String scaleBase;
    String vaultBase;
    int redSwitchPos = 1; //0 is red possession, 1 is netural, 2 is blue possession
    int tempRedSwitch = 1;
    int blueSwitchPos = 1; //0 is red possession, 1 is neutral, 2 is blue possession
    int tempBlueSwitch = 1;
    int scalePos = 1; //same schema as above two
    int tempScale = 1;

    boolean isSergeant;
    static public ArrayList<Long> timeStamps;
    static public ArrayList<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentScouting = LoginActivity.currentScouting;
        currentTournament = LoginActivity.currentTournament;
        setTitle("Team # "+currentScouting.getTeamNumber()+" Color "+ (currentScouting.isBlue() ? "Blue":"Red"));

        if(!currentScouting.isSergeant()) {
            setContentView(R.layout.activity_auton);
            isSergeant = false;
        }else{
            setContentView(R.layout.activity_auton_sergeant);
            isSergeant = true;
        }
        /*
        setContentView(R.layout.activity_auton_sergeant);
        isSergeant = true;
        */
        ((SeekBar) findViewById(R.id.startingposition)).bringToFront();
        list = new ArrayList<String>();
        timeStamps = new ArrayList<Long>();
        Teleop();
        baseline();
        findViewById(R.id.autonUndo)                                                                                                                                                                                                                                                                                                                                                                                                                                                          .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undo();
            }
        });
        if(!isSergeant) {
            switches();
            scale();
            vault();
           // pileAndLines();
        }else{
            setChangeButtons();
        }
    }

    public void Teleop() {
        teleop = (Button) findViewById(R.id.AutonToTeleop);

        teleop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toy = new Intent(Auton.this,Input.class);
                SeekBar startingPosition = (SeekBar) findViewById(R.id.startingposition);
                int progress = startingPosition.getProgress();
                toy.putExtra("Starting Position", (progress*2)-100);
                toy.putExtra("Baseline Crossed", baselineCrossed);
               // toy.putExtra("Auton Timestamps", timeStamps.toArray(new Long[timeStamps.size()]));
               // toy.putExtra("Auton Actions", list);
               // System.out.println(list);
                startActivity(toy);
            }
        });
    }

    public void switches(){
        ownSwitch = (Button) findViewById(R.id.AutonOwnSwitch);
        oppSwitch = (Button) findViewById(R.id.AutonOppSwitch);
        ownSwitchBase = LoginActivity.getButtonColor("Switch", currentScouting.isBlue(), true);
        ownSwitchCount = 0;
        ownSwitch.setText(ownSwitchBase+": "+ownSwitchCount);
        oppSwitchBase = LoginActivity.getButtonColor("Switch", currentScouting.isBlue(), false);
        oppSwitchCount = 0;
        oppSwitch.setText(oppSwitchBase+": "+oppSwitchCount);
        ownSwitch.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                list.add("0_0_0");
                ownSwitchCount++;
                ownSwitch.setText(ownSwitchBase+": "+ (ownSwitchCount));
                timeStamps.add(System.currentTimeMillis());
            }
        });

        oppSwitch.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                list.add("0_0_2");
                oppSwitchCount++;
                oppSwitch.setText(oppSwitchBase+": "+oppSwitchCount);
                timeStamps.add(System.currentTimeMillis());
            }
        });
    }

    public void scale(){
        scale = (Button) findViewById(R.id.AutonScale);
        scaleBase = "Scale";
        scaleCount = 0;
        scale.setText(scaleBase+": "+scaleCount);
        scale.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                list.add("0_0_1");
                scaleCount++;
                scale.setText(scaleBase+": "+scaleCount);
                timeStamps.add(System.currentTimeMillis());
            }
        });
    }
/*
    public void pileAndLines(){
        pile = (Button) findViewById(R.id.button4);
        oppPCubeLine = (Button) findViewById(R.id.AutonOtherLine);
        ownPCubeLine = (Button) findViewById(R.id.AutonOwnLine);
        ownPCubeLine.setText(LoginActivity.getButtonColor("PCube Line", currentScouting.isBlue(), true));
        oppPCubeLine.setText(LoginActivity.getButtonColor("PCube Line", currentScouting.isBlue(), false));
        pile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.add("0_1_2");
                timeStamps.add(System.currentTimeMillis());
            }
        });
        oppPCubeLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.add("0_1_4");
                timeStamps.add(System.currentTimeMillis());
            }
        });
        ownPCubeLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.add("0_1_3");
                timeStamps.add(System.currentTimeMillis());
            }
        });
    }
*/
    public void vault(){
        vault = (Button) findViewById(R.id.AutonVault);
        vaultBase = vault.getText().toString();
        vaultCount = 0;
        vault.setText(vaultBase+": "+vaultCount);
        vault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.add("0_0_3");
                vaultCount++;
                vault.setText(vaultBase+": "+vaultCount);
                timeStamps.add(System.currentTimeMillis());
            }
        });
    }

    public void baseline() {
        baseline = (ToggleButton) findViewById(R.id.baseline);
        baseline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    baselineCrossed = true;
                } else {
                    baselineCrossed = false;
                }
            }
        });
    }

    public void setChangeButtons(){
         Button redSwitch = (Button) findViewById(R.id.AutonChangeRedSwitch);
         Button blueSwitch = (Button) findViewById(R.id.AutonChangeBlueSwitch);
         final Button scale = (Button) findViewById(R.id.AutonChangeScale);
         redSwitch.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 AlertDialog.Builder builder = new AlertDialog.Builder(Auton.this);
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
                         list.add("10_0_"+String.valueOf(redSwitchPos));
                         timeStamps.add(System.currentTimeMillis());
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Auton.this);
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
                        list.add("10_0_"+String.valueOf(6+blueSwitchPos));
                        timeStamps.add(System.currentTimeMillis());
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Auton.this);
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
                        list.add("10_0_"+String.valueOf(scalePos+3));
                        timeStamps.add(System.currentTimeMillis());
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


    public void undo(){
        if(list.size()==0||timeStamps.size()==0) return;
        String action_id = list.remove(list.size()-1);
        timeStamps.remove(timeStamps.size()-1);
        if(action_id.equals("0_0_0")){
            //own switch
            ownSwitchCount--;
            ownSwitch.setText(ownSwitchBase+": "+ownSwitchCount);
        }else if(action_id.equals("0_0_1")){
            //scale
            scaleCount--;
            scale.setText(scaleBase+": "+scaleCount);
        }else if(action_id.equals("0_0_2")){
            //away switch
            oppSwitchCount--;
            oppSwitch.setText(oppSwitchBase+": "+oppSwitchCount);
        }else if(action_id.equals("0_0_3")){
            //vault
            vaultCount--;
            vault.setText(vaultBase+": "+vaultCount);
        }
    }


}
