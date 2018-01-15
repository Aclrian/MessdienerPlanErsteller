package net.aclrian.messdiener.window.planerstellen;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.aclrian.frame.old.AlleMedisPane;
import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.utils.Util;
import net.aclrian.messdiener.window.WMainFrame;

import java.awt.ScrollPane;
import java.util.ArrayList;
import java.awt.List;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class WMediAuswaehlen extends JFrame {
	/**
	 * Create the frame.
	 */
	public WMediAuswaehlen(ArrayList<Messdiener> hauptarray, boolean nurleiter, WWMessenHinzufuegen wmh) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setTitle("W\u00E4hle Leiter, die eingeteilt werden sollen");
		if (!nurleiter) {
			setTitle("W\u00E4hle Messdiener, die eingeteilt werden sollen");
		}
		ArrayList<JCheckBox> checkboxes = new ArrayList<JCheckBox>();
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		getContentPane().add(panel);

		JButton btnAbbrechen = new JButton("Abbrechen");
		btnAbbrechen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				zurueckgeben(null, wmh);
			}
		});
		getContentPane().add(btnAbbrechen);

		JButton btnBesttigen = new JButton("Best\u00E4tigen");
		
		getContentPane().add(btnBesttigen);
		AlleMedisPane amp = new AlleMedisPane(hauptarray, nurleiter);
		Container c = amp.getMedisinList();
		panel.add(c);
		btnBesttigen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ArrayList<Messdiener> rtn = amp.getAusgewaehlte(hauptarray);
				zurueckgeben(rtn, wmh);
			}
		});

		this.setVisible(true);
	}

	public void zurueckgeben(ArrayList<Messdiener> rtn, WWMessenHinzufuegen wmh) {
		wmh.getAusgewaehlte(rtn);
		this.dispose();
	}
	
	
	
	

}
