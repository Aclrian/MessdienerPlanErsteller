package net.aclrian.messdiener.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.aclrian.messdiener.deafault.Messe;
import net.aclrian.messdiener.pictures.References;
import net.aclrian.messdiener.start.AProgress;
import net.aclrian.messdiener.utils.Utilities;
import net.aclrian.messdiener.window.APanel;
import net.aclrian.messdiener.window.auswaehlen.ACheckBox;

public class MesseAnzeigen extends APanel {

	private static final long serialVersionUID = -2230487108865122325L;

	private ACheckBox chbxHochamt = new ACheckBox("Hochamt");
	// private JLabel lblUhrzeit = new JLabel("Uhrzeit");
	private JSpinner spinnerDatum = new JSpinner();
	private JLabel lblDatum = new JLabel("Datum");
	private JSpinner spinnerAnzahlMedis = new JSpinner();
	private JLabel lblAnzahlDerMessdiener = new JLabel("Anzahl der Medis");
	private JLabel lblOrt = new JLabel("Ort");
	private JLabel lblTyp = new JLabel("Typ");
	private JSpinner spinnerKirche = new JSpinner();
	private JSpinner spinnerTyp = new JSpinner();
	private JButton btnSetzeManuell = new JButton("manuell einteilen");
	private JButton btntitle = new JButton("Titel " + References.ae + "ndern");
	// private ArrayList<Messdiener> ausgewaehlte;
	// private EnumBoolean nurleiter;
	private JTextField tftitel = new JTextField();
	private String title = null;

	private ACheckBox chbxnurleiter = new ACheckBox("nur Leiter");
	// private Messe bearbeiten;
	// private int ibearbeiten;

	/**
	 * Create the panel.
	 * 
	 * @param defaultButtonheight
	 * @param defaultButtonwidth
	 * @param ap
	 */
	public MesseAnzeigen(int defaultButtonwidth, int defaultButtonheight, AProgress ap) {
		super(defaultButtonwidth, defaultButtonheight, true);
		spinnerDatum.setModel(new SpinnerDateModel(new Date(), null, null, Calendar.HOUR_OF_DAY));
		JSpinner.DateEditor de_spinnerDatum = new JSpinner.DateEditor(spinnerDatum, "dd.MM.yyy HH:mm");
		spinnerDatum.setEditor(de_spinnerDatum);
		add(chbxHochamt);
		add(tftitel);
		add(lblDatum);
		add(lblAnzahlDerMessdiener);
		add(spinnerDatum);
		add(spinnerAnzahlMedis);
		add(spinnerKirche);
		add(spinnerTyp);
		add(lblOrt);
		add(lblTyp);
		add(btnSetzeManuell);
		tftitel.setVisible(false);
		btntitle.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (btntitle.getText().equals("Titel " + References.ae + "ndern")) {
					tftitel.setVisible(true);
					if (tftitel.getText().equals("")) {
						tftitel.setText(spinnerTyp.getModel().getValue().toString());
					}
					btntitle.setText("Titel speichern");
				} else {
					title = tftitel.getText();
					tftitel.setVisible(false);
					btntitle.setText("Titel " + References.ae + "ndern");
				}
				
			}
		});
		add(chbxnurleiter);
		add(btntitle);
		chbxHochamt.setSelected(false);
		chbxHochamt.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (chbxHochamt.isSelected()) {
					chbxHochamt.setIcon(new ImageIcon(References.class.getResource("title.png")));
				} else {
					chbxHochamt.setIcon(new ImageIcon(References.getIcon()));
				}

			}
		});

		SpinnerNumberModel slmAnzahl = new SpinnerNumberModel(6, 0, 50, 1);
		spinnerAnzahlMedis.setModel(slmAnzahl);
		String[] orte = new String[ap.getAda().getPfarrei().getOrte().size()];
		for (int i = 0; i < orte.length; i++) {
			orte[i] = ap.getAda().getPfarrei().getOrte().get(i);
		}
		SpinnerListModel snmKirche = new SpinnerListModel(orte);
		spinnerKirche.setModel(snmKirche);

		String[] type = new String[ap.getAda().getPfarrei().getTypen().size()];
		for (int i = 0; i < type.length; i++) {
			type[i] = ap.getAda().getPfarrei().getTypen().get(i);
		}
		SpinnerListModel snmTypen = new SpinnerListModel(type);
		spinnerTyp.setModel(snmTypen);
		getBtnSpeichern().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				speichern(ap);
			}
		});
	}

	protected void speichern(AProgress ap) {
		Messe m;
		if (title == null) {
			m = new Messe(chbxHochamt.isSelected(), ((int) spinnerAnzahlMedis.getModel().getValue()),
					((Date) spinnerDatum.getModel().getValue()), ((String) spinnerKirche.getModel().getValue()),
					((String) spinnerTyp.getModel().getValue()), ap.getAda());
		} else {
			m = new Messe(chbxHochamt.isSelected(), ((int) spinnerAnzahlMedis.getModel().getValue()),
					((Date) spinnerDatum.getModel().getValue()), ((String) spinnerKirche.getModel().getValue()),
					((String) spinnerTyp.getModel().getValue()), title, ap.getAda());
		}
		Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), m.getID() + " wurde erstellt.");
		ap.getWAlleMessen().addMesse(m);
	}
	public void offneMesse(Messe m){
		setztinPanel(m);
	}

	private void setztinPanel(Messe m) {
		title = m.getTitle();
		chbxHochamt.setSelected(m.isHochamt());
		
	}

	@Override
	protected void graphics(int width, int heigth) {
		int drei = width / 3;
		int stdhoehe = heigth / 20;
		int abstandhoch = heigth / 100;
		int abstandweit = width / 100;
		int eingenschaften = width / 5;
		int haelfte = width / 2;
		getBtnAbbrechen().setBounds(abstandweit, heigth - abstandhoch - this.getDfbtnheight(), this.getDfbtnwidth(),
				this.getDfbtnheight());
		getBtnSpeichern().setBounds(width - abstandweit - this.getDfbtnwidth(),
				heigth - abstandhoch - this.getDfbtnheight(), this.getDfbtnwidth(), this.getDfbtnheight());
		lblDatum.setBounds(abstandweit, abstandhoch + stdhoehe, eingenschaften, stdhoehe);
		spinnerDatum.setBounds(abstandweit + eingenschaften, abstandhoch + stdhoehe,
				haelfte - abstandweit - eingenschaften, stdhoehe);
		chbxHochamt.setBounds(haelfte + abstandweit, abstandhoch + 2 * stdhoehe, drei - abstandweit, stdhoehe);
		lblAnzahlDerMessdiener.setBounds(abstandweit, abstandhoch + 3 * stdhoehe, eingenschaften, stdhoehe);
		spinnerAnzahlMedis.setBounds(abstandweit + eingenschaften, abstandhoch + 3 * stdhoehe,
				haelfte - abstandweit - eingenschaften, stdhoehe);
		spinnerKirche.setBounds(abstandweit + eingenschaften, abstandhoch + 5 * stdhoehe,
				haelfte - abstandweit - eingenschaften, stdhoehe);
		spinnerTyp.setBounds(abstandweit + eingenschaften, abstandhoch + 7 * stdhoehe,
				haelfte - abstandweit - eingenschaften, stdhoehe);
		lblOrt.setBounds(abstandweit, abstandhoch + 5 * stdhoehe, haelfte - abstandweit - eingenschaften, stdhoehe);
		lblTyp.setBounds(abstandweit, abstandhoch + 7 * stdhoehe, haelfte - abstandweit - eingenschaften, stdhoehe);
		btnSetzeManuell.setBounds(haelfte + abstandweit, abstandhoch + 4 * stdhoehe, (int) ((drei - abstandweit) * 0.5),
				stdhoehe);
		chbxnurleiter.setBounds(haelfte + abstandweit + (int) ((drei - abstandweit) * 0.5), abstandhoch + 4 * stdhoehe,
				drei - abstandweit, stdhoehe);
		btntitle.setBounds(haelfte + abstandweit, abstandhoch + 6 * stdhoehe, (int) ((drei - abstandweit) * 0.5),
				stdhoehe);
		tftitel.setBounds(haelfte+2*abstandweit+btntitle.getBounds().width, abstandhoch + 6 * stdhoehe, eingenschaften, stdhoehe);

	}

}
