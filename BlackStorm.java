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
    double targetSpeed = 250;
    
    if (m.trackPosition > 0.8) {
      double distanceRight = m.trackEdgeSensors[18];
      action.steering += (4.0 - distanceRight) * 0.05;
    } else if (m.trackPosition < -0.8) {
      double distanceLeft = m.trackEdgeSensors[0];
      action.steering -= (4.0 - distanceLeft) * 0.05;
    } // end of if
    
    
    // ------------------- Kurvenerkennung ------------------------ 
    /* Vor einer Kurve wird die Geschwindkeit verringert, damit
    * das Auto nicht von der Rennstrecke abkommt. 
    */
    
    /*
    // zweite Variante
    double maxDist = 0;
    for (int i = 0; i < 18; i++) {
    maxDist = Math.max(maxDist, m.trackEdgeSensors[i]);
    } // end of for
    targetSpeed = maxDist; 
    */
    
    
    double distanceAhead = m.trackEdgeSensors[9];
    double distanceAheadRight = m.trackEdgeSensors[10];
    double distanceAheadLeft = m.trackEdgeSensors[8];
    
    if (distanceAhead < 70 && (distanceAheadRight < 60)) {
      m.trackPosition = -0.1;
      targetSpeed = 130;  
      action.brake = Math.min((m.speed - targetSpeed) / 10, 1);     
    } else {
      targetSpeed = 360;
    } // end of if-else
    
    if (distanceAhead < 70 && distanceAheadLeft < 60) {
      m.trackPosition = 0.1;
      targetSpeed = 130;  
      action.brake = Math.min((m.speed - targetSpeed) / 10, 1);     
    } else {
      targetSpeed = 360;
    } // end of if-else
    /*
    if (distanceAhead < 40) {
    targetSpeed = 100;
    action.brake = Math.min((m.speed - targetSpeed) / 10, 1);
    } // end of if
    */
    
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
    
    
    if (m.speed < 60) {
      action.gear = 1;
    } // end of if
    else if (m.speed < 110) {
      action.gear = 2;
    } // end of if 
    else if (m.speed < 150) {
      action.gear = 3;  
    } // end of if
    else if (m.speed < 190) {       
      action.gear = 4;
    } // end of if  
    else if (m.speed < 240) {       
      action.gear = 5;
    } // end of if
    else if (m.speed < 280) {     
      action.gear = 6;
    } else {
      action.gear = m.gear;
    } // end of if-else
    
    
    /*
    // zweite Möglichkeit zum Hochschalten
    if (action.gear < 6 && m.rpm > 8000 && action.accelerate > 0) {
    action.gear = m.gear + 1;
    } // end of if
    else if (action.gear > 1 && m.rpm < 3000 || action.brake > 0 && action.gear > 1) {
    action.gear = m.gear - 1;;
    } else {
    action.gear = m.gear;
    } // end of if-else
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
      action.steering -= (3.0 - distanceLeft) * 0.05;
    }
    if (distanceRight < 3.0) {
      action.steering += (3.0 - distanceRight) * 0.05;
    }
    
    
    // Beta Phase Rausfahren bei einem Crash
    if ((distanceAhead < 1 && distanceAheadRight < 1) && (m.trackPosition > 1 || m.trackPosition < -1)) {
      targetSpeed = 10;
      if (m.speed < 2) {
        action.gear = -1;
        action.steering = m.angleToTrackAxis * 0.75;
        //double distanceRight = m.trackEdgeSensors[18];
        action.steering -= (4.0 - distanceRight) * 0.05;
      } // end of if 
    } // end of if 
    
    if ((distanceAhead < 1 || distanceAheadLeft < 1) && (m.trackPosition > 1 || m.trackPosition > -1)) {
      targetSpeed = 10;
      if (m.speed < 2) {
        action.gear = -1;
        action.steering = m.angleToTrackAxis * 0.75;
        //double distanceLeft = m.trackEdgeSensors[0];
        action.steering += (4.0 - distanceLeft) * 0.05;  
      } // end of if
    } // end of if    
    
    
    // return the action
    return action;
  }

  public void shutdown() {
    System.out.println("Bye Bye!");
  }
}
