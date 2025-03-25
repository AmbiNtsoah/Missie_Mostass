package frames;

import java.awt.EventQueue;

/**
 * Classe principale qui va faire apparaître 
 * l'interface graphique
 */
public class Main {
	/**
	 * Fonction principale qui va éxécuter nos codes.
	 * @param args
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
