package net.aclrian.mpe.components;

import javax.swing.DefaultCellEditor;
import javax.swing.table.DefaultTableModel;
import net.aclrian.mpe.messe.Messverhalten;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.start.AData;

public class MessverhaltenTable extends ATable {

	private static final long serialVersionUID = -6466321726979045876L;
	private static final String[] columnames = { "Standardmesse", "kann" };
	private ADTM dtm;

	public MessverhaltenTable() {
		dtm = new ADTM();
		setModel(dtm);
	}

	public void setMessverhalten(Messverhalten mv, AData ada) {
		//dtm.setDataVector(mv.getData(ada), columnames);
		getColumnModel().getColumn(0).setPreferredWidth(200);
		this.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new ACheckBox()));
		this.getColumnModel().getColumn(1).getCellEditor().addCellEditorListener(new ACellEditorListener());
		this.getColumnModel().getColumn(1).setCellRenderer(new ACheckBoxRenderer());
	}

	public Messverhalten getMessdaten(AData ada) {
		/*Messverhalten mv = new Messverhalten(ada);
		for (int i = 0; i < dtm.getRowCount(); i++) {
			String s = (String) dtm.getValueAt(i, 0);
			boolean kann = (Boolean) dtm.getValueAt(i, 1);
			for (StandartMesse sm : ada.getSMoheSonstiges()) {
				if (s.equals(sm.toBenutzerfreundlichenString())) {
					mv.editiereBestimmteMesse(ada, sm, kann);
					continue;
				}
			}
		}*/
		return null;//mv;
	}

	private class ADTM extends DefaultTableModel {
		private static final long serialVersionUID = 1L;
		private Class<?>[] c = new Class[] { String.class, Boolean.class };

		public boolean isCellEditable(int row, int column) {
			if (c[column].equals(Boolean.class)) {
				return true;
			}
			return false;
		};

		public Class<?> getColumnClass(int columnIndex) {
			return c[columnIndex];
		}

	}
}
