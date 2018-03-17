package ca.lakeeffect.scoutingapp;

import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Ajay on 9/25/2016.
 */
public class TeleopPage extends Fragment implements View.OnClickListener {

    SurfaceView surface;
    Field field;

    Button pickup;
    Button drop;
    Button undo;
    Button fail;
    Button failedDropOff;

    //All the events made by the person this round
    ArrayList<Event> events = new ArrayList<Event>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState){

        View view = inflator.inflate(R.layout.teleoppage, container, false);

        surface = (SurfaceView) view.findViewById(R.id.fieldCanvas);
        field = new Field(surface, BitmapFactory.decodeResource(getResources(), R.drawable.field));
        surface.setOnTouchListener(field);

        pickup = (Button) view.findViewById(R.id.pickupButton);
        pickup.setOnClickListener(this);
        drop = (Button) view.findViewById(R.id.dropButton);
        drop.setOnClickListener(this);
        undo = (Button) view.findViewById(R.id.undo);
        undo.setOnClickListener(this);
        fail = (Button) view.findViewById(R.id.failButton);
        fail.setOnClickListener(this);
        failedDropOff = (Button) view.findViewById(R.id.failDropOffButton);
        failedDropOff.setOnClickListener(this);

        view.setTag("page2");

        return view;
    }

    @Override
    public void onClick(View v) {

        if(v == undo){

            if(events.size() > 0){

                Event event = events.get(events.size()-1);

                String location = "";

                if(field.selected != -1) {
                    location += "location " + field.selected;
                } else{
                    location += "the field";
                }

                new AlertDialog.Builder(getContext())
                        .setTitle("Confirm")
                        .setMessage("Are you sure you would like to undo the action that said " + getActionText(event.eventType) + location + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                events.remove(events.size()-1);
                            }
                        })
                        .setNegativeButton("No", null)
                        .create()
                        .show();
            } else {
                Toast.makeText(getContext(), "There is nothing to undo, you have not made any events yet", Toast.LENGTH_SHORT).show();
            }

            return; //only have to undo, not add an event
        }


        final Event event;
        int eventType = -1;

        String action = "";

        if(v == pickup) {
            eventType = 0;
        } else if(v == drop) {
            eventType = 1;
        } else if(v == fail) {
            eventType = 2;
        }else if(v == failedDropOff) {
            eventType = 3;
        }

        action = getActionText(eventType);

        if(field.selected != -1) {
            action += "location " + field.selected;
        } else{
            action += "the field";
        }

        if(eventType != -1){

            event = new Event(eventType, field.selected, System.currentTimeMillis(), 0);

            new AlertDialog.Builder(getContext())
                    .setTitle("Confirm")
                    .setMessage("Are you sure you would like to say " + action + "?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            events.add(event);
                        }
                    })
                    .setNegativeButton("No", null)
                    .create()
                    .show();
        }
    }

    public String getActionText(int eventType){
        switch (eventType){
            case 0:
                return "that the robot picked up from ";
            case 1:
                return "that the robot dropped onto ";
            case 2:
                return "that the robot failed picking up in ";
            case 3:
                return "that the robot failed dropping off in ";
        }
        return "invalid event";
    }

    public void reset(){
        events.clear();
    }

    public String[] getData(){
        StringBuilder labels = new StringBuilder();
        StringBuilder data = new StringBuilder();

        int scaleHit=0;
        int scaleMiss=0;
        int ownSwitchHit=0;
        int ownSwitchMiss=0;
        int otherSwitchHit=0;
        int otherSwitchMiss=0;
        int vaultHit=0;
        int vaultMiss=0;

        for(Event e : events){
            int location = e.location;
            if(e.eventType==1){
                if(location==2){
                    vaultHit++;
                }
                if(location==5||location==6){
                    ownSwitchHit++;
                }
                if(location==7||location==8){
                    scaleHit++;
                }
                if(location==9||location==10){
                    otherSwitchHit++;
                }
            }
            if(e.eventType==3){
                if(location==2){
                    vaultMiss++;
                }
                if(location==5||location==6){
                    ownSwitchMiss++;
                }
                if(location==7||location==8){
                    scaleMiss++;
                }
                if(location==9||location==10){
                    otherSwitchMiss++;
                }
            }
        }

        labels.append("Own Switch Cubes,");
        data.append(ownSwitchHit+",");
        labels.append("Own Switch Miss,");
        data.append(ownSwitchMiss+",");
        labels.append("Scale Cubes,");
        data.append(scaleHit+",");
        labels.append("Scale Miss,");
        data.append(scaleMiss+",");
        labels.append("Other Switch Cubes,");
        data.append(otherSwitchHit+",");
        labels.append("Other Switch Miss,");
        data.append(otherSwitchMiss+",");

        return(new String[] {labels.toString(), data.toString()});
    }

}
