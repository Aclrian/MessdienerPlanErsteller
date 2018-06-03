package net.aclrian.messdiener.window.auswaehlen;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import net.aclrian.messdiener.deafault.Messverhalten;
import net.aclrian.messdiener.deafault.Sonstiges;
import net.aclrian.messdiener.deafault.StandartMesse;
import net.aclrian.messdiener.start.AData;

public class StandartMessenAuswaehlen extends JList<CheckboxListItem> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 662136861793559850L;
	private ArrayList<CheckboxListItem> cbli = new ArrayList<CheckboxListItem>();
	private ArrayList<StandartMesse> stdm = new ArrayList<StandartMesse>();
	private DefaultListModel<CheckboxListItem> dlm = new DefaultListModel<CheckboxListItem>();

	public StandartMessenAuswaehlen(ArrayList<StandartMesse> stdm) {

		// Create a list containing CheckboxListItem's
		this.setModel(dlm);

		for (int i1 = 0; i1 < stdm.size(); i1++) {
			if (new Sonstiges().isSonstiges(stdm.get(i1))) {
				continue;
			}
			cbli.add(new CheckboxListItem(stdm.get(i1).toString()));
			dlm.addElement(cbli.get(cbli.size() - 1));
			this.stdm.add(stdm.get(i1));
		}
		setLayout(null);

		/*
		 * JScrollPane scrollPane_1 = new JScrollPane(); //
		 * scrollPane_1.setBounds(0, 0, 430, 278); add(scrollPane_1);
		 * 
		 * scrollPane_1.setViewportView();
		 */
		// Use a CheckboxListRenderer (see below)
		// to renderer list cells

		this.setCellRenderer(new CheckboxListRenderer());
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Add a mouse listener to handle changing selection

		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
					@SuppressWarnings("unchecked")
					JList<CheckboxListItem> list = (JList<CheckboxListItem>) event.getSource();

					// Get index of item clicked

					int index = list.locationToIndex(event.getPoint());
					CheckboxListItem item = (CheckboxListItem) list.getModel().getElementAt(index);

					// Toggle selected state

					item.setSelected(!item.isSelected());

					// Repaint cell

					list.repaint(list.getCellBounds(index, index));
			}
		});
		this.setVisible(true);
	}

	public Messverhalten getMessverhalten(AData ada) {
		Messverhalten mv = new Messverhalten(ada);
		for (int i = 0; i < cbli.size(); i++) {
			StandartMesse sm = stdm.get(i);
			mv.editiereBestimmteMesse(ada, sm, cbli.get(i).isSelected());
		}
		return mv;
	}

	public void setMessverhalten(Messverhalten mv, AData ada) {
		dlm.removeAllElements();
		for (int i = 0; i < cbli.size(); i++) {
			StandartMesse sm = stdm.get(i);
			//Utilities.logging(getClass(), getClass().getEnclosingMethod(), sm.toString()+": "+mv.getBestimmtes(sm, wmf));
			//System.out.println(sm.toString());
			boolean b = mv.getBestimmtes(sm, ada);
			cbli.get(i).setSelected(b);
			//System.out.println(cbli.get(i).toString());
			dlm.addElement(cbli.get(i));
		}
		//Utilities.logging(getClass(), getClass().getEnclosingMethod(), "DEBUG: " + this.getModel().getSize());
	}
}

// Represents items in the list that can be selected

// Handles rendering cells in the list using a check box

/*
 * class CheckboxListRenderer2 extends JCheckBox implements
 * ListCellRenderer<CheckboxListItem> {
 * 
 *//**
	* 
	*//*
	 * private static final long serialVersionUID = 2374514268224025896L;
	 * 
	 * @Override public Component getListCellRendererComponent(JList<? extends
	 * CheckboxListItem> list, CheckboxListItem value, int index, boolean
	 * isSelected, boolean cellHasFocus) { setEnabled(list.isEnabled());
	 * setSelected(value.isSelected()); setFont(list.getFont());
	 * setBackground(list.getBackground()); setForeground(list.getForeground());
	 * setText(value.toString()); return this; } }
	 */

/**
 * 
 */
/*
 * private static final long serialVersionUID = 4878887357187626278L; private
 * ArrayList<ACheckBox> smms = new ArrayList<ACheckBox>();
 * 
 * private class ACheckBox extends JCheckBox {
 * 
 *//**
	* 
	*/
/*
 * private static final long serialVersionUID = 532035263264380447L; private
 * StandartMesse sm;
 * 
 * public ACheckBox(StandartMesse sm) { setText(sm.toString()); this.sm = sm; }
 * 
 * public StandartMesse getSm() { return sm; } }
 * 
 *//**
	 * Create the panel.
	 *//*
	 * public StandartMessenAuswaehlen(WMainFrame wmf) { this.setLayout(new
	 * FlowLayout(FlowLayout.LEADING, ));
	 * 
	 * ArrayList<StandartMesse> smms = new ArrayList<StandartMesse>();
	 * smms.add(new StandartMesse("Sa", 14, "00", "St_Martinus", 2,
	 * "Hochzeit")); smms.add(new StandartMesse("Sa", 18, "30", "St_Martinus",
	 * 6, "Messe")); smms.add(new StandartMesse("So", 10, "00", "St_Martinus",
	 * 6, "Messe")); smms.add(new StandartMesse("So", 15, "00", "St_Martinus",
	 * 2, "Taufe")); smms.add(new StandartMesse("Sa", 18, "00", "St_Martinus",
	 * 6, "Messe")); smms.add(new StandartMesse("Di", 19, "00", "Alt_St_Martin",
	 * 2, "Messe")); smms.add(new Sonstiges());
	 * 
	 * // smms.add(wmf.getSonstiges()); for (StandartMesse sm :
	 * wmf.getPfarrei().getStandardMessen()) { if (new
	 * Sonstiges().isSonstiges(sm)) { continue; } ACheckBox a = new
	 * ACheckBox(sm); smms.add(a); add(a); a.setVisible(true); }
	 * this.setVisible(true); }
	 * 
	 * public Messverhalten getMessverhalten(WMainFrame wmf) { Messverhalten rtn
	 * = new Messverhalten(wmf); for (ACheckBox acb : smms) {
	 * rtn.editiereBestimmteMesse(wmf, acb.getSm(), acb.isSelected()); } return
	 * rtn; }
	 * 
	 * public void setMessverhalten(Messverhalten mv, WMainFrame wmf) { for
	 * (ACheckBox aCheckBox : smms) { boolean kwm =
	 * mv.getBestimmtes(aCheckBox.getSm(), wmf); aCheckBox.setSelected(kwm); } }
	 * public static void main(String[] args) { WMainFrame wmf = new
	 * WMainFrame(); wmf.setVisible(false); StandartMessenAuswaehlen sma = new
	 * StandartMessenAuswaehlen(wmf); sma.setVisible(true);
	 * 
	 * } }
	 */