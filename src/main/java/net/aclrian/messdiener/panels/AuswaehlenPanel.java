package net.aclrian.messdiener.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JLabel;
import javax.swing.JScrollPane;

import net.aclrian.messdiener.pictures.References;
import net.aclrian.messdiener.start.AProgress;
import net.aclrian.messdiener.window.auswaehlen.AList;

public class AuswaehlenPanel<E> extends APanel {

	private static final long serialVersionUID = -4151999826909159522L;

	private JScrollPane pane = new JScrollPane();
	private JLabel label = new JLabel("Ausw"+References.ae+"hlen");
	private AList<E> alist;
	
	public AuswaehlenPanel(int dfbtnwidth, int dfbtnheight, ArrayList<E> hauptarray, Comparator<? super E> comp, AProgress ap) {
		super(dfbtnwidth,dfbtnheight,true, ap);
		alist = new AList<E>(hauptarray, comp);
		add(pane);
		pane.setViewportView(alist);
		pane.setColumnHeaderView(label);
		getBtnSpeichern().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(alist.getSelected());
				
			}
		});
		//alist.setLayoutOrientation(AList.VERTICAL_WRAP);//HORIZONTAL_WRAP);
		graphics();
	}
	
	public AList<E> getAlist() {
		return alist;
	}
	
	@Override
	public void graphics() {
		int width = this.getBounds().width;
		int heigth = this.getBounds().height;
		//int drei = width / 3;
		//int stdhoehe = heigth / 20;
		int abstandhoch = heigth / 100;
		int abstandweit = width / 100;
		int eingenschaften = width / 5;
		//int haelfte = width / 2;
		getBtnAbbrechen().setBounds(abstandweit, heigth - abstandhoch - this.getDfbtnheight(), this.getDfbtnwidth(),
				this.getDfbtnheight());
		getBtnSpeichern().setBounds(width - abstandweit - this.getDfbtnwidth(),
				heigth - abstandhoch - this.getDfbtnheight(), this.getDfbtnwidth(), this.getDfbtnheight());
		//pane.setBounds(this.getDfbtnwidth()+abstandweit, abstandhoch, abstandweit+width-4*abstandweit-2*getDfbtnwidth(), heigth - 3*abstandhoch - this.getDfbtnheight());
		pane.setBounds(abstandweit, abstandhoch, 4*eingenschaften, heigth - 3*abstandhoch - this.getDfbtnheight());
		//alist.setBorder(WAlleMessen.b);
		//System.out.println(alist.getBounds());
	}
}
