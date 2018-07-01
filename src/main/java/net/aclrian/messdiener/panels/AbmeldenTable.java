package net.aclrian.messdiener.panels;

import java.awt.Color;
import java.awt.Component;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.Messe;
import net.aclrian.messdiener.start.AProgress;
import net.aclrian.messdiener.utils.RemoveDoppelte;

public class AbmeldenTable extends JTable {

    private static final long serialVersionUID = -1056881678315222840L;

    private AbDTM abdtm;

    public AbmeldenTable(AProgress ap) {
	ArrayList<String> sarry = new ArrayList<>();
	SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
	for (Messe messe : ap.getAda().getMesenarray()) {
	    sarry.add(df.format(messe.getDate()));
	}
	RemoveDoppelte<String> rd = new RemoveDoppelte<>();
	System.out.println("");
	rd.removeDuplicatedEntries(sarry);
	sarry.sort(new Comparator<String>() {
	    @SuppressWarnings("null")
	    @Override
	    public int compare(String o1, String o2) {
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		try {
		    Date d1 = df.parse(o1);
		    Date d2 = df.parse(o2);
		    return d1.compareTo(d2);
		} catch (ParseException e) {
		    e.printStackTrace();
		    return (Integer) null;
		}
		
	    }
	});
	sarry.add(0, "Messdiener");
	String[] s = new String[sarry.size()];
	sarry.toArray(s);
	abdtm = new AbDTM();
	setModel(abdtm);
	abdtm.setDataVector(ap.getAbmeldenTableVector(s), s);
	setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    public void speichern(AProgress ap, boolean kanndann) {
	SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
	for (int i = 0; i < abdtm.getRowCount(); i++) {
	    Messdiener m=  ap.getMessdienerFromString((String) abdtm.getValueAt(i, 0), ap.getMediarraymitMessdaten());
	    System.out.println("Messdiener: " + m.toString() + ": ");
	    for (int j = 1; j < abdtm.getColumnCount(); j++) {
		boolean b = (boolean) abdtm.getValueAt(i, j);
		if (b != kanndann) {
		    System.out.println("\tkann nicht am " + abdtm.getColumnName(j));
		    try {
			m.getMessdatenDaten().austeilen(df.parse(abdtm.getColumnName(j)));
		    } catch (ParseException e) {
			e.printStackTrace();
		    }
		}
	    }
	}
    }

    public void setForegroundInHeader(Color foreground, Color background) {
	JTableHeader header = this.getTableHeader();
	final TableCellRenderer hr = this.getTableHeader().getDefaultRenderer();
	header.setDefaultRenderer(new TableCellRenderer() {
	    private JLabel lbl;

	    @Override
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
		    boolean hasFocus, int row, int column) {
		lbl = (JLabel) hr.getTableCellRendererComponent(table, value, false, false, row, column);
		lbl.setBorder(BorderFactory.createCompoundBorder(lbl.getBorder(),
			BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		lbl.setBackground(background);
		lbl.setForeground(foreground);
		/*
		 * return (value == selectedColumn) ? hr.getTableCellRendererComponent( table,
		 * value, true, true, row, column) : hr.getTableCellRendererComponent( table,
		 * value, false, false, row, column);
		 */
		return lbl;
	    }
	});
    }

    public class AbDTM extends DefaultTableModel {
	private static final long serialVersionUID = 1L;
	private Class<?>[] c = new Class[] { Messdiener.class, Boolean.class };

	public AbDTM() {

	}

	public boolean isCellEditable(int row, int column) {
	    if (column == 0) {
		return false;
	    } else {
		return true;
	    }
	}

	public Class<?> getColumnClass(int columnIndex) {
	    try {
		return c[columnIndex];
	    } catch (IndexOutOfBoundsException e) {
		return c[1];
	    }
	}
    }

}
