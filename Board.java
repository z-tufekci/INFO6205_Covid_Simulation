package simulationCovid;

import java.util.ArrayList;


public class Board {
/*I could not simulate on the U.S. map */
	/*population density*/
	private static Board board = null;
	private static Cell[][] map;	
	protected static int count = 1;
	private static int n = 57;
	private static int m = 128;
	private static int incubationTime = 4;  
	private static int recoveryTime = 10;  
	private static double asympthomaticRate = 0.17;	
	private static double transmissionRate = 0.35;/*between 0 to 1*/
	private static double asympthomaticTransmissionRate = 0.35*(0.58);/*between 0 to 1*/
	private static double transmissionRateWearMask = transmissionRate*(0.35);/*between 0 to 1 (approx : %12 while masking)*/
	private static double transmissionRateInQuarantine = 0.10;/*between 0 to 1*/
	private static double fatalityRate = 0.015;
	protected boolean wearMask;
	private double wearMaskRate = 0.50;	/*default value:0.50 between 0 to 1*/
	private double contactTraceRate = 0;	
	private double rVal = 2.5;/*each person infected with the virus will spread it to average "rVal" people.*/
	private double kVal = 0.16; /* superspreading value */

	ArrayList<Cell> newInfected;
	private Board() {
		System.out.println("Board");
		map = new Cell[n][m];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < m; j++)
				map[i][j] = new Cell(i, j);
	}
	public static Board instance() {
		if (board == null)
			board = new Board();
		return (board);
	}
	protected Cell[][] getMap(){
		return map;
	}
	protected void setN(int N) {
		Board.n = N;
	}
	protected void setM(int M) {
		Board.m = M;
	}
	protected void setWearMaskRate(double wearMaskRate) {
		this.wearMaskRate = wearMaskRate;
	}
	protected void setrVal(double rVal) {
		this.rVal = rVal;
	}
	protected void setContactTraceRate(double contactTraceRate) {
		this.contactTraceRate = contactTraceRate;
	}	
	protected void clearMap(){
		map = new Cell[n][m];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < m; j++)
				map[i][j] = new Cell(i, j);
		count = 0;
		clearQuarantine();
	}
    protected int getN() {
    	return n;
    }
    protected int getM() {
    	return m;
    }
	protected static boolean isSusceptible(int i, int j) {
		if (!insideMap(i, j))
			return false;
		return (map[i][j].isSusceptible());
	}
	protected static boolean insideMap(int i, int j) {
		if (i >= 0 && i < map.length && j >= 0 && j < map[0].length)
			return true;
		return false;
	}
	public boolean isFinished() {
		for(int t = 0; t < n;t++)
			for(int k = 0; k < m; k++)
				if(map[t][k].isSusceptible())
					return false;
		return true;
	}
	public void nextStep() {		
		spreadVirus();
		increaseDay();
		quarantina();
	}		
	void firstInfected(){
		int rX = n/2;	//(int)(Math.random()*n);
		int rY = m/2;	//(int)(Math.random()*m);
		placeInfected(rX, rY);
		board.increaseDay();
	}
	void placeInfected(int i, int j){
		if(map[i][j] != null) {
			map[i][j].setInfectedButNoSymptoms();
		}
	}

	void quarantina(){
		if(newInfected == null || newInfected.size() == 0) return;
		
		//System.out.println("Contact Trace: "+contactTraceRate);
		int quarantineSize= (int)(newInfected.size() * contactTraceRate);
		for(int i = 0;i< quarantineSize;i++) {
		
			int random = (int) (Math.random()*newInfected.size());
			while(newInfected.get(random).isInQuarantine())
				random = (int) (Math.random()*newInfected.size());
			Cell quarantined = newInfected.get(random);
			quarantined.setQuarantine(true);
		}
	}
	
	void spreadVirus(){
		newInfected= new ArrayList<Cell>(); 
		ArrayList<Cell> infectedL =  getInfected();
		int infSize = infectedL.size();
		double val = rVal*infSize; 
		int count = 0;
		if(kVal<1) {
			int superspreader = (int) Math.ceil(infSize*kVal);			
			int rate=0;
			if(superspreader  != 0) {
				rate = infSize / superspreader;
			}
			int superspreadCount = (int)(3.55*rVal);
			int remaining = (int)val - superspreadCount;
			int remainingCount = 0;
			if((infSize-2*superspreader)  != 0) {
				remainingCount = remaining/(infSize-2*superspreader);
			}
			for (Cell cell : infectedL) {
				/*superspreader*/
				if(count % rate == 0) {
					for(int i=0 ;i< superspreadCount ;i++) {

						/*go neighbor */
						Location l =  goAvailableNeighbor(cell);						
						double random = Math.random(); /*this generate number between 0 to 1 */
						if(l != null) {
							Cell neighbor = map[l.getX()][l.getY()];
							/*If spreader is in quarantine, there is still tiny probability to spread like %10 */
							if(cell.isInQuarantine() && random < transmissionRateInQuarantine ) {
								placeInfected(l.getX(), l.getY());
								newInfected.add(neighbor);
							}else 	if(cell.isWearMask() && neighbor.isWearMask()) {
								if(random < transmissionRateWearMask) {
									placeInfected(l.getX(), l.getY());
									newInfected.add(neighbor);
								}
							}
							else if(random < transmissionRate) {
								if(cell.isInfectedButNoSymptoms() && random < asympthomaticTransmissionRate) {  /*Asympthomatic transimission rate is less than symhtompac rate*/
									placeInfected(l.getX(), l.getY());	
									newInfected.add(neighbor);
								}else {
									placeInfected(l.getX(), l.getY());
									newInfected.add(neighbor);
								}							
							}
						}
						count++;
					}
				}
				/*some cases not spread aprox %10 */
				else if (count % rate == 1) {
					count++;
				}
				/*average case*/
				else {
					for(int i=0 ;i< remainingCount ;i++) {
						/*go neighbor */
						Location l =  goAvailableNeighbor(cell);

						double random = Math.random(); /*this generate number between 0 to 1 */
						if(l != null) {
							Cell neighbor = map[l.getX()][l.getY()];
							if(cell.isInQuarantine() && random < transmissionRateInQuarantine ) {
								placeInfected(l.getX(), l.getY());
								newInfected.add(neighbor);
							} else	if(cell.isWearMask() && neighbor.isWearMask()) {
								if(random < transmissionRateWearMask) {
									placeInfected(l.getX(), l.getY());
									newInfected.add(neighbor);
								}
							}
							else if(random < transmissionRate) {
								if(cell.isInfectedButNoSymptoms() && random < asympthomaticTransmissionRate) {  /*Asympthomatic transimission rate is less than symhtompac rate*/
									placeInfected(l.getX(), l.getY());	
									newInfected.add(neighbor);
								}else {
									placeInfected(l.getX(), l.getY());
									newInfected.add(neighbor);
								}							
							}
						}
						count++;
					}
				}
			}				
		}
		/*If kVal >1*/
		else {
			for (Cell cell : infectedL) {
					for(int i=0 ; i< (int)rVal ;i++) {
						/*go neighbor */
						Location l =  goAvailableNeighbor(cell);
						double random = Math.random(); /*this generate number between 0 to 1 */
						if(l != null) {
							Cell neighbor = map[l.getX()][l.getY()];
							if(cell.isInQuarantine() && random < transmissionRateInQuarantine ) {
									placeInfected(l.getX(), l.getY());
									newInfected.add(neighbor);
							} else if(cell.isWearMask() && neighbor.isWearMask()) {
								if(random < transmissionRateWearMask) {
									placeInfected(l.getX(), l.getY());
									newInfected.add(neighbor);
								}
							}
							else if(random < transmissionRate) {
								if(cell.isInfectedButNoSymptoms() && random < asympthomaticTransmissionRate) {  /*Asympthomatic transimission rate is less than symhtompac rate*/
									placeInfected(l.getX(), l.getY());
									newInfected.add(neighbor);
								}else {
									placeInfected(l.getX(), l.getY());
									newInfected.add(neighbor);
								}							
							}
						}
						
						count++;
						if(count > val)
							break;
					}
					if(count > val)
						break;
			}

		}		
	}
	protected Location goAvailableNeighbor(Cell c){
		//boolean find = false;
		Location l = null;
		int t = 1;
		//while(!find) {
			int i = (int) (Math.random()*3)  -1 ;//  0 -1 +1
			int j = (int) (Math.random()*3)  -1 ;//  0 -1 +1	
			i= i*t;
			j= j*t;
			//System.out.println(i+" "+j);
			if(isSusceptible(c.getX() + i, c.getY() + j))/*First try random neighbor*/
				l = new Location(c.getX() +i , c.getY() + j);	
			else if (isSusceptible(c.getX(), c.getY() + t))
				l = new Location(c.getX(), c.getY() + t);
			else if (isSusceptible(c.getX(), c.getY() - t))
				l = new Location(c.getX(), c.getY() - t);
			else if (isSusceptible(c.getX() + t, c.getY()))
				l = new Location(c.getX() + t, c.getY());
			else if (isSusceptible(c.getX() - t, c.getY()))
				l = new Location(c.getX() - t, c.getY());
			else if (isSusceptible(c.getX() - t, c.getY() - t))
				l = new Location(c.getX() - t, c.getY() - t);
			else if (isSusceptible(c.getX() - t, c.getY() + t))
				l = new Location(c.getX() - t, c.getY() + t);
			else if (isSusceptible(c.getX() + t, c.getY() - t))
				l = new Location(c.getX() + t, c.getY() - t);
			else if (isSusceptible(c.getX() + t, c.getY() + t))
				l = new Location(c.getX() + t, c.getY() + t);
			//t++;
			//System.out.println((l)+""+t);
			//if(t>n/2 && t>m/2)
			//	break;
			//if(l != null)
			//	find = true;
		//}
		//System.out.println(l);
		return l;
	}
	private ArrayList<Cell> getInfected() {
		ArrayList<Cell> infectedL =  new ArrayList<Cell>();
		for(int t = 0; t < n;t++) {
			for(int k = 0; k < m; k++) {
				if(map[t][k].isInfectedWithSymptoms() || map[t][k].isInfectedButNoSymptoms() ) {
						infectedL.add(map[t][k]);
				}
			}
		}
		return infectedL;
	}
	private void increaseDay() {
		for(int t = 0; t < n;t++) {
			for(int k = 0; k < m; k++) {
				if(map[t][k].isInfectedButNoSymptoms() ) {
						map[t][k].increaseIncubDay();
						double random = Math.random();
						if(map[t][k].getIncubDay() == incubationTime && random > asympthomaticRate) {
							map[t][k].setInfectedWithSymptoms();
						}
						if(map[t][k].getIncubDay() == recoveryTime + incubationTime) {
							map[t][k].setRecovered();
						}
				}
				if(map[t][k].isInfectedWithSymptoms() ) {						
						map[t][k].increaseSympDay();
						if(map[t][k].getSympDay() == recoveryTime) {
							double random = Math.random();
							if(random <= fatalityRate ) {
								map[t][k].setDied();
							}else {
								map[t][k].setRecovered();								
							}
						}						
				}
			}
		}
	}
	protected void wearMask() {
		//System.out.println("Yes"+wearMaskRate);
		if(wearMaskRate < 0 || wearMaskRate > 1) return;
			
		for(int t = 0; t < n;t++) {
			for(int k = 0; k < m; k++) {
				double random = Math.random();/*0 to 1*/
				if(random <= wearMaskRate) {
					map[t][k].setWearMask(true);
				}
				
			}
		}
		
	}
	protected void clearWearMask() {	/*Default mask setting */		
		for(int t = 0; t < n;t++) {
			for(int k = 0; k < m; k++) {
				double random = Math.random();/*0 to 1*/
				if(random <= wearMaskRate) {
					map[t][k].setWearMask(false);
				}
				
			}
		}
		
	}
	protected void clearQuarantine() {	/*Default mask setting */		
		for(int t = 0; t < n;t++) 
			for(int k = 0; k < m; k++) 
					map[t][k].setQuarantine(false);
	}
	
}