package simulationCovid;
/**
 * 
 * @author Zeynep Tufekci
 *
 */

/**
 * 
 * Cell class each entry of the board has two dimensional Cell[][]. 
 *
 */
public class Cell extends Location {
	private int status = 0;  
	/* 0:Suspicious 
	 * 1:Infected (incubation period, no symptoms)
	 * 2:Infected (with symptoms)
	 * 3:Recovered 
	 * 4:Died 
	 * */
	private boolean wearMask = false;
	private boolean quarantine = false;
	
	protected boolean isInQuarantine() {
		return quarantine;
	}
	protected void setQuarantine(boolean quarantine) {
		this.quarantine = quarantine;
	}
	protected void setWearMask(boolean wearMask) {
		this.wearMask = wearMask;
	}
	protected boolean isWearMask() {
		return wearMask;
	}
	private int incubDay = 0;/* which Incubation day */
	protected int getIncubDay() {
		return incubDay;
	}
	protected int getSympDay() {
		return sympDay;
	}
	private int sympDay = 0; /* symptom day */

	protected void increaseSympDay() {
		this.sympDay = sympDay + 1;
	}
	protected void increaseIncubDay() {
		this.incubDay = incubDay + 1;
	}
	protected int getStatus() {
		return status;
	}
	private void setStatus(int status) {
		this.status = status;
	}
	public Cell(int x, int y) {
		super(x, y);
	}
	boolean isSusceptible() {
		return (status == 0);
	}
	boolean isInfectedButNoSymptoms(){
		return (status == 1);
	}
	boolean isInfectedWithSymptoms(){
		return (status == 2);
	}
	boolean isRecovered(){
		return (status == 3);
	}
	boolean isDied(){
		return (status == 4);
	}
	void setSusceptible(){
		setStatus(0);
	}
	void setInfectedButNoSymptoms(){
		setStatus(1);
	}
	void setInfectedWithSymptoms(){
		setStatus(2);
	}
	void setRecovered() {
		setStatus(3);
	}
	void setDied() {
		setStatus(4);
	}
}
