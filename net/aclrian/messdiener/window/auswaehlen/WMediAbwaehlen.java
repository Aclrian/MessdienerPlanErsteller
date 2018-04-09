package net.aclrian.messdiener.window.auswaehlen;

import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import com.toedter.calendar.JDateChooser;

import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.utils.Utilities;
import net.aclrian.messdiener.window.planerstellen.WMessenHinzufuegen;

public class WMediAbwaehlen extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4538344294622696740L;

	private SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

//	private ArrayList<Messdiener> hauptarray;
	private JPanel panel = new JPanel();
	private JDateChooser jd1 = new JDateChooser();
	private JDateChooser jd2 = new JDateChooser();

	private Messdiener m;

	public WMediAbwaehlen(Messdiener m, boolean neusetzen, WMessenHinzufuegen wmh) {
		wmh.setEnabled(false);
		this.m = m;
		if (neusetzen) {
			m.getMessdatenDaten().loescheAbwesendeDaten();
		}
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(Utilities.setFrameMittig(450, 300));
		setTitle(m.makeId() + " abmelden");

		getContentPane().setLayout(null);

		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, SystemColor.windowBorder, SystemColor.windowBorder,
				SystemColor.windowBorder, SystemColor.windowBorder));
		panel.setBounds(12, 12, 262, 128);
		getContentPane().add(panel);
		panel.setLayout(null);

		JButton bntabmelden = new JButton("Abmelden");
		bntabmelden.setBounds(130, 86, 122, 25);
		panel.add(bntabmelden);

		JButton btnAbbrechen = new JButton("Abbrechen");
		btnAbbrechen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				wmh.setEnabled(true);
			}
		});
		btnAbbrechen.setBounds(10, 86, 110, 25);
		panel.add(btnAbbrechen);

		JLabel lblBis = new JLabel("Bis:");
		lblBis.setBounds(10, 49, 70, 25);
		panel.add(lblBis);

		jd1.setBounds(61, 49, 188, 25);
		panel.add(jd1);
		jd2.setBounds(61, 12, 188, 25);
		panel.add(jd2);

		JLabel lblVon = new JLabel("Von:");
		lblVon.setBounds(10, 12, 70, 25);
		panel.add(lblVon);
		bntabmelden.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				abmelden(wmh);
			}
		});
		setVisible(true);
	}

	public void abmelden(WMessenHinzufuegen wmh) {
		Date anfang = jd1.getDate();
		Date ende = jd2.getDate();
		Calendar calanfang = Calendar.getInstance();
		calanfang.setTime(anfang);
		Calendar calende = Calendar.getInstance();
		calende.setTime(ende);
		/*
		 * Calendar start = Calendar.getInstance(); start.setTime(anfang); for (Date
		 * date = start.getTime(); date.before(ende); start.add(Calendar.DATE, 1), date
		 * = start.getTime()) { m.getMessdatenDaten().austeilen(date);
		 * System.out.println("Der Messdiener " + m.makeId() + " wurde für den " +
		 * df.format(date) + " ausgeteilt."); }
		 */
		LocalDate lstart = anfang.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate lstopp = ende.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int days = (int) ChronoUnit.DAYS.between(lstopp, lstart);
		System.out.println(days + "sdsadg!");
		Calendar ical = Calendar.getInstance();
		ical.setTime(ende);
		for (int i = 0; i <= days; i++) {
			m.getMessdatenDaten().austeilen(ical.getTime());
			Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),
					"Der Messdiener " + m.makeId() + " wurde fuer den " + df.format(ical.getTime()) + " ausgeteilt.");
			ical.add(Calendar.DATE, 1);
		}
		panel.setVisible(false);
		wmh.rueckgeben(m);
		wmh.setEnabled(true);
		dispose();
	}

	public int daysBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
	}
}