package torcs.simple;

import torcs.scr.Action;
import torcs.scr.Driver;
import torcs.scr.SensorModel;

/*
 * Coded by Emirhan, Bilel, Görkem
 *
 * Simple controller as a starting point to develop your own one - accelerates
 * slowly - tries to maintain a constant speed (only accelerating, no braking) -
 * stays in first gear - steering follows the track and avoids to come too close
 * to the edges
 */
public class BlackStorm extends Driver {
  
  // counting each time that control is called
  private int tickcounter = 0;
  
  public Action control(SensorModel m) {
    
    // adjust tick counter
    tickcounter++;
    
    // check, if we just started the race
    if (tickcounter == 1) {
      System.out.println("This is BlackStorm on track "
      + getTrackName());
      System.out.println("This is a race "
      + (damage ? "with" : "without") + " damage.");
    }
    
    // create new action object to send our commands to the server
    Action action = new Action();
    
    // ---------------- compute target speed ----------------------
    
    // very basic behaviour. stay safe
    double targetSpeed = 360;
    
    /*
    * ----------------------- control velocity --------------------
    */
    
    // simply accelerate until we reach our target speed.
    if (m.speed < targetSpeed) {
      action.accelerate = Math.min((targetSpeed - m.speed) / 10, 1);
    } else {
      action.brake = Math.min((m.speed - targetSpeed) / 10, 1);
    }
    assert action.brake * action.accelerate < 0.1;
    
    // ------------------- control gear ------------------------
    
    // go in second gear
    
    if (m.speed < 60) {
      action.gear = 1;
    } // end of if
    else if (m.speed < 110) {
      action.gear = 2;
    } // end of if 
    else if (m.speed < 150) {
      action.gear = 3;  
    } // end of if
    else if (m.speed < 180) {
      action.gear = 4;
    } // end of if  
    else if (m.speed < 240) {
      action.gear = 5;
    } // end of if
    else if (m.speed < 280) {
      action.gear = 6;
    } else {
      action.gear = 6;
    } // end of if-else

     // ------------------- Kurvenerkennung ------------------------ 
       /* Vor einer Kurve wird die Geschwindkeit verringert, damit
        * das Auto nicht von der Rennstrecke abkommt. 
        */
    
    
    /*
    * ----------------------- control steering ---------------------
    */
    
    double distanceLeft = m.trackEdgeSensors[0];
    double distanceRight = m.trackEdgeSensors[18];
    
    // follow the track
    action.steering = m.angleToTrackAxis * 0.75;
    
    // avoid to come too close to the edges
    if (distanceLeft < 3.0) {
      action.steering -= (5.0 - distanceLeft) * 0.05;
    }
    if (distanceRight < 3.0) {
      action.steering += (5.0 - distanceRight) * 0.05;
    }
    
    // return the action
    return action;
  }

  public void shutdown() {
    System.out.println("Bye Bye!");
  }
}
