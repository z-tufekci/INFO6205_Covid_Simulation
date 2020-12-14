package simulationCovid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;

/**
 * 
 * @author Zeynep Tufekci
 *
 */

/**
 * 
 * Simulation class main running part of code. 
 *
 */
@SuppressWarnings("deprecation")
public class Simulation extends Observable implements Runnable {
	private Board board;
	private Thread thread = null; 
	private boolean done = true; 
	private boolean paused = false;
	//protected boolean distrubuted;
	public Simulation(){	
		run();
	}
	
	/**
	 * Start the simulation
	 */
	public void startSim() {
	  System.out.println("Starting the simulation");
	  if (thread != null ) return;
	  thread = new Thread(this);
	  done = false;
	  paused = false;
	  thread.start();
	}
	
	/**
	 * Toggle the Pause of the simulation
	 */
	public void pauseSim() {
		paused = !paused;
	}

	/**
	 * Is the simulation currently in a paused state?
	 * @return true if paused
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * Stop the simulation
	 */
	public void stopSim() {
		System.out.println("Stopping the simulation");
		if (thread == null ) return;
		done = true;
		paused = false;
	}

	/**
	 * Make the current thread sleep a little
	 * @param millis
	 */
	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 *  Begin this thread of execution
	 */
	@Override
	public void run() {
		board = Board.instance();
		if(!done)
			runSimLoop();
		thread = null;
	}

	/**
	 * first clear map, initiate the variables in terms of different chosen rules then update each step.
	 */
	private void runSimLoop() {
		board.clearMap();	
		initiate();
		while (!done) {
			if (!paused) {
			   updateSim();
			}
			sleep(500L);
		}
		board.clearMap();	
	}
	
	
	private void initiate() {
		board.firstInfected();	
		setChanged();
		notifyObservers(this);
		sleep(500L);	
	}
	
	/**
	 * Perform an update to our simulation
	 */
	private void updateSim() {		
		if(!board.isFinished()) {
				board.nextStep();
		}
		else {
			done = true;
		}
		/*inform observer that situation is changed.*/
		setChanged();
		notifyObservers(this);
	}
	protected Board getBoard() {
		return board;
	}

	public boolean isDone() {
		return done;
	}

	public boolean isRunning() {
		return (thread != null);
	}
}
