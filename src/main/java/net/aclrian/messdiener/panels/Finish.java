package net.aclrian.messdiener.panels;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import net.aclrian.convertFiles.Converter;
import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.Messe;
import net.aclrian.messdiener.deafault.Sonstiges;
import net.aclrian.messdiener.deafault.StandartMesse;
import net.aclrian.messdiener.differenzierung.Einstellungen;
import net.aclrian.messdiener.pictures.References;
import net.aclrian.messdiener.start.AData;
import net.aclrian.messdiener.start.AProgress;
import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.utils.RemoveDoppelte;
import net.aclrian.messdiener.utils.Utilities;

enum EnumAction {
    EinfachEinteilen(), TypeBeachten();
}

public class Finish extends APanel {
    private static final long serialVersionUID = 1100706202326699632L;

    private JEditorPane editorPane = new JEditorPane("text/html", "");
    private DefaultListModel<Messdiener> dlm = new DefaultListModel<Messdiener>();
    private JList<Messdiener> list = new JList<Messdiener>(dlm);
    private JScrollPane sPMessen = new JScrollPane();
    private JScrollPane sPMedis = new JScrollPane();
    private JLabel labelmesse = new JLabel("Die generierten Messen:");
    private JLabel labelmedis = new JLabel("Nicht eingeteilte Messdiener:");
    private JButton topdf = new JButton("Zum Pfd-Dokument");
    private JButton todocx = new JButton("Zum Word-Dokument");
    private JButton toback = new JButton("Zur" + References.ue + "ck");
    // private JPanel panel = new JPanel();

    private Converter con;
    private ArrayList<Messdiener> hauptarray;
    private ArrayList<Messe> messen;

    public Finish(int defaultButtonwidth, int defaultButtonheight, AProgress ap) {
	super(defaultButtonwidth, defaultButtonheight, false, ap);
	HTMLEditorKit editorkit = new HTMLEditorKit();
	editorPane.setEditorKit(editorkit);
	sPMessen.setViewportView(editorPane);
	sPMessen.setColumnHeaderView(labelmesse);
	sPMedis.setViewportView(list);
	sPMedis.setColumnHeaderView(labelmedis);
	editorPane.setText("<html><h1>Der Fertige Messdienerplan</h1></html>");
	add(sPMedis);
	add(sPMessen);

	add(todocx);
	add(topdf);
	add(toback);
	this.hauptarray = ap.getMediarraymitMessdaten();
	this.messen = ap.getAda().getMesenarray();
	// Generieren:
	// Vorzeitig
	for (Messe messe : messen) {
	    ArrayList<Messdiener> vor = ap.getAda().getVoreingeteilte().get(messe);
	    if (vor != null) {
		for (int i = 0; i < vor.size(); i++) {
		    Messdiener messdiener = vor.get(i);
		    for (Messdiener messdiener2 : ap.getMediarraymitMessdaten()) {
			if (messdiener.toString().equals(messdiener2.toString())) {
			    messe.vorzeitigEiteilen(messdiener2, ap.getAda());
			}
		    }
		}
	    }
	}

	generate(ap);
	// to JEditPane
	StringBuffer s = new StringBuffer("<html>");
	for (int i = 0; i < messen.size(); i++) {
	    Messe messe = messen.get(i);
	    String m1 = messe.htmlAusgeben();
	    m1 = m1.substring(6, m1.length() - 7);
	    if (i == 0) {
		Date start = messe.getDate();
		Date ende = messen.get(messen.size() - 1).getDate();
		SimpleDateFormat df = new SimpleDateFormat("dd. MMMM", Locale.GERMAN);
		String text = "Messdienerplan vom " + df.format(start) + " bis " + df.format(ende);
		s.append("<h1>" + text + "</h1>");
	    }
	    s.append("<br>" + m1 + "</br>");
	}
	s.append("</html>");
	editorPane.setText(s.toString());
	for (Messdiener medi : hauptarray) {
	    if (medi.getMessdatenDaten().getInsgesamtEingeteilt() == 0) {
		dlm.addElement(medi);
	    }
	}
	/*
	 * try { con = new Converter(this); } catch (IOException e1) { new
	 * Erroropener(e1.getMessage()); e1.printStackTrace(); }
	 */
	// ---------------.-------------
	Toolkit.getDefaultToolkit().beep();
	// Ende
	graphics();
	ap.getWAlleMessen().graphics();
	graphics();
    }

    private void generate(AProgress ap) {
	neuerAlgorythmus(ap);
    }

    public void neuerAlgorythmus(AProgress ap) {
	hauptarray.sort(Messdiener.einteilen);
	for (int i = 0; i < hauptarray.size(); i++) {
	    System.out.println(hauptarray.get(i).getMessdatenDaten().getAnz_messen() + "/"
		    + hauptarray.get(i).getMessdatenDaten().getMax_messen());
	}
	System.out.println("-------");
	// Dap und zvmzwm fruehzeitg erkennen und beheben
	// ArrayList<ArrayList<E>>
	for (StandartMesse sm : ap.getAda().getPfarrei().getStandardMessen()) {
	    int anz_real = 0;
	    int anz_monat = 0;
	    ArrayList<Messdiener> array = new ArrayList<Messdiener>();
	    if (!(AData.sonstiges.isSonstiges(sm))) {
		for (Messdiener medi : hauptarray) {
		    int id = 0;
		    if (medi.isIstLeiter()) {
			id++;
		    }
		    int ii = ap.getAda().getPfarrei().getSettings().getDaten()[id].getAnz_dienen();
		    if (medi.getDienverhalten().getBestimmtes(sm, ap.getAda()) && ii != 0) {
			array.add(medi);
			anz_monat += medi.getMessdatenDaten().getkannnochAnz();
		    }
		}
		anz_real = anz_monat;
		int benoetigte_anz = 0;
		ArrayList<Messe> dfmessen = new ArrayList<Messe>();
		Calendar start = Calendar.getInstance();
		start.setTime(messen.get(0).getDate());
		start.add(Calendar.MONTH, 1);
		System.out.println();
		for (Messe me : messen) {
		    if (!AData.sonstiges.isSonstiges(me.getStandardMesse())) {
			if (me.getDate().after(start.getTime())) {
			    anz_real = anz_real + anz_monat;
			    start.setTime(me.getDate());
			}
			dfmessen.add(me);
			benoetigte_anz += sm.getAnz_messdiener();
		    }
		}
		if (benoetigte_anz > anz_real) {
		    int fehlt = benoetigte_anz - anz_real;
		    try {
			int medishinzufuegen = (int) Math.ceil((double) (fehlt / array.size()));// abrunden
			Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),
				"Es wurde abgerundet: " + (double) (fehlt / array.size()) + "-->" + medishinzufuegen);
			for (Messdiener messdiener : array) {
			    messdiener.getMessdatenDaten().addtomaxanz(medishinzufuegen, ap.getAda(),
				    messdiener.isIstLeiter());
			}
			Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),
				"Zu den Messdienern, die am " + sm.getWochentag() + " um " + sm.getBeginn_stunde()
					+ " koennen, werden + " + medishinzufuegen
					+ " zu ihrem normalen Wert hinzugefuegt!");

		    } catch (ArithmeticException e) {
			Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),
				"Kein Messdiener kann: " + sm.getWochentag() + sm.getBeginn_stunde() + ":"
					+ sm.getBeginn_minute());
		    }
		}
	    }
	}
	// DAV UND ZWMZVM ENDE
	Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),
		References.Ue + "berpruefung zu ende!");
	// neuer Monat:
	Calendar start = Calendar.getInstance();
	start.setTime(messen.get(0).getDate());
	start.add(Calendar.MONTH, 1);
	SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
	Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),
		"n" + References.ae + "chster Monat bei: " + df.format(start.getTime()));
	// EIGENTLICHER ALGORYTHMUS
	for (int i = 0; i < messen.size(); i++) {
	    Messe me = messen.get(i);
	    // while(m.isfertig() || stackover){
	    if (me.getDate().after(start.getTime())) {
		start.add(Calendar.MONTH, 1);
		Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),
			"naechster Monat: Es ist " + df.format(me.getDate()));
		for (Messdiener messdiener : hauptarray) {
		    messdiener.getMessdatenDaten().naechsterMonat();
		}
	    }
	    Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), "Messe dran: " + me.getID());
	    if (me.getStandardMesse() instanceof Sonstiges) {
		this.einteilen(me, EnumAction.EinfachEinteilen, ap);
	    } else {
		this.einteilen(me, EnumAction.TypeBeachten, ap);
	    }
	    Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), "Messe fertig: " + me.getID());
	}
    }

    private void einteilen(Messe m, EnumAction act, AProgress ap) {
	switch (act) {
	case EinfachEinteilen:
	    ArrayList<Messdiener> medis;
	    boolean zwang = false;
	  //  try {
		medis = get(AData.sonstiges, m, ap.getAda());
	//    } catch (Exception e) {
	//	Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), e.getMessage());
		// medis = beheben(m, ap.getAda());
	//	zwang = true;
	 //   }
	    Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),
		    "\t" + medis.size() + " f" + References.ue + "r " + m.getnochbenoetigte());
	    if (m.getnochbenoetigte() < medis.size()) {
		System.out.println("Die Messe " + m.getID() + "hat zu wenige Messdiener");
		new Erroropener("Die Messe " + m.getID() + "hat zu wenige Messdiener");
	    }
	    for (int j = 0; j < medis.size(); j++) {
		einteilen(m, medis.get(j), zwang, ap);
	    }
	    break;
	case TypeBeachten:
	    ArrayList<Messdiener> medis2;
	    boolean zwang2 = false;
	  //  try {
		medis2 = get(m.getStandardMesse(), m, ap.getAda());
	    //} catch (Exception e) {
		//Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), e.getMessage());
		//medis2 = beheben(m, ap.getAda());
		//zwang2 = true;
	   // }
	    Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),
		    "\t" + medis2.size() + " f" + References.ue + "r " + m.getnochbenoetigte());
	    if (m.getnochbenoetigte() < medis2.size()) {
		System.out.println("Die Messe " + m.getID() + "hat zu wenige Messdiener");
		new Erroropener("Die Messe " + m.getID() + "hat zu wenige Messdiener");
	    }
	    for (int j = 0; j < medis2.size(); j++) {
		einteilen(m, medis2.get(j), zwang2, ap);
	    }
	    break;
	default:
	    break;
	}
    }

    public void einteilen(Messe m, Messdiener medi, boolean zwang, AProgress ap) {
	boolean d = false;
	if (m.istFertig()) {
	    return;
	} else if (zwang) {
	    if (medi.getMessdatenDaten().kann(m.getDate(), zwang)) {
		m.einteilenZwang(medi);
		d = true;
	    }
	} else {
	    if (medi.getMessdatenDaten().kann(m.getDate(), zwang)) {
		m.einteilen(medi);
		d = true;
	    }
	}
	if (!m.istFertig() && d == true) {
	    ArrayList<Messdiener> anv = medi.getMessdatenDaten().getAnvertraute(ap.getMediarraymitMessdaten());

	    RemoveDoppelte<Messdiener> rd = new RemoveDoppelte<Messdiener>();
	    rd.removeDuplicatedEntries(anv);
	    if (anv.size() >= 1) {
		anv.sort(Messdiener.einteilen);
		for (Messdiener messdiener : anv) {
		    boolean b = messdiener.getDienverhalten().getBestimmtes(m.getStandardMesse(), ap.getAda());
		    if (m.getStandardMesse().toString().startsWith("D")) {
			System.out.println(b);
		    }

		    if (messdiener.getMessdatenDaten().kann(m.getDate(), zwang) && b) {
			Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),
				"\t" + messdiener.makeId() + " dient mit " + medi.makeId());
			einteilen(m, messdiener, zwang, ap);
		    }
		}
	    }
	}
    }

    public ArrayList<Messdiener> beheben(Messe m, AData ada) {
	ArrayList<Messdiener> rtn = get(m.getStandardMesse(), m.getDate(), ada);
	if (rtn.size() < m.getnochbenoetigte()) {
	    ArrayList<Messdiener> prov = new ArrayList<Messdiener>();
	    hauptarray.sort(Messdiener.einteilen);
	    int i = rtn.size();
	    for (Messdiener messdiener : hauptarray) {
		if (messdiener.getMessdatenDaten().kanndann(m.getDate(), false) && i < m.getnochbenoetigte()) {
		    prov.add(messdiener);
		    i++;
		}
	    }
	    for (Messdiener messdiener : prov) {
		new Erroropener("<html><body>Bei der Messe: " + m.getID()
			+ "<br></br>herrscht Messdiener-Knappheit</br><br>Daher wird wohl" + messdiener.makeId()
			+ "einspringen m" + References.ue + "ssen, weil er generell kann.</br></body></html>");
	    }
	    rtn.addAll(prov);
	    // Wenn wirklich keiner mehr kann
	    if (rtn.size() < m.getnochbenoetigte()) {
		hauptarray.sort(Messdiener.einteilen);
		Einstellungen e = ada.getPfarrei().getSettings();
		for (Messdiener messdiener : hauptarray) {
		    int id = 0;
		    if (messdiener.isIstLeiter()) {
			id++;
		    }
		    int ii = e.getDaten()[id].getAnz_dienen();
		    if (ii != 0 && i < m.getnochbenoetigte()) {
			new Erroropener("<html><body>Bei der Messe: " + m.getID() + "<br></br>herrscht GRO"
				+ References.GROssenSZ + "E Messdiener-Knappheit</br><br>Daher wird wohl"
				+ messdiener.makeId() + "einspringen m" + References.ue + "ssen.</br></body></html>");
			rtn.add(messdiener);
		    }
		}
	    } else {
		new Erroropener("<html><body>Die Messe:<br>" + m.getID()
			+ "</br><br>hat schon neue Messdiener bekommen, die schon zu oft eingeteilt sind, aber es herrscht Messdiener-Knappheit</br></body></html>");
	    }
	    // }
	}
	rtn.sort(Messdiener.einteilen);
	return rtn;
    }

    public ArrayList<Messdiener> get(StandartMesse sm, Messe m, AData ada) {
	ArrayList<Messdiener> al = new ArrayList<Messdiener>();
	for (Messdiener medi : hauptarray) {
	    // System.out.println(medi.makeId());
	    int id = 0;
	    if (medi.isIstLeiter()) {
		id++;
	    }
	    int ii = ada.getPfarrei().getSettings().getDaten()[id].getAnz_dienen();
	    if (medi.getDienverhalten().getBestimmtes(sm, ada) == true && ii != 0
		    && medi.getMessdatenDaten().kann(m.getDate(), false)) {
		al.add(medi);
	    }
	}
	Collections.shuffle(al);
	al.sort(Messdiener.einteilen);
	return al;
    }

    public ArrayList<Messdiener> get(StandartMesse sm, Date d, AData ada) {
	ArrayList<Messdiener> al = new ArrayList<Messdiener>();
	ArrayList<Messdiener> al2 = new ArrayList<Messdiener>();
	for (Messdiener medi : hauptarray) {
	    // System.out.println(medi.makeId());
	    int id = 0;
	    if (medi.isIstLeiter()) {
		id++;
	    }
	    int ii = ada.getPfarrei().getSettings().getDaten()[id].getAnz_dienen();
	    if (medi.getDienverhalten().getBestimmtes(sm, ada) == true && ii != 0) {
		if (medi.getMessdatenDaten().kann(d, false)) {
		    al.add(medi);
		} else if (medi.getMessdatenDaten().kann(d, true)) {
		    al2.add(medi);
		}
	    }
	}
	Collections.shuffle(al);
	Collections.shuffle(al2);
	al.sort(Messdiener.einteilen);
	for (int i = 0; i < al.size(); i++) {
	    System.out.println(al.get(i).getMessdatenDaten().getAnz_messen() + "/"
		    + al.get(i).getMessdatenDaten().getMax_messen());
	}
	System.out.println("-------");

	al2.sort(Messdiener.einteilen);
	for (int i = 0; i < al2.size(); i++) {
	    System.out.println(al2.get(i).getMessdatenDaten().getAnz_messen() + "/"
		    + al2.get(i).getMessdatenDaten().getMax_messen());
	}
	System.out.println("-------");

	al.addAll(al2);
	for (int i = 0; i < al.size(); i++) {
	    System.out.println(al.get(i).getMessdatenDaten().getAnz_messen() + "/"
		    + al.get(i).getMessdatenDaten().getMax_messen());
	}
	System.out.println("-------");

	return al;
    }
/*
    private class NotEnughtMedis extends Exception {
	/**
	 * 
	 * /
	private static final long serialVersionUID = 1730056704969911415L;
	private Messe m;
	private Thread t = new Thread() {
	    @Override
	    public void run() {
		new Erroropener(getMessage());
	    }
	};

	public NotEnughtMedis(Messe m) {
	    this.m = m;
	    t.run();
	}

	@Override
	public String getMessage() {
	    String s = super.getMessage();
	    s += "Zu wenige M" + References.oe + "gliche Messdiener bei: " + m.getID();

	    return s;
	}
    }
*/
    public String getText() {
	return editorPane.getText();
    }

    @Override
    public void graphics() {
	int width = this.getBounds().width;
	int heigth = this.getBounds().height;
	int drei = width / 3;
	// int stdhoehe = heigth / 20;
	int abstandhoch = heigth / 100;
	int abstandweit = width / 100;
	// int eingenschaften = width / 5;
	// int haelfte = width / 2;
	sPMedis.setBounds(abstandweit, abstandhoch, drei - abstandweit,
		heigth - this.getDfbtnheight() - 4 * abstandhoch);
	sPMessen.setBounds(drei + abstandweit, abstandhoch, 2 * drei - 3 * abstandweit,
		heigth - this.getDfbtnheight() - 4 * abstandhoch);

	getBtnAbbrechen().setBounds(abstandweit, heigth - abstandhoch - this.getDfbtnheight(), this.getDfbtnwidth(),
		this.getDfbtnheight());
	getBtnSpeichern().setBounds(width - abstandweit - this.getDfbtnwidth(),
		heigth - abstandhoch - this.getDfbtnheight(), this.getDfbtnwidth(), this.getDfbtnheight());
	toback.setBounds(abstandweit, heigth - abstandhoch - this.getDfbtnheight(), this.getDfbtnwidth(),
		this.getDfbtnheight());
	Rectangle r = toback.getBounds();
	r.x = r.x + width;
	todocx.setBounds((int) (toback.getBounds().x + width * 0.5 - this.getDfbtnwidth() * 0.5), toback.getBounds().y,
		this.getDfbtnwidth(), this.getDfbtnheight());
	topdf.setBounds(width - abstandweit - this.getDfbtnwidth(), heigth - abstandhoch - this.getDfbtnheight(),
		this.getDfbtnwidth(), this.getDfbtnheight());
    }
}
