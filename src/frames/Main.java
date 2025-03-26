package frames;

import java.awt.EventQueue;

/**
 * Point d'entrée pour permettre à notre
 * interface graphique de s'afficher
 */
public class Main {
	/**
	 * Methode consulté par le compilateur JAVA
	 * Pour exécuter nos codes.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Frames frame = new Frames();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
