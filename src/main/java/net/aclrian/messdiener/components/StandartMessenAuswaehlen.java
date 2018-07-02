package net.aclrian.messdiener.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import net.aclrian.messdiener.messe.Messverhalten;
import net.aclrian.messdiener.messe.Sonstiges;
import net.aclrian.messdiener.messe.StandartMesse;
import net.aclrian.messdiener.start.AData;

public class StandartMessenAuswaehlen extends JList<ACheckBox> {

    private static final long serialVersionUID = 662136861793559850L;
    private ArrayList<ACheckBox> cbli = new ArrayList<ACheckBox>();
    private ArrayList<StandartMesse> stdm = new ArrayList<StandartMesse>();
    private DefaultListModel<ACheckBox> dlm = new DefaultListModel<ACheckBox>();

    public StandartMessenAuswaehlen(ArrayList<StandartMesse> stdm) {
	this.setModel(dlm);
	for (int i1 = 0; i1 < stdm.size(); i1++) {
	    if (new Sonstiges().isSonstiges(stdm.get(i1))) {
		continue;
	    }
	    cbli.add(new ACheckBox(stdm.get(i1).toString()));
	    dlm.addElement(cbli.get(cbli.size() - 1));
	    this.stdm.add(stdm.get(i1));
	}
	setLayout(null);
	this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	this.addMouseListener(new MouseAdapter() {
	    public void mouseClicked(MouseEvent event) {
		@SuppressWarnings("unchecked")
		JList<ACheckBox> list = (JList<ACheckBox>) event.getSource();
		int index = list.locationToIndex(event.getPoint());
		ACheckBox item = (ACheckBox) list.getModel().getElementAt(index);
		item.setSelected(!item.isSelected());
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
	    boolean b = mv.getBestimmtes(sm, ada);
	    cbli.get(i).setSelected(b);
	   dlm.addElement(cbli.get(i));
	}
    }
}