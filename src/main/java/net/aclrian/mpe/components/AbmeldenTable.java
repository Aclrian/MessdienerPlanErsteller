package net.aclrian.mpe.components;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.start.AProgress;
import net.aclrian.mpe.utils.RemoveDoppelte;
import net.aclrian.mpe.utils.Utilities;

public class AbmeldenTable extends ATable {

	private static final long serialVersionUID = -1056881678315222840L;

	private AbDTM abdtm = new AbDTM();

	public AbmeldenTable(AProgress ap) {
		super();
		setModel(abdtm);
		// Data
		ArrayList<String> sarry = new ArrayList<>();
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		for (Messe messe : ap.getAda().getMesenarray()) {
			sarry.add(df.format(messe.getDate()));
		}
		RemoveDoppelte<String> rd = new RemoveDoppelte<>();
		rd.removeDuplicatedEntries(sarry);
		sarry.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
				try {
					Date d1 = df.parse(o1);
					Date d2 = df.parse(o2);
					return d1.compareTo(d2);
				} catch (ParseException e) {
					e.printStackTrace();
					return o1.compareTo(o2);
				}

			}
		});
		sarry.add(0, "Messdiener");
		String[] s = new String[sarry.size()];
		sarry.toArray(s);
		abdtm.setDataVector(ap.getAbmeldenTableVector(s, df), s);

		// Table
		for (int i = 1; i < this.getColumnCount(); i++) {
			this.getColumnModel().getColumn(i).setCellEditor(new DefaultCellEditor(new ACheckBox()));
			this.getColumnModel().getColumn(i).getCellEditor().addCellEditorListener(new ACellEditorListener());
			this.getColumnModel().getColumn(i).setCellRenderer(new ACheckBoxRenderer());
		}
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

	}

	public void schreiben(AProgress ap) {

	}

	public void andersrum() {
		for (int i = 0; i < abdtm.getRowCount(); i++) {
			for (int j = 1; j < abdtm.getColumnCount(); j++) {
				boolean b = (boolean) abdtm.getValueAt(i, j);
				abdtm.setValueAt(!b, i, j);
			}
		}
	}

	public void speichern(AProgress ap, boolean kanndann) {
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		for (int i = 0; i < abdtm.getRowCount(); i++) {
			Messdiener m = ap.getMessdienerFromString((String) abdtm.getValueAt(i, 0), ap.getMediarraymitMessdaten());
			for (int j = 1; j < abdtm.getColumnCount(); j++) {
				boolean b = (boolean) abdtm.getValueAt(i, j);
				if (b != kanndann) {
					try {
						m.getMessdatenDaten().austeilen(df.parse(abdtm.getColumnName(j)));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}
		Utilities.logging(this.getClass(), "speichern", "Abmeldungen wurden verarbeitet.");
	}

	public static class AbDTM extends DefaultTableModel {
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