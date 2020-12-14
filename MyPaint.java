package simulationCovid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;

@SuppressWarnings("deprecation")
public class MyPaint extends JPanel implements Observer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Simulation mySim;
	
	public MyPaint() {
	}

	public void paint(Graphics g) {
		drawCanvas(g);
	}
	
	public void drawCanvas(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;		
		Dimension size = getSize();
		
		g2d.setColor(new Color(102,178,255)); 
		g2d.fillRect(0, 0, size.width, size.height);
		
		int boxSize = 9;
		int maxCol = size.width / boxSize;
		int maxRows = size.height/ boxSize;
		//System.out.println(maxRows+" "+maxCol);
		int r=204, gr=204, b=255;
		
		Cell[][] map = null;
		if(mySim != null)
			map = mySim.getBoard().getMap();

		for (int i = 0; i < maxRows; i++) { 
			for (int j = 0; j < maxCol; j++) {			

				Color col = new Color(r, gr, b);
				g2d.setColor(col);
				//g2d.fillRect(j*10, i*10, boxSize, boxSize);
				g2d.fillOval(j*10, i*10, boxSize, boxSize);
				if(mySim != null && Board.insideMap(i, j)) {

					if(map[i][j].isSusceptible()) {
						g2d.setColor(Color.LIGHT_GRAY);
					}else if(map[i][j].isInfectedButNoSymptoms()) {
						g2d.setColor(Color.PINK);						
					}else if(map[i][j].isInfectedWithSymptoms()) {
						g2d.setColor(Color.RED);						
					}else if(map[i][j].isRecovered()) {
						g2d.setColor(Color.GREEN);
					}else if(map[i][j].isDied()) {
						g2d.setColor(Color.DARK_GRAY);
					}

					//g2d.fillRect(j*10, i*10, boxSize, boxSize);
					  g2d.fillOval(j*10, i*10, boxSize, boxSize);
					if(map[i][j].isInQuarantine()) {
						g2d.setColor(Color.YELLOW); /*In Quarantine*/ 
						g2d.drawOval(j*10, i*10, boxSize-1, boxSize-1);
					}
						
					if(map[i][j].isWearMask()) {
						g2d.setColor(Color.WHITE); /*Using Mask*/
						//g2d.drawRect(j*10, i*10, boxSize, boxSize);
						g2d.setFont(new Font("TimesRoman", Font.BOLD, 12)); 
						g2d.drawString("-",j*10+2,i*10+7);/* "-" Symbolizing  Wearing mask*/
					}
				}
				
			}
		}
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if(arg == null) {
		}else if(arg instanceof Simulation) {
			mySim = (Simulation) arg;
		}	
		repaint();		
	}
	
}
