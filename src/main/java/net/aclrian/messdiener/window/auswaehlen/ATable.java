package net.aclrian.messdiener.window.auswaehlen;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import net.aclrian.messdiener.deafault.Messverhalten;
import net.aclrian.messdiener.deafault.StandartMesse;
import net.aclrian.messdiener.start.AData;

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
	
	public void setForegroundInHeader(Color foreground, Color background) {
		JTableHeader header = this.getTableHeader();
		final TableCellRenderer hr = this.getTableHeader().getDefaultRenderer();
        header.setDefaultRenderer(new TableCellRenderer() {
            private JLabel lbl;
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, 
                    int row, int column) {
                    lbl = (JLabel) hr.getTableCellRendererComponent(table, value, 
                            false, false, row, column);
                    lbl.setBorder(BorderFactory.createCompoundBorder(
                            lbl.getBorder(), 
                            BorderFactory.createEmptyBorder(1, 1, 1, 1)));
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                    lbl.setBackground(background);
                    lbl.setForeground(foreground);
                /*return (value == selectedColumn) ? hr.getTableCellRendererComponent(
                 table, value, true, true, row, column) : hr.getTableCellRendererComponent(
                 table, value, false, false, row, column);*/
                return lbl;
            }
        });
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

