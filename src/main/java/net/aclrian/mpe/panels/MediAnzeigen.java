package net.aclrian.mpe.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;

import net.aclrian.mpe.components.ACheckBox;
import net.aclrian.mpe.components.ATextField;
import net.aclrian.mpe.components.MessverhaltenTable;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messdiener.WriteFile;
import net.aclrian.mpe.messe.Messverhalten;
import net.aclrian.mpe.resources.References;
import net.aclrian.mpe.start.AProgress;
import net.aclrian.mpe.start.WEinFrame.EnumActivePanel;
import net.aclrian.mpe.utils.Erroropener;
import net.aclrian.mpe.utils.RemoveDoppelte;
import net.aclrian.mpe.utils.Utilities;

public class MediAnzeigen extends APanel {
	private static final long serialVersionUID = 9198327596275692676L;
	private ATextField jtfvorname = new ATextField("Vorname");
	private ATextField jtfnachname = new ATextField("Nachname");
	private JLabel jahrgang_spinner_label = new JLabel("Eintritt");
	private JSpinner spinner = new JSpinner();
	private ACheckBox chckbxLeiter = new ACheckBox("Leiter");
	private JScrollPane geschwiscroll = new JScrollPane();
	private JLabel lblGeschwister = new JLabel("Geschwister");
	private JScrollPane freundscroll = new JScrollPane();
	private JLabel lblFreunde = new JLabel("Freunde");
	private JButton zuFreunde = new JButton(References.pfeilrechts);
	private JButton vonFreunde = new JButton(References.pfeillinks);
	private JButton zuGeschw = new JButton(References.pfeilrechts);
	private JButton vonGeschw = new JButton(References.pfeillinks);
	private MessverhaltenTable atable = new MessverhaltenTable();
	private JScrollPane scrol = new JScrollPane();
	private DefaultListModel<Messdiener> dlmfr = new DefaultListModel<Messdiener>();
	private JList<Messdiener> freundeview = new JList<Messdiener>(dlmfr);
	private DefaultListModel<Messdiener> dlmge = new DefaultListModel<Messdiener>();
	private JList<Messdiener> geschwisterview = new JList<Messdiener>(dlmge);
	private final int currentyear = Calendar.getInstance().get(Calendar.YEAR);
	private Messdiener moben;
	private ArrayList<Messdiener> freunde = new ArrayList<Messdiener>();
	private ArrayList<Messdiener> geschwi = new ArrayList<Messdiener>();
	// private JLabel warnung = new JLabel(
	// "Bitte vorm Speichern die Fenster Gr" + References.oe + References.ss + "e "
	// + References.ae + "ndern!");
	private ArrayList<Messdiener> bearbeitete = new ArrayList<Messdiener>();

	public MediAnzeigen(int dfbtnwidth, int dfbtnheight, AProgress ap) {
		super(dfbtnwidth, dfbtnheight, true, "Messdiener anzeigen", ap);
		setLayout(null);
		geschwisterview.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		freundeview.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		atable.setMessverhalten(new Messverhalten(ap.getAda()), ap.getAda());
		scrol.setViewportView(atable);
		spinner.setModel(new SpinnerNumberModel(currentyear, currentyear - 18, currentyear, 1));
		add(scrol);
		// add(warnung);
		add(jtfvorname);
		add(jtfnachname);
		add(spinner);
		add(chckbxLeiter);
		add(geschwiscroll);
		add(zuFreunde);
		add(zuGeschw);
		add(vonFreunde);
		add(vonGeschw);
		geschwiscroll.setColumnHeaderView(lblGeschwister);
		geschwiscroll.setViewportView(geschwisterview);
		add(jahrgang_spinner_label);
		add(freundscroll);
		freundscroll.setColumnHeaderView(lblFreunde);
		freundscroll.setViewportView(freundeview);
		add(this.getBtnAbbrechen());
		add(this.getBtnSpeichern());
		getBtnAbbrechen().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (Art.bearbeiten == getArt()) {
					ap.getAda().addMedi(moben);
				}
				ap.getWAlleMessen().changeAP(EnumActivePanel.Start, true);
			}
		});

		getBtnSpeichern().addActionListener(e -> speichern(ap));

		vonFreunde.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					freunde.remove(freundeview.getSelectedValue());
					freundeview.remove(freundeview.getSelectedIndex());
					update();
				} catch (ArrayIndexOutOfBoundsException e2) {
				}
				update();
			}
		});
		vonGeschw.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					geschwi.remove(geschwisterview.getSelectedValue());
					geschwisterview.remove(geschwisterview.getSelectedIndex());
					update();
				} catch (ArrayIndexOutOfBoundsException e2) {
					Utilities.logging(getClass(), "vonGeschwi:ActionListener", e2.getMessage());
				}
				update();
			}
		});
		zuFreunde.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (freunde.size() < Messdiener.freundelenght) {
					try {
						Messdiener m = ap.getWAlleMessen().getMedi();
						freunde.add(m);
						dlmfr.addElement(m);
					} catch (NullPointerException e2) {
						new Erroropener(new Exception("erst einen Messdiener ausw" + References.ae + "hlen"));
					}
					update();
				} else {
					new Erroropener(
							new Exception("Es können nur maximal " + Messdiener.freundelenght + " Messdiener eingetragen werden."));
				}
				update();
			}
		});
		zuGeschw.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (geschwi.size() < Messdiener.geschwilenght) {
					try {
						Messdiener m = ap.getWAlleMessen().getMedi();
						geschwi.add(m);
						((DefaultListModel<Messdiener>) geschwisterview.getModel()).addElement(m);
					} catch (NullPointerException e2) {
						new Erroropener(new Exception("erst einen Messdiener ausw" + References.ae + "hlen"));
					}
					update();

				} else {
					new Erroropener(
							new Exception("Es können nur maximal " + Messdiener.geschwilenght + " Messdiener eingetragen werden."));
				}
				update();
			}
		});
		graphics();
		setVisible(true);
	}

	protected void update() {
		RemoveDoppelte<Messdiener> rf = new RemoveDoppelte<>();
		rf.removeDuplicatedEntries(freunde);
		dlmfr.removeAllElements();
		for (Messdiener messdiener : freunde) {
			dlmfr.addElement(messdiener);
		}
		rf.removeDuplicatedEntries(geschwi);
		dlmge.removeAllElements();
		for (Messdiener messdiener : geschwi) {
			dlmge.addElement(messdiener);
		}
	}

	@Override
	public void graphics() {
		update();
		int width = this.getBounds().width;
		int heigth = this.getBounds().height;
		int drei = width / 3;
		int stdhoehe = heigth / 20;
		int abstandhoch = heigth / 100;
		int abstandweit = width / 100;
		int pfeilgr = drei / 2;
		int pfeilabst = drei / 4;
		jtfvorname.setBounds(abstandweit, abstandhoch + stdhoehe, drei - abstandweit, stdhoehe);
		jtfnachname.setBounds(drei + abstandweit, abstandhoch + stdhoehe, drei - 1 * abstandweit, stdhoehe);
		chckbxLeiter.setBounds(2 * drei + abstandweit, abstandhoch + stdhoehe, drei - 2 * abstandweit, stdhoehe);
		getBtnAbbrechen().setBounds(abstandweit, heigth - abstandhoch - this.getDfbtnheight(), this.getDfbtnwidth(),
				this.getDfbtnheight());
		getBtnSpeichern().setBounds(width - abstandweit - this.getDfbtnwidth(),
				heigth - abstandhoch - this.getDfbtnheight(), this.getDfbtnwidth(), this.getDfbtnheight());
		scrol.setBounds(abstandweit, (int) (2 * stdhoehe + 2 * abstandhoch), drei + drei - abstandweit,
				(int) (3.5 * stdhoehe));
		spinner.setBounds((int) (2.25 * drei + abstandweit), abstandhoch + 2 * stdhoehe,
				(int) (0.5 * drei - 2 * abstandweit), stdhoehe);
		jahrgang_spinner_label.setBounds(2 * drei + abstandweit, abstandhoch + 2 * stdhoehe,
				(int) (0.5 * drei - 2 * abstandweit), stdhoehe);
		freundscroll.setBounds(drei, 7 * stdhoehe, drei, 4 * stdhoehe);
		geschwiscroll.setBounds(drei, 12 * stdhoehe, drei, 4 * stdhoehe);
		vonFreunde.setBounds(pfeilabst, (int) (7.5 * stdhoehe), pfeilgr, stdhoehe);
		zuFreunde.setBounds(pfeilabst, (int) (9.5 * stdhoehe), pfeilgr, stdhoehe);
		vonGeschw.setBounds(pfeilabst, (int) (12.5 * stdhoehe), pfeilgr, stdhoehe);
		zuGeschw.setBounds(pfeilabst, (int) (14.5 * stdhoehe), pfeilgr, stdhoehe);
		// warnung.setBounds(getBtnAbbrechen().getBounds().x +
		// getBtnAbbrechen().getBounds().width + abstandweit,
		// heigth - stdhoehe, width - 2 * abstandweit, stdhoehe - abstandhoch);
	}

	public void setMedi(Messdiener medi, AProgress ap) {
		setArt(Art.bearbeiten);
		this.moben = medi;
		atable.setMessverhalten(medi.getDienverhalten(), ap.getAda());
		chckbxLeiter.setSelected(medi.isIstLeiter());
		ArrayList<Messdiener> medis = new ArrayList<Messdiener>();
		medis.addAll(ap.getAda().getMediarray());
		medis.removeIf(e -> e.toString().equals(medi.toString()));
		setMedis(medi.getFreunde(), ap, freundeview, freunde);
		setMedis(medi.getGeschwister(), ap, geschwisterview, geschwi);
		try {
			((SpinnerNumberModel) spinner.getModel()).setValue(medi.getEintritt());
		} catch (IllegalArgumentException e) {
			if ((medi.getEintritt() < (currentyear - 18))) {
				medi.setEintritt(currentyear - 18);
				((SpinnerNumberModel) spinner.getModel()).setValue(medi.getEintritt());
			}
		}
		jtfvorname.setText(medi.getVorname());
		jtfnachname.setText(medi.getNachnname());
	}

	private void setMedis(String[] anv, AProgress ap, JList<Messdiener> m, ArrayList<Messdiener> array) {
		for (String string : anv) {
			for (Messdiener medi : ap.getAda().getMediarray()) {
				if (medi.toString().equals(string)) {
					((DefaultListModel<Messdiener>) m.getModel()).addElement(medi);
					array.add(medi);
					continue;
				}
			}
		}
	}

	public void speichern(AProgress ap) {
		if (geschwi.size() <= 3 && freunde.size() <= 5 && !jtfvorname.getText().equals("")
				&& !jtfnachname.getText().equals("")) {
			// ME
			Messdiener m = new Messdiener(null);
			if (getArt() == Art.bearbeiten) {
				m.setFile(moben.getFile());
			}
			m.setzeAllesNeu(jtfvorname.getText(), jtfnachname.getText(),
					(int) ((SpinnerNumberModel) spinner.getModel()).getValue(), chckbxLeiter.isSelected(),
					atable.getMessdaten(ap.getAda()));
			if (getArt() == Art.bearbeiten) {
				boolean b = moben.getNachnname().equals(m.getNachnname());
				boolean bo = moben.getVorname().equals(m.getVorname());
				if (b == false || bo == false) {
					m.getFile().delete();
				}
			}
			m.setFreunde(getArrayString(freunde, m.getFreunde().length));
			m.setGeschwister(getArrayString(geschwi, m.getGeschwister().length));
			for (Messdiener messdiener : ap.getAda().getMediarray()) {
				alteloeschen(m, messdiener.getFreunde(), bearbeitete);
				alteloeschen(m, messdiener.getGeschwister(), bearbeitete);
				if (moben != null) {
					alteloeschen(moben, messdiener.getFreunde(), bearbeitete);
					alteloeschen(moben, messdiener.getGeschwister(), bearbeitete);
				}
			}
			for (int i = 0; i < geschwi.size(); i++) {
				Messdiener medi = geschwi.get(i);
				addBekanntschaft(medi, m, true);
			}
			for (int i = 0; i < freunde.size(); i++) {
				Messdiener medi = freunde.get(i);
				addBekanntschaft(medi, m, false);
			}
			// speichern
			RemoveDoppelte<Messdiener> rd = new RemoveDoppelte<Messdiener>();
			rd.removeDuplicatedEntries(bearbeitete);
			try {
				for (Messdiener messdiener : bearbeitete) {
					WriteFile wf = new WriteFile(messdiener, ap.getAda().getUtil().getSavepath());
					wf.toXML(ap);
				}
				WriteFile wf = new WriteFile(m, ap.getAda().getSavepath());
				wf.toXML(ap);
				Utilities.logging(this.getClass(), "speichern", "Messdiener " + m.makeId() + " wurde gespeichert!");
			} catch (IOException e) {
				new Erroropener(new Exception("Konnte nicht Speichern! Wegen: " + e.getCause()));
				e.printStackTrace();
			}
			ap.getWAlleMessen().changeAP(EnumActivePanel.Start, true);
		} else if (geschwi.size() > 3) {
			new Erroropener(new Exception("Man kann nur maximal 3 Geschwister haben"));
		} else if (freunde.size() > 5) {
			new Erroropener(new Exception("Man kann nur maximal 5 Freunde haben"));
		} else {
			new Erroropener(new Exception("Trage einen richtigen Namen ein!"));
		}
	}

	private void addBekanntschaft(Messdiener medi, Messdiener woben, boolean isGeschwister) {
		if (!isGeschwister) {// Freunde
			// fuer medi
			ArrayList<String> leins = new ArrayList<String>((List<String>) Arrays.asList(medi.getFreunde()));
			leins.add(woben.toString());
			leins.removeIf(t -> t.equals(""));
			String[] s = new String[5];
			leins.toArray(s);
			for (int i = 0; i < s.length; i++) {
				if (s[i] == null) {
					s[i] = "";
				}

			}
			medi.setFreunde(s);
		} else {
			ArrayList<String> leins = new ArrayList<String>((List<String>) Arrays.asList(medi.getGeschwister()));
			leins.add(woben.toString());
			leins.removeIf(t -> t.equals(""));
			String[] s = new String[5];
			leins.toArray(s);
			for (int i = 0; i < s.length; i++) {
				if (s[i] == null) {
					s[i] = "";
				}

			}
			medi.setGeschwister(s);
		}
		bearbeitete.add(medi);
	}

	private String[] getArrayString(ArrayList<Messdiener> freunde2, int begr) {
		String[] s = new String[begr];
		for (int i = 0; i < s.length; i++) {
			try {
				s[i] = freunde2.get(i).toString();
			} catch (IndexOutOfBoundsException | NullPointerException e) {
				s[i] = new String("");
			}
		}
		return s;
	}

	public static void alteloeschen(Messdiener m, String[] array, ArrayList<Messdiener> ueberarbeitete) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(m.toString())) {
				array[i] = "";
				ueberarbeitete.add(m);
			}
		}
	}
}