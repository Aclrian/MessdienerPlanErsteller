package net.aclrian.messdiener.panels;

import java.util.Calendar;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import net.aclrian.messdiener.deafault.ATextField;
import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.Messverhalten;
import net.aclrian.messdiener.pictures.References;
import net.aclrian.messdiener.start.AProgress;
import net.aclrian.messdiener.window.APanel;
import net.aclrian.messdiener.window.auswaehlen.ACheckBox;
import net.aclrian.messdiener.window.auswaehlen.ATable;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MediAnzeigen extends APanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 9198327596275692676L;
	private JTextField textField = new ATextField("Vorname");
	private JTextField textField_1 = new ATextField("Nachname");
	private JLabel jahrgang_spinner_label = new JLabel("Eintritt");
	private JSpinner spinner = new JSpinner();
	private ACheckBox chckbxLeiter = new ACheckBox("Leiter");
	private JScrollPane geschwiscroll = new JScrollPane();
	private JLabel lblGeschwister = new JLabel("Geschwister");
	private JButton vongeschwi = new JButton(References.pfeillinks);
	private JButton zugeschwi = new JButton(References.pfeilrechts);
	private JButton vonfreund = new JButton(References.pfeillinks);
	private JButton zufreund = new JButton(References.pfeilrechts);
	private JScrollPane freundscroll = new JScrollPane();
	private JLabel lblFreunde = new JLabel("Freunde");
	private ATable atable = new ATable();
	private JScrollPane scrol = new JScrollPane();
	private DefaultListModel<Messdiener> dfmgeschwi = new DefaultListModel<Messdiener>();
	private DefaultListModel<Messdiener> dfmfreunde = new DefaultListModel<Messdiener>();
	private JList<Messdiener> freundeview = new JList<Messdiener>(dfmfreunde);
	private JList<Messdiener> geschwisterview = new JList<Messdiener>(dfmgeschwi);
	private final int currentyear = Calendar.getInstance().get(Calendar.YEAR);

	/**
	 * Create the panel.
	 **/
	public MediAnzeigen(int dfbtnwidth, int dfbtnheight, AProgress ap) {// int withe, int heigh) {// , WMainFrame wmf) {
		super(dfbtnwidth, dfbtnheight, true);
		setLayout(null);
		atable.setMessverhalten(new Messverhalten(ap.getAda()), ap.getAda());
		scrol.setViewportView(atable);
		spinner.setModel(new SpinnerNumberModel(currentyear, currentyear - 18, currentyear, 1));
		add(scrol);
		add(textField);
		add(textField_1);
		add(spinner);
		add(chckbxLeiter);
		add(geschwiscroll);
		geschwiscroll.setColumnHeaderView(lblGeschwister);
		geschwiscroll.setViewportView(geschwisterview);
		zufreund.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				actwithButtons(EnumAnvertraut.freund, EnumVonZu.zu, ap);
			}
		});
		add(zufreund);
		vonfreund.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				actwithButtons(EnumAnvertraut.freund, EnumVonZu.von, ap);
			}
		});
		zugeschwi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				actwithButtons(EnumAnvertraut.geschwister, EnumVonZu.zu, ap);
			}
		});
		vongeschwi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				actwithButtons(EnumAnvertraut.geschwister, EnumVonZu.von, ap);
			}
		});
		add(vonfreund);
		add(zugeschwi);
		add(vongeschwi);
		add(jahrgang_spinner_label);
		add(freundscroll);
		freundscroll.setColumnHeaderView(lblFreunde);
		freundscroll.setViewportView(freundeview);
		add(this.getBtnAbbrechen());
		add(this.getBtnSpeichern());
		// add(atable);
		graphics(12 * dfbtnwidth, 80 / 7 * dfbtnheight);
		// graphics(12*dfbtnwidth, 80/7*dfbtnheight);
		setVisible(true);
	}

	protected void graphics(int width, int heigth) {
		int drei = width / 3;
		int stdhoehe = heigth / 20;
		int abstandhoch = heigth / 100;
		int abstandweit = width / 100;
		textField.setBounds(abstandweit, abstandhoch + stdhoehe, drei - abstandweit, stdhoehe);
		textField_1.setBounds(drei + abstandweit, abstandhoch + stdhoehe, drei - 1 * abstandweit, stdhoehe);
		chckbxLeiter.setBounds(2 * drei + abstandweit, abstandhoch + stdhoehe, drei - 2 * abstandweit, stdhoehe);
		getBtnAbbrechen().setBounds(abstandweit, heigth - abstandhoch - this.getDfbtnheight(), this.getDfbtnwidth(),
				this.getDfbtnheight());
		getBtnSpeichern().setBounds(width - abstandweit - this.getDfbtnwidth(), heigth - abstandhoch - this.getDfbtnheight(),
				this.getDfbtnwidth(), this.getDfbtnheight());
		scrol.setBounds(abstandweit, (int) (2 * stdhoehe + 2 * abstandhoch), drei + drei - abstandweit,
				(int) (3.5 * stdhoehe));
		spinner.setBounds((int) (2.25 * drei + abstandweit), abstandhoch + 2 * stdhoehe,
				(int) (0.5 * drei - 2 * abstandweit), stdhoehe);
		jahrgang_spinner_label.setBounds(2 * drei + abstandweit, abstandhoch + 2 * stdhoehe,
				(int) (0.5 * drei - 2 * abstandweit), stdhoehe);
		freundscroll.setBounds(drei, 7 * stdhoehe, drei, 4 * stdhoehe);
		double fhoehe = freundscroll.getBounds().y + freundscroll.getBounds().height * 0.5 - abstandhoch;
		geschwiscroll.setBounds(drei, 12 * stdhoehe, drei, 4 * stdhoehe);
		double ghoehe = geschwiscroll.getBounds().y + geschwiscroll.getBounds().height * 0.5 - abstandhoch;
		zufreund.setBounds((int) (0.25 * drei), (int) (fhoehe - (abstandhoch + stdhoehe) * 0.5), (int) (drei * 0.5),
				stdhoehe);
		vonfreund.setBounds((int) (0.25 * drei), (int) (fhoehe + (abstandhoch + stdhoehe) * 0.5), (int) (drei * 0.5),
				stdhoehe);
		zugeschwi.setBounds((int) (0.25 * drei), (int) (ghoehe - (abstandhoch + stdhoehe) * 0.5), (int) (drei * 0.5),
				stdhoehe);
		vongeschwi.setBounds((int) (0.25 * drei), (int) (ghoehe + (abstandhoch + stdhoehe) * 0.5), (int) (drei * 0.5),
				stdhoehe);
	}

	public void actwithButtons(EnumAnvertraut eav, EnumVonZu evz, AProgress ap) {
		System.out.println(eav.name()+evz.name());
	}

	private enum EnumAnvertraut {
		freund, geschwister;
	}

	private enum EnumVonZu {
		von, zu;
	}
}