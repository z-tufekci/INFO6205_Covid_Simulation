/**
 * 
 */
package simulationCovid;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;

/**
 * App – An abstract UI Base Application class
 * @author Zeynep Tufekci
 */
public abstract class App implements ActionListener {

	protected JFrame frame;
	protected MenuManager menuMgr;
	
	/**
	 * App constructor
	 * This is called before the inheriting class constructor
	 */
	public App() {
		initGUI();
	}
	

	/**
	 * Initialize the Graphical User Interface
	 */
    public void initGUI() {
    	frame = new JFrame();

		frame.setResizable(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //JFrame.DISPOSE_ON_CLOSE)
		
		// Permit the app to hear about the window opening
		//frame.addWindowListener(this); 
		
		menuMgr = new MenuManager(this);
		
		menuMgr.createDefaultActions(); // Set up default menu items
		
		frame.setJMenuBar(menuMgr.getMenuBar()); // Add a menu bar to this application
		
		frame.setLayout(new BorderLayout());
		frame.add(getNorthPanel(), BorderLayout.NORTH);
		frame.add(getMainPanel(), BorderLayout.CENTER);
		
    }
    
    /**
     * Override this method to provide a top level control panel if needed
     * @return a JPanel, which contains the main content of of your application
     */
    public abstract JPanel getNorthPanel();
    
    /**
     * Override this method to provide the main content panel.
     * @return a JPanel, which contains the main content of of your application
     */
    public abstract JPanel getMainPanel() ;


    /**
     * A convenience method that uses the Swing dispatch threat to show the UI.
     * This prevents concurrency problems during component initialization.
     */
    public void showUI() {
    	
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	frame.setVisible(true); 
            }
        });
    }
    
    /**
     * Shut down the application
     */
    public void exit() {
    	frame.dispose();
    	System.exit(0);
    }

    /**
     * Override this method to show a About Dialog
     */
    public void showHelp() {
    }
 
}


