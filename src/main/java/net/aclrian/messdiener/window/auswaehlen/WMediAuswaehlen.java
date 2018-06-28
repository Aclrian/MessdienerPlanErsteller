package net.aclrian.messdiener.window.auswaehlen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.pictures.References;
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
		this.setIconImage(References.getIcon());
		setBounds(Utilities.setFrameMittig(450, 300));
		setTitle("W"+References.ae+"hle Leiter, die eingeteilt werden sollen");
		if (!nurleiter) {
			setTitle("W"+References.ae+"hle Messdiener, die eingeteilt werden sollen");
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
		
				JButton btnBesttigen = new JButton("Best"+References.ae+"tigen");
				btnBesttigen.setBounds(183, 143, 112, 25);
				
				getContentPane().add(btnBesttigen);
				AList<Messdiener> amp;
				if (nurleiter) {
					@SuppressWarnings("unchecked")
					ArrayList<Messdiener> al = (ArrayList<Messdiener>) hauptarray.clone();
					amp = new AList<Messdiener>(al, Messdiener.compForMedis);
				} else {
					 amp = new AList<Messdiener>(hauptarray, Messdiener.compForMedis);
				}
				
				btnBesttigen.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						ArrayList<Messdiener> rtn = amp.getSelected();
						zurueckgeben(rtn, wmh);
					}
				});
		getContentPane().add(btnAbbrechen);
		
		add(amp);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setVisible(true);
	}

	public void zurueckgeben(ArrayList<Messdiener> rtn, WMessenHinzufuegen wmh) {
		wmh.getAusgewaehlte(rtn);
		this.dispose();
	}
	
	
	
	

}