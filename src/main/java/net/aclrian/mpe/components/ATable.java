package net.aclrian.mpe.components;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import net.aclrian.mpe.messe.Messverhalten;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.start.AData;

public class ATable extends JTable {

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
		this.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new ACheckBox()));
		this.getColumnModel().getColumn(1).getCellEditor().addCellEditorListener(new CellEditorListener() {
			
			@Override
			public void editingStopped(ChangeEvent e) {
				if(e.getSource() instanceof DefaultCellEditor && ((DefaultCellEditor)e.getSource()).getComponent() instanceof ACheckBox) {
					((ACheckBox)((DefaultCellEditor)e.getSource()).getComponent()).setHorizontalAlignment(SwingConstants.CENTER);
				}				
			}
			
			@Override
			public void editingCanceled(ChangeEvent e) {
				if(e.getSource() instanceof DefaultCellEditor && ((DefaultCellEditor)e.getSource()).getComponent() instanceof ACheckBox) {
					((ACheckBox)((DefaultCellEditor)e.getSource()).getComponent()).setHorizontalAlignment(SwingConstants.CENTER);
				}
			}
		});
		this.getColumnModel().getColumn(1).setCellRenderer(new ACheckBoxRenderer());
	}

	public Messverhalten getMessdaten(AData ada) {
		Messverhalten mv = new Messverhalten(ada);
		for (int i = 0; i < dtm.getRowCount(); i++) {
			String s = (String) dtm.getValueAt(i, 0);
			boolean kann = (Boolean) dtm.getValueAt(i, 1);
			for (StandartMesse sm : ada.getSMoheSonstiges()) {
				if (s.equals(sm.toBenutzerfreundlichenString())) {
					mv.editiereBestimmteMesse(ada, sm, kann);
					continue;
				}
			}
		}
		return mv;
	}

	public void setForegroundInHeader(Color foreground, Color background, Color grid, Color sForeground,
			Color sbackground) {
		JTableHeader header = this.getTableHeader();
		final TableCellRenderer hr = this.getTableHeader().getDefaultRenderer();
		header.setDefaultRenderer(new TableCellRenderer() {
			private JLabel lbl;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				lbl = (JLabel) hr.getTableCellRendererComponent(table, value, false, false, row, column);
				// lbl.setBorder(BorderFactory.createCompoundBorder(lbl.getBorder(),
				// BorderFactory.createEmptyBorder(1, 1, 1, 1)));
				lbl.setHorizontalAlignment(SwingConstants.CENTER);
				lbl.setBackground(background);
				lbl.setForeground(foreground);
				Border b = BorderFactory.createMatteBorder(1,column==0?1:0,1,1/*0, column==0 ? 0 : 1, 1, column==1 ? 0 : 1*/, grid);
				lbl.setBorder(b);
				return lbl;
			}
		});
		this.setGridColor(grid);
		this.setSelectionBackground(sbackground);
		this.setSelectionForeground(sForeground);
	}

	public class ACheckBoxRenderer extends ACheckBox implements TableCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		ACheckBoxRenderer() {
			setText("");
			setHorizontalAlignment(JLabel.CENTER);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if (isSelected) {
				setBackground(table.getSelectionBackground());
				setForeground(table.getSelectionForeground());
			} else {
				setBackground(table.getBackground());
				setForeground(table.getForeground());
			}
			// setSel
			setSelected((value != null && ((Boolean) value).booleanValue()));
			return this;
		}
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
