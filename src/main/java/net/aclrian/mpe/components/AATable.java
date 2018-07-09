package net.aclrian.mpe.components;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

public class AATable extends JTable {

    private static final long serialVersionUID = -1972759179020594336L;

    public AATable(DefaultTableModel dtm) {
	super(dtm);
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
		return lbl;
	    }
	});

    }
}