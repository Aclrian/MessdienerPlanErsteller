package net.aclrian.messdiener.panels;

import javax.swing.JLabel;

import net.aclrian.messdiener.differenzierung.Pfarrei;
import net.aclrian.messdiener.start.AProgress;
import net.aclrian.messdiener.window.APanel;

public class Start extends APanel {

	private static final long serialVersionUID = 7258230476105946056L;

	private JLabel title = new JLabel("<html><body><h1>MessdienerplanErsteller</h1></body></html>");
	private JLabel unterueberschrift = new JLabel();
	private JLabel pfarreilabel = new JLabel();

	public Start(int dfbtnheigth, int dfbtnwidht, Pfarrei pf) {
		super(dfbtnwidht, dfbtnheigth, false);
		unterueberschrift.setText("<html><body><h2>version: " + AProgress.VersionID + " von Aclrian</h2></body></html>");
		pfarreilabel.setText("<html><body><h2>und der Messdienergemeinschaft <i>" + pf.getName() + " </h2></body></html>");
		add(title);
		add(unterueberschrift);
		add(pfarreilabel);
	}

	@Override
	protected void graphics(int width, int heigth) {
		int drei = width / 3;
		int stdhoehe = heigth / 20;
		int abstandhoch = heigth / 100;
		int abstandweit = width / 100;
		int eingenschaften = width / 5;
		int haelfte = width / 2;
		title.setBounds(abstandweit, abstandhoch, width-2*abstandweit, stdhoehe);
		unterueberschrift.setBounds(abstandweit, 2*abstandhoch+stdhoehe, width-2*abstandweit, stdhoehe);
		pfarreilabel.setBounds(abstandweit, 3*abstandhoch+2*stdhoehe, width-2*abstandweit, stdhoehe);
		
	}

}
