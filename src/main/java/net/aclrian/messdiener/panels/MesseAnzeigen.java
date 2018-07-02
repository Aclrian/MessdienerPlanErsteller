package net.aclrian.messdiener.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.aclrian.messdiener.components.ACheckBox;
import net.aclrian.messdiener.components.AList;
import net.aclrian.messdiener.messdiener.Messdiener;
import net.aclrian.messdiener.messe.Messe;
import net.aclrian.messdiener.resources.References;
import net.aclrian.messdiener.start.AProgress;
import net.aclrian.messdiener.start.WEinFrame;
import net.aclrian.messdiener.start.WEinFrame.EnumActivePanel;
import net.aclrian.messdiener.utils.Utilities;

public class MesseAnzeigen extends APanel {

    private static final long serialVersionUID = -2230487108865122325L;

    private ACheckBox chbxHochamt = new ACheckBox("Hochamt");
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
    private SpinnerNumberModel slmAnzahl = new SpinnerNumberModel(6, 0, 50, 1);
    private SpinnerListModel snmKirche;
    private SpinnerListModel snmTypen;
    private JTextField tftitel = new JTextField();
    private String title = null;
    private ACheckBox chbxnurleiter = new ACheckBox("nur Leiter");
    private SpinnerDateModel sdmDatum;
    private AList<Messdiener> alist;
    private ArrayList<Messdiener> eingeteilte = new ArrayList<Messdiener>();
    private JScrollPane scrollpane = new JScrollPane();
    private Messe moben;

    public MesseAnzeigen(int defaultButtonwidth, int defaultButtonheight, AProgress ap) {
	super(defaultButtonwidth, defaultButtonheight, true, ap);
	sdmDatum = new SpinnerDateModel(new Date(), null, null, Calendar.HOUR_OF_DAY);
	spinnerDatum.setModel(sdmDatum);
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
	add(scrollpane);
	scrollpane.setVisible(false);
	tftitel.setVisible(false);
	btntitle.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (btntitle.getText().equals("Titel " + References.ae + "ndern")) {
		    tftitel.setVisible(true);
		    if (tftitel.getText().equals("")) {
			if (title.equals("")) {
			    tftitel.setText(spinnerTyp.getModel().getValue().toString());
			} else {
			    tftitel.setText(title);
			}
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

	spinnerAnzahlMedis.setModel(slmAnzahl);
	String[] orte = new String[ap.getAda().getPfarrei().getOrte().size()];
	for (int i = 0; i < orte.length; i++) {
	    orte[i] = ap.getAda().getPfarrei().getOrte().get(i);
	}
	snmKirche = new SpinnerListModel(orte);
	spinnerKirche.setModel(snmKirche);

	String[] type = new String[ap.getAda().getPfarrei().getTypen().size()];
	for (int i = 0; i < type.length; i++) {
	    type[i] = ap.getAda().getPfarrei().getTypen().get(i);
	}
	snmTypen = new SpinnerListModel(type);
	spinnerTyp.setModel(snmTypen);
	btnSetzeManuell.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (btnSetzeManuell.getText().equals("manuell einteilen")) {
		    if (alleSindLeiter() || chbxnurleiter.isSelected()) {
			alist = new AList<Messdiener>(ap.getLeiter(ap.getAda().getMediarray()),
				Messdiener.compForMedis);
		    } else {
			alist = new AList<Messdiener>(ap.getAda().getMediarray(), Messdiener.compForMedis);
		    }
		    WEinFrame.farbe(alist, false);
		    alist.setSelected(eingeteilte, true);
		    eingeteilte = alist.getSelected();
		    scrollpane.setViewportView(alist);
		    scrollpane.setVisible(true);
		    graphics();
		    btnSetzeManuell.setText("Einteilen");
		    alist.setLayoutOrientation(AList.VERTICAL_WRAP);
		} else {
		    eingeteilte = alist.getSelected();
		    btnSetzeManuell.setText("manuell einteilen");
		    scrollpane.setVisible(false);
		}
	    }
	});
	getBtnSpeichern().addActionListener(e -> speichern(ap));
	getBtnAbbrechen().addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (Art.bearbeiten == getArt()) {
		    ap.getAda().addMesse(moben);
		}
		ap.getAda().getVoreingeteilte().put(moben, eingeteilte);
		ap.getWAlleMessen().changeAP(EnumActivePanel.Start, true);
	    }
	});
    }

    protected boolean alleSindLeiter() {
	if (eingeteilte.size() == 0) {
	    return false;
	}
	for (Messdiener messdiener : eingeteilte) {
	    if (!messdiener.isIstLeiter()) {
		return false;
	    }
	}
	return true;
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
	SimpleDateFormat sdf = new SimpleDateFormat("EEE");
	if (getArt() == Art.bearbeiten && sdf.format(m.getDate()).equals(sdf.format(moben.getDate()))) {
	    m.setStandartMesse(moben.getStandardMesse(), ap.getAda());
	}
	ap.getAda().getVoreingeteilte().put(m, eingeteilte);
	Utilities.logging(this.getClass(), "speichern", m.getID() + " wurde erstellt.");
	ap.getWAlleMessen().addMesse(m);
	ap.getWAlleMessen().changeAP(EnumActivePanel.Start, true);
    }

    public void setMesse(Messe m, AProgress ap) {
	setArt(Art.bearbeiten);
	this.moben = m;
	title = m.getTitle();
	chbxHochamt.setSelected(m.isHochamt());
	slmAnzahl.setValue(m.getAnz_messdiener());
	snmTypen.setValue(m.getMesseTyp());
	snmKirche.setValue(m.getKirche());
	sdmDatum.setValue(m.getDate());
	eingeteilte = ap.getAda().getVoreingeteilte().get(m);
	if (eingeteilte == null) {
	    eingeteilte = new ArrayList<>();
	}
	ap.getAda().getVoreingeteilte().remove(m);
    }

    @Override
    public void graphics() {
	int width = this.getBounds().width;
	int heigth = this.getBounds().height;
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
	tftitel.setBounds(haelfte + 2 * abstandweit + btntitle.getBounds().width, abstandhoch + 6 * stdhoehe,
		eingenschaften, stdhoehe);
	if (alist != null) {
	    scrollpane.setBounds(abstandweit, 9 * stdhoehe, width - 2 * abstandweit, 6 * stdhoehe);
	}
    }
}
