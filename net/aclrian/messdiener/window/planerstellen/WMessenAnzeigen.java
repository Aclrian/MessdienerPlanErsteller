package net.aclrian.messdiener.window.planerstellen;

import java.awt.Checkbox;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.Messe;
import net.aclrian.messdiener.utils.EnumMesseTyp;
import net.aclrian.messdiener.utils.EnumOrt;
import javax.swing.JTextField;

public class WMessenAnzeigen extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8043887359375039385L;
	private JPanel contentPane;
	private JPanel panel = new JPanel();
	private Checkbox chbxHochamt = new Checkbox("Hochamt");
	private JLabel lblUhrzeit = new JLabel("Uhrzeit");
	private JTextField spinnerDatum = new JTextField();
	// private JSpinner spinnerDatum = new JSpinner();
	private JLabel lblDatum = new JLabel("Datum");
	private JTextField spinnerAnzahlMedis = new JTextField();
	private JLabel lblAnzahlDerMessdiener = new JLabel("Anzahl der Medis");
	private JLabel lblAnzahlDerLeiter = new JLabel("Eingeteilte");
	private JTextField spinnerKirche = new JTextField();
	private JTextField spinnerTyp = new JTextField();
	private JTextField txtFdatum;

	/**
	 * Create the frame.
	 */
	public WMessenAnzeigen(Messe m) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 312, 298);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);


		panel.setBounds(12, 0, 283, 256);
		contentPane.add(panel);
		panel.setLayout(null);
		chbxHochamt.setEnabled(false);

		chbxHochamt.setBounds(10, 70, 115, 23);
		panel.add(chbxHochamt);

		lblUhrzeit.setBounds(12, 41, 62, 23);
		panel.add(lblUhrzeit);
		spinnerDatum.setEnabled(false);

		SimpleDateFormat dfstunde = new SimpleDateFormat("HH:mm");
		spinnerDatum.setBounds(80, 43, 77, 20);
		panel.add(spinnerDatum);

		lblDatum.setBounds(10, 14, 70, 15);
		panel.add(lblDatum);
		spinnerAnzahlMedis.setEnabled(false);

		spinnerAnzahlMedis.setBounds(146, 97, 45, 20);
		panel.add(spinnerAnzahlMedis);

		lblAnzahlDerMessdiener.setBounds(10, 99, 134, 15);
		panel.add(lblAnzahlDerMessdiener);

		lblAnzahlDerLeiter.setBounds(10, 122, 91, 15);
		panel.add(lblAnzahlDerLeiter);
		spinnerKirche.setEnabled(false);

		//spinnerKirche.setModel(new SpinnerListModel(EnumOrt.values()));
		spinnerKirche.setBounds(146, 148, 119, 20);
		panel.add(spinnerKirche);
		spinnerTyp.setEnabled(false);

		//spinnerTyp.setModel(new SpinnerListModel(EnumMesseTyp.values()));
		spinnerTyp.setBounds(146, 180, 119, 20);
		panel.add(spinnerTyp);
		
		JLabel lblOrt = new JLabel("Ort");
		lblOrt.setBounds(10, 150, 70, 15);
		panel.add(lblOrt);
		
		JLabel lblTypDerMesse = new JLabel("Typ der Messe");
		lblTypDerMesse.setBounds(10, 182, 115, 15);
		panel.add(lblTypDerMesse);
		
		txtFdatum = new JTextField();
		txtFdatum.setEditable(false);
		txtFdatum.setBounds(80, 12, 114, 19);
		panel.add(txtFdatum);
		txtFdatum.setColumns(10);
		SimpleDateFormat ddf = new SimpleDateFormat("dd.MM.yyyy");
		txtFdatum.setText(ddf.format(m.getDate()));
		spinnerDatum.setText(dfstunde.format(m.getDate()));
		chbxHochamt.setState(m.isHochamt());
		System.out.println("M:" + m.getAnz_messdiener());
		spinnerAnzahlMedis.setText(String.valueOf(m.getAnz_messdiener()));
		spinnerKirche.setText(String.valueOf(m.getKirche()));
		spinnerTyp.setText(String.valueOf(m.getMesseTyp()));
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				schliessen();
			}
		});
		btnOk.setBounds(100, 212, 117, 25);
		panel.add(btnOk);
		
		JButton btnAnzeigen = new JButton("Anzeigen");
		btnAnzeigen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				anzeigen(m);
			}
		});
		btnAnzeigen.setBounds(148, 122, 117, 20);
		panel.add(btnAnzeigen);
		String s = "";
		for (Messdiener element : m.getEingeteilte()) {
			s+=element.makeId()+",";
		}
		if (s.endsWith(",")) {
			s = s.substring(0, s.length() - 1);
		}
		//
		panel.setVisible(true);
		
		contentPane.setVisible(true);
	}

	public void anzeigen(Messe m) {
		JFrame f = new JFrame();
		String message  =  "<html><body>";
		int i = 0;
		for (Messdiener element : m.getEingeteilte()) {
			message+=element.makeId();
			if (i == 3) {
				message+="<br>";
				i=0;
			}
			else {
				message+=", ";
			}
			i++;
		}
		if (message.endsWith(", ")) {
			message = message.substring(0, message.length() - 2);
		}
		
		JOptionPane.showMessageDialog(f, message, "Alle schon eingeteilten Messdiener:", JOptionPane.INFORMATION_MESSAGE);
		
	}

	public void schliessen() {
	//	this.dispose();
		
	}
}
