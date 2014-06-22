package cl.uai.webcursos.emarking.desktop;

import java.awt.EventQueue;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class eMarking {

	private static Logger logger = Logger.getLogger(eMarking.class);
	private static boolean IS_MAC = false;
	
	public static void main(String[] args) {
		// Obtain current locale for language settings
		Locale locale = Locale.getDefault();
		// Set language settings
		EmarkingDesktop.lang = ResourceBundle.getBundle("cl.uai.webcursos.emarking.desktop.lang", locale);
		// Obtain properties for log4j
		PropertyConfigurator.configure("log4j.properties");
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Getting operating system info
		String lcOSName = System.getProperty("os.name").toLowerCase();
		IS_MAC = lcOSName.startsWith("mac os x");

		if(IS_MAC) {
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "eMarking");
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}

		// Starting
		logger.info("Initializing eMarking desktop");


		// Invoke window asynchronously
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// Our main window
					EmarkingDesktop window = new EmarkingDesktop();
					window.getFrame().setLocationRelativeTo(null);
					window.getFrame().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
