package net.aclrian.messdiener.window.auswaehlen;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.aclrian.messdiener.deafault.Messverhalten;
import net.aclrian.messdiener.deafault.StandartMesse;
import net.aclrian.messdiener.newy.progress.AData;

public class ATable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6466321726979045876L;
	private static final String[] columnames = { "Standardmesse", "kann" };
	private ADTM dtm;

	public ATable() {
		dtm = new ADTM();	
		setModel(dtm);
	}
	public void setMessverhalten(Messverhalten mv, AData ada) {
		dtm.setDataVector(mv.getData(ada), columnames);
		getColumnModel().getColumn(0).setPreferredWidth(200);
	}
	public Messverhalten getMessdaten(AData ada){
		Messverhalten mv = new Messverhalten(ada);
		for (int i = 0; i < dtm.getRowCount(); i++) {
			StandartMesse s = (StandartMesse) dtm.getValueAt(i, 0);
			boolean kann = (Boolean) dtm.getValueAt(i, 1);
			for (StandartMesse sm : ada.getSMoheSonstiges()) {
				if (s.toString().equals(sm.toString())) {
					mv.editiereBestimmteMesse(ada, sm, kann);
					continue;
				}
			}
		}
		return mv;
	}

	
	private class ADTM extends DefaultTableModel{
		private static final long serialVersionUID = 1L;
		private Class<?>[] c = new Class[]{
			StandartMesse.class, Boolean.class
		};
		public boolean isCellEditable(int row, int column) {
			if(c[column].equals(Boolean.class)){
				return true;
			}
			return false;
		};
		public Class<?> getColumnClass(int columnIndex) {
			return c[columnIndex];
		}
		
	}
}

