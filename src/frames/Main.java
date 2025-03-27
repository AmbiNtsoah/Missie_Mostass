package frames;

import java.awt.EventQueue;


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