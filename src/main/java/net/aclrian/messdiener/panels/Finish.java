package net.aclrian.messdiener.panels;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.pictures.References;
import net.aclrian.messdiener.start.AProgress;

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
	private JPanel panel = new JPanel();

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
		add(panel);
		panel.add(todocx);
		panel.add(topdf);
		panel.add(toback);
		graphics();
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
		// int haelfte = width / 2;
		sPMedis.setBounds(abstandweit, abstandhoch, drei - abstandweit,
				heigth - (int) (1.1 * stdhoehe) - 4 * abstandhoch);
		sPMessen.setBounds(drei + abstandweit, abstandhoch, 2 * drei - 3 * abstandweit,
				heigth - (int) (1.1 * stdhoehe) - 4 * abstandhoch);

		panel.setBounds(abstandweit, heigth - (int) (1.1 * stdhoehe) - abstandhoch, width - 2 * abstandweit,
				(int) (1.1 * stdhoehe));
		int w = width - 2 * abstandweit;
		double dif = (1.1 * stdhoehe) - stdhoehe;
		topdf.setBounds((int) (3 * w * 0.1 + 2 * eingenschaften), (int) (dif * 0.5), eingenschaften, stdhoehe);
		todocx.setBounds((int) (2 * w * 0.1 + 1 * eingenschaften), (int) (dif * 0.5), eingenschaften, stdhoehe);
		toback.setBounds((int) (w * 0.1 + 0 * eingenschaften), (int) (dif * 0.5), eingenschaften, stdhoehe);
	}
}
