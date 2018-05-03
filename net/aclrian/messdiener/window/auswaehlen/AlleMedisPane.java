package net.aclrian.messdiener.window.auswaehlen;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.window.WMainFrame;
import net.aclrian.messdiener.window.References;
//special thanks to https://learn-java-by-example.com/java/add-checkbox-items-jlist/
public class AlleMedisPane {

	private JList<CheckboxListItem> list = new JList<CheckboxListItem>();
	private ListModel<CheckboxListItem> mlist = new DefaultListModel<>();
	private Container container;
	private ArrayList<Messdiener> mediss = new ArrayList<Messdiener>();
	
	public AlleMedisPane(ArrayList<Messdiener> hauptarray, boolean nurleiter) {
		JFrame frame = new JFrame();
		frame.setIconImage(WMainFrame.getIcon(new References()));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Create a list containing CheckboxListItem's
		list.setModel(mlist);
		for (Messdiener element : hauptarray) {
			if (!nurleiter) {
				((DefaultListModel<CheckboxListItem>) mlist).addElement(new CheckboxListItem(element.makeId()));
				mediss.add(element);
			}
			else{
			if (element.isIstLeiter()) {
				((DefaultListModel<CheckboxListItem>) mlist).addElement(new CheckboxListItem(element.makeId()));
				mediss.add(element);
			}
			}
		}

		// Use a CheckboxListRenderer (see below)
		// to renderer list cells

		list.setCellRenderer(new CheckboxListRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Add a mouse listener to handle changing selection

		list.addMouseListener(new MouseAdapter() {
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

		frame.getContentPane().add(new JScrollPane(list));
		frame.pack();
		this.container = frame.getContentPane();
	}
	
	public Container getMedisinList(){
		return container;
		
	}
	
	public ArrayList<Messdiener> getAusgewaehlte(ArrayList<Messdiener> hauptarray){
		ArrayList<Messdiener> rtn = new ArrayList<Messdiener>();
		for (int i = 0; i < mlist.getSize(); i++) {
			CheckboxListItem item =	mlist.getElementAt(i);
			if(item.isSelected()){
				rtn.add(mediss.get(i));
			}
		}
		return rtn;
		
	}

}
class CheckboxListItem {
	private String label;
	private boolean isSelected = false;

	public CheckboxListItem(String label) {
		this.label = label;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String toString() {
		return label;
	}
}

class CheckboxListRenderer extends JCheckBox implements ListCellRenderer<CheckboxListItem> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2374514268224025896L;

	@Override
	public Component getListCellRendererComponent(JList<? extends CheckboxListItem> list, CheckboxListItem value,
			int index, boolean isSelected, boolean cellHasFocus) {
		setEnabled(list.isEnabled());
		setSelected(value.isSelected());
		setFont(list.getFont());
		setBackground(list.getBackground());
		setForeground(list.getForeground());
		setText(value.toString());
		return this;
	}
}
// Represents items in the list that can be selected



// Handles rendering cells in the list using a check box

