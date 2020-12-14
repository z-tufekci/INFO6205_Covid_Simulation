package simulationCovid;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
/**
 * 
 * @author Zeynep Tufekci
 * Main GUI part of Project. It extends from DDApp abstract class.
 * Control part and status part is here.
 */
@SuppressWarnings("deprecation")
public class VirusAppUI extends App implements Observer, ActionListener {
	DecimalFormat df = new DecimalFormat("#.##");
	private Simulation simulation;

	private JPanel buttonPanel;
	private JPanel infoPanel;
	private MyPaint paint;
	private JButton startBtn;
	private JButton stopBtn;
	private JButton pauseBtn;
	
	private final int r_MIN = 0;
	private final int r_MAX = 10;
	private final int r_INIT = 5;

	private JComboBox<Double> rateWearMask;
	private JLabel curStatus;
	private JLabel statusLabel;
	
	private JSlider rVal;
	private JLabel rLabel;
	private JCheckBox checkBox;
	private JLabel rateMaskLabel;
	private JLabel explLabel;
	private JSlider socDist;
	private JLabel socDistLabel;
	private JLabel contactTraceLabel;
	private JSlider contactTrace;
	
	public VirusAppUI() {
		super();
		frame.getContentPane().add(getInfoPanel(),BorderLayout.SOUTH);/*Info panel*/
		customizeGUI();
		showUI();
	}

	private void customizeGUI() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setSize(new Dimension((int)screenSize.getWidth(),(int)(screenSize.getHeight())));				
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setTitle("Virus Simulation App");
		frame.setLocationRelativeTo(null);	
	}

	public void showUI() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame.setVisible(true);
			}
		});
	}

	void updateButtons() {
		if (simulation.isDone()) {
			if (startBtn != null)
				startBtn.setEnabled(true);
			if (pauseBtn != null)
				pauseBtn.setEnabled(false);
			if (stopBtn != null)
				stopBtn.setEnabled(false);
			if (rateWearMask != null  && checkBox != null && !checkBox.isSelected()) {
				makeWearMaskRateVisible(false);
			}
			
			/*if(simulation.getBoard().wearMask) {
				simulation.getBoard().clearWearMask();
			}*/

		} else {
			if (startBtn != null)
				startBtn.setEnabled(false);
			if (pauseBtn != null)
				pauseBtn.setEnabled(true);
			if (stopBtn != null)
				stopBtn.setEnabled(true);
			if(simulation.getBoard().wearMask) {
				simulation.getBoard().wearMask();
			}
			curStatus.setText("RUNNING\t\t");	
		}
	}
	void makeWearMaskRateVisible(boolean b){
		rateMaskLabel.setVisible(b);
		rateWearMask.setVisible(b);
	}
	private JPanel makeButtonPanel() {
		buttonPanel = new JPanel(); 
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); 
		buttonPanel.setBackground(new Color(102,178,255));

		startBtn = new JButton("Start");
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {	
				simulation.startSim();
				if (startBtn.isEnabled()) {
					updateButtons();
					System.out.println("Starting...");
				}
			}
		});

		pauseBtn = new JButton("Pause");
		pauseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				simulation.pauseSim();
				if(curStatus.getText().equals("PAUSED\t\t"))
					curStatus.setText("RUNNING\t\t");					
				else
					curStatus.setText("PAUSED\t\t");
			}
		});

		stopBtn = new JButton("Stop");
		stopBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				simulation.stopSim();
				if (stopBtn.isEnabled()) {
					updateButtons();
					System.out.println("Stopping...");
					curStatus.setText("STOPPED\t\t");		
				}
			}
		});
			
		contactTraceLabel = new JLabel();
		contactTrace = new JSlider(JSlider.HORIZONTAL,0,4,0);
		Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
		labelTable.put( new Integer( 0 ), new JLabel("%0") );
		labelTable.put( new Integer( 1 ), new JLabel("%25") );
		labelTable.put( new Integer( 2 ), new JLabel("%50") );
		labelTable.put( new Integer( 3 ), new JLabel("%75") );
		labelTable.put( new Integer( 4 ), new JLabel("%100") );
		contactTrace.setLabelTable( labelTable );
		contactTrace.setMinorTickSpacing(1);
		contactTrace.setPaintTicks(true);
		contactTrace.setPaintLabels(true);
		contactTrace.setValue(0);
		String text = "Contact Trace: "+String.valueOf(contactTrace.getValue()*(0.25));
		contactTraceLabel.setText(text);
		
		contactTrace.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
            	String text = String.valueOf( df.format(contactTrace.getValue()*(0.25)));
            	if(text.length() <3)
            		text += "   ";
                contactTraceLabel.setText( "Contact Trace:"+ text);
                if(simulation != null && simulation.getBoard() != null) {
                	simulation.getBoard().setContactTraceRate(contactTrace.getValue()*(0.25));
                }
            }
        });
		
		rLabel = new JLabel();
		rVal = new JSlider(JSlider.HORIZONTAL,
                r_MIN, r_MAX, r_INIT);
		rVal.setMinorTickSpacing(1);
		rVal.setPaintTicks(true);
		Hashtable<Integer, JLabel> labelss = new Hashtable<>();
        labelss.put(0, new JLabel("0"));
        labelss.put(1, new JLabel("0.5"));
        labelss.put(2, new JLabel("1"));
        labelss.put(3, new JLabel("1.5"));
        labelss.put(4, new JLabel("2"));
        labelss.put(5, new JLabel("2.5"));
        labelss.put(6, new JLabel("3"));
        labelss.put(7, new JLabel("3.5"));
        labelss.put(8, new JLabel("4"));
        labelss.put(9, new JLabel("4.5"));
        labelss.put(10, new JLabel("5"));
        rVal.setLabelTable(labelss);
		rVal.setPaintLabels(true);
		
		text = "R VALUE: "+String.valueOf(rVal.getValue()*(0.5));
		rLabel.setText(text);
		
		rVal.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
            	String text = String.valueOf(rVal.getValue()*(0.5));
            	if(text.length()<2)
            		text+="  ";
                rLabel.setText( "R VALUE:"+ text );
                
                if(simulation != null && simulation.getBoard() != null) {
                	simulation.getBoard().setrVal(rVal.getValue()*(0.5));
                }
            }
        });
		
		checkBox = new JCheckBox("<html>" + "WEAR\n MASK".replaceAll("\\n", "<br>") + "</html>",false);  
		checkBox.addItemListener(new ItemListener() {		
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					simulation.getBoard().wearMask = true;
					makeWearMaskRateVisible(true);
				}
				else if(e.getStateChange() == ItemEvent.DESELECTED) {
					simulation.getBoard().wearMask = false;
					makeWearMaskRateVisible(false);
					simulation.getBoard().clearWearMask();
				}
					
			}
		}); 
		Double[] rateL = new Double[100];//1 100
		for (int i= 0;i<rateL.length;i++) {
			rateL[i] = (double) (i+1) ; //1 .. 100
		}
		rateMaskLabel = new JLabel("<html>" + "WEAR\nMASK RATE".replaceAll("\\n", "<br>") + "</html>");
		rateWearMask = new JComboBox<Double>(rateL); 
		rateWearMask.setSelectedItem(50.0);
		rateWearMask.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if (simulation != null && simulation.getBoard() != null) {
					@SuppressWarnings("unchecked")
					JComboBox<Double> cb = (JComboBox<Double>) arg0.getSource();
					double rate = (double) cb.getSelectedItem();
					simulation.getBoard().setWearMaskRate((rate/100.0));/*percentage*/
					String text = "<html>" + "WEAR\nMASK RATE".replaceAll("\\n", "<br>") + "</html> "+df.format(rate);
					rateMaskLabel.setText(text);
					//simulation.getBoard().clearWearMask();
				}
			}
		});
		
		socDistLabel = new JLabel();
		socDist = new JSlider(JSlider.HORIZONTAL,
                r_MIN, r_MAX, r_INIT);

		Hashtable<Integer, JLabel> labels = new Hashtable<>();
        labels.put(0, new JLabel("0"));
        labels.put(1, new JLabel("0.5"));
        labels.put(2, new JLabel("1"));
        labels.put(3, new JLabel("1.5"));
        labels.put(4, new JLabel("2"));
        labels.put(5, new JLabel("2.5"));
        labels.put(6, new JLabel("3"));
        labels.put(7, new JLabel("3.5"));
        labels.put(8, new JLabel("4"));
        labels.put(9, new JLabel("4.5"));
        labels.put(10, new JLabel("5"));
        socDist.setLabelTable(labels);
		socDist.setMinorTickSpacing(1);
		socDist.setPaintTicks(true);
        socDist.setPaintLabels(true);
		
		text = "<html>" + "Social\n Distance".replaceAll("\\n", "<br>") + "</html>"+String.valueOf(socDist.getValue()*(0.5));
		socDistLabel.setText(text);
		
		socDist.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
            	String text = String.valueOf(socDist.getValue()*(0.5));
            	if(text.length()<2)
            		text+="  ";
            	socDistLabel.setText( "<html>" + "Social\n Distance".replaceAll("\\n", "<br>") + "</html>"+ text );
                
                if(simulation != null && simulation.getBoard() != null) {
                	double value = socDist.getValue()*(0.5);
                	if(value < 1.5) {
                		simulation.getBoard().setrVal(3.0);
                	}else if(value< 2.5) {
                		simulation.getBoard().setrVal(2.0);
                	}else if(value< 3.5) {
                		simulation.getBoard().setrVal(1.5);
                	}else if(value< 4.5) {
                    	simulation.getBoard().setrVal(1.0);
                	}else { //if(socDist.getValue()>= 4.5) 
                		simulation.getBoard().setrVal(0.5);
                	}
                	
                	
                
                }
            }
        });
		buttonPanel.add(startBtn);
		buttonPanel.add(pauseBtn);
		buttonPanel.add(stopBtn);
		buttonPanel.add(contactTraceLabel);
		buttonPanel.add(contactTrace);
		buttonPanel.add(rLabel);
		buttonPanel.add(rVal);
		buttonPanel.add(checkBox);
		buttonPanel.add(rateMaskLabel);
		buttonPanel.add(rateWearMask);
		buttonPanel.add(socDistLabel);
		buttonPanel.add(socDist);

		return buttonPanel;
	}

	public void exit() {
		frame.dispose();
		System.exit(0);
	}
	
	/** 
	 * To maximize screen
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("We received an ActionEvent " + e);
		if (e.getSource() instanceof JMenuItem) {
			JMenuItem mi = (JMenuItem) e.getSource();
			if (mi.getText().equals("Maximize window")) {
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				frame.setSize(screenSize);
				frame.setLocationRelativeTo(null);
			}
		}
	}

	@Override
	public JPanel getNorthPanel() {
		return makeButtonPanel();
	}

	@Override
	public JPanel getMainPanel() {
		
		paint = new MyPaint();
		simulation = new Simulation();
		updateButtons();
		simulation.addObserver(paint);
		simulation.addObserver(this);
		return paint;
	}

	public JPanel getInfoPanel() {
		infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));	
		infoPanel.setBackground(new Color(102,178,255));
		
		statusLabel = new JLabel("PROGRAM STATUS: ");
		curStatus = new JLabel("NOT RUNNING\t\t");
		statusLabel.setLabelFor(curStatus);

		infoPanel.add(statusLabel);
		infoPanel.add(curStatus);
		
		explLabel = new JLabel();
		String text = "|| PINK IS INFECTIOUS BUT DOES NOT SHOW ANY SYMPTHOMS || RED IS INFECTIOUS WITH SYMPTHOMS ||\n|| GREEN IS RECOVERED || BLACK IS DIED || GRAY IS SUSPICIOUS || YELLOW CIRCLE IS IN QUARANTINE";
		text = "<html>" + text.replaceAll("\\n", "<br>") + "</html> ";
		explLabel.setText(text);
		infoPanel.add(explLabel);
		return infoPanel;		
	}
	
	public static void main(String[] args) throws UnknownHostException {
		new VirusAppUI();
	}

	@Override
	public void update(Observable o, Object arg) {
		if (simulation != null && simulation.isDone()) {
			if(simulation.getBoard() != null) {
				if(simulation.getBoard().wearMask)
					simulation.getBoard().wearMask();
			}

		}
		
		if(simulation != null && simulation.getBoard() != null ) {			
		}	
	}
}
