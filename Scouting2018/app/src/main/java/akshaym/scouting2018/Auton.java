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
    Button pile;
    Button ownPCubeLine;
    Button oppPCubeLine;
    int redSwitchPos = 1; //0 is red possession, 1 is netural, 2 is blue possession
    int tempRedSwitch = 1;
    int blueSwitchPos = 1; //0 is red possession, 1 is neutral, 2 is blue possession
    int tempBlueSwitch = 1;
    int scalePos = 1; //same schema as above two
    int tempScale = 1;

    boolean isSergeant;
    ArrayList<Long> timeStamps;
    ArrayList<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentScouting = LoginActivity.currentScouting;
        currentTournament = LoginActivity.currentTournament;

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
        list = new ArrayList<String>();
        timeStamps = new ArrayList<Long>();
        Teleop();
        baseline();
        if(!isSergeant) {
            switches();
            scale();
            vault();
            pileAndLines();
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
                toy.putExtra("Auton Timestamps", timeStamps.toArray(new Long[timeStamps.size()]));
                toy.putExtra("Auton Actions", list);
               // System.out.println(list);
                startActivity(toy);
            }
        });
    }

    public void switches(){
        ownSwitch = (Button) findViewById(R.id.AutonOwnSwitch);
        oppSwitch = (Button) findViewById(R.id.AutonOppSwitch);
        ownSwitch.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                list.add("0_0_0");
                timeStamps.add(System.currentTimeMillis());
            }
        });

        oppSwitch.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                list.add("0_0_2");
                timeStamps.add(System.currentTimeMillis());
            }
        });
    }

    public void scale(){
        scale = (Button) findViewById(R.id.AutonScale);
        scale.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                list.add("0_0_1");
                timeStamps.add(System.currentTimeMillis());
            }
        });
    }

    public void pileAndLines(){
        pile = (Button) findViewById(R.id.button4);
        oppPCubeLine = (Button) findViewById(R.id.AutonOtherLine);
        ownPCubeLine = (Button) findViewById(R.id.AutonOwnLine);
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

    public void vault(){
        vault = (Button) findViewById(R.id.AutonVault);
        vault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.add("0_0_3");
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


}
