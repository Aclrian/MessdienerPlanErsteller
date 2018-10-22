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
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class ATable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1242219054398849006L;
	public ATable() {
		super();
	}

	public ATable(TableModel dm) {
		super(dm);
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
	public class ACellEditorListener implements CellEditorListener {
		
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

}
