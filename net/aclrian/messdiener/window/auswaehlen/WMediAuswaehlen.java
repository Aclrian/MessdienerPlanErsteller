package net.aclrian.messdiener.window.auswaehlen;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.utils.Utilities;
import net.aclrian.messdiener.window.planerstellen.WMessenHinzufuegen;

public class WMediAuswaehlen extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5361226882427936085L;

	/**
	 * Create the frame.
	 */
	public WMediAuswaehlen(ArrayList<Messdiener> hauptarray, boolean nurleiter, WMessenHinzufuegen wmh) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(Utilities.setFrameMittig(450, 300));
		setTitle("W\u00E4hle Leiter, die eingeteilt werden sollen");
		if (!nurleiter) {
			setTitle("W\u00E4hle Messdiener, die eingeteilt werden sollen");
		}
	
		@SuppressWarnings("unused")
		ArrayList<JCheckBox> checkboxes = new ArrayList<JCheckBox>();
		hauptarray.sort(Messdiener.compForMedis);
		getContentPane().setLayout(null);
		JPanel panel = new JPanel();
		panel.setBounds(12, 5, 283, 131);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		getContentPane().add(panel);

		JButton btnAbbrechen = new JButton("Abbrechen");
		btnAbbrechen.setBounds(12, 143, 110, 25);
		btnAbbrechen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				zurueckgeben(null, wmh);
			}
		});
		
				JButton btnBesttigen = new JButton("Best\u00E4tigen");
				btnBesttigen.setBounds(183, 143, 112, 25);
				
				getContentPane().add(btnBesttigen);
				AlleMedisPane amp = new AlleMedisPane(hauptarray, nurleiter);
				btnBesttigen.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						ArrayList<Messdiener> rtn = amp.getAusgewaehlte(hauptarray);
						zurueckgeben(rtn, wmh);
					}
				});
		getContentPane().add(btnAbbrechen);
		
		Container c = amp.getMedisinList();
		panel.add(c);

		this.setVisible(true);
	}

	public void zurueckgeben(ArrayList<Messdiener> rtn, WMessenHinzufuegen wmh) {
		wmh.getAusgewaehlte(rtn);
		this.dispose();
	}
	
	
	
	

}
