package net.aclrian.messdiener.components;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import net.aclrian.messdiener.messe.Sonstiges;

public class AList<E> extends JList<ACheckBox> {
    // special thanks to
    // https://learn-java-by-example.com/java/add-checkbox-items-jlist/
    private static final long serialVersionUID = 334476946039754351L;
    private ListModel<ACheckBox> mlist = new DefaultListModel<ACheckBox>();
    private ArrayList<E> array = new ArrayList<E>();

    public AList(ArrayList<E> hauptarray, Comparator<? super E> comp) {
	hauptarray.removeIf(e -> e instanceof Sonstiges);
	if (comp != null) {
	    hauptarray.sort(comp);
	}
	
	setModel(mlist);
	for (E element : hauptarray) {
	    ((DefaultListModel<ACheckBox>) mlist).addElement(new ACheckBox(element.toString()));
	    array.add(element);
	}

	setCellRenderer(new CheckboxListRenderer());
	setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	addMouseListener(new MouseAdapter() {
	    public void mouseClicked(MouseEvent event) {
		if (event.getSource() instanceof AList<?>) {
		    AList<?> list = (AList<?>) event.getSource();
		    // Get index of item clicked
		    int index = list.locationToIndex(event.getPoint());
		    if (index > -1) {
			ACheckBox item = (ACheckBox) list.getModel().getElementAt(index);
			// Toggle selected state
			item.setSelected(!item.isSelected());
			// Repaint cell
			list.repaint(list.getCellBounds(index, index));
		    }
		}
	    }
	});
    }

    public ArrayList<E> getSelected() {
	ArrayList<E> rtn = new ArrayList<E>();
	for (int i = 0; i < mlist.getSize(); i++) {
	    if (mlist.getElementAt(i).isSelected()) {
		rtn.add(array.get(i));
	    }
	}
	return rtn;
    }

    public void setSelected(ArrayList<E> selected, boolean makeotherfalse) {
	if (makeotherfalse) {
	    for (int index = 0; index < mlist.getSize(); index++) {
		mlist.getElementAt(index).setSelected(false);
	    }
	}
	for (E elem : selected) {
	    for (int i = 0; i < mlist.getSize(); i++) {
		System.out.println(elem.toString());
		System.out.println(mlist.getElementAt(i).getText());
		if (elem.toString().equals(mlist.getElementAt(i).getText())) {
		    mlist.getElementAt(i).setSelected(true);
		    continue;
		}
	    }
	}
    }
}

class CheckboxListRenderer implements ListCellRenderer<ACheckBox> {

    @Override
    public Component getListCellRendererComponent(JList<? extends ACheckBox> list, ACheckBox value, int index,
	    boolean isSelected, boolean cellHasFocus) {
	value.setEnabled(list.isEnabled());
	value.setSelected(value.isSelected());
	value.setFont(list.getFont());
	value.setBackground(list.getBackground());
	value.setForeground(list.getForeground());
	value.setText(value.toString());
	return value;
    }
}
