package net.aclrian.mpe.utils;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class InactiveBackgroundTest {

    public JComponent makeUI() {
        JSpinner s0 = new JSpinner();
        s0.setPreferredSize(new Dimension(100, 20));
        s0.setEnabled(false);
        UIManager.put("FormattedTextField.inactiveBackground", Color.RED);
        JSpinner s1 = new JSpinner();
        s1.setEnabled(false);
        s1.setPreferredSize(new Dimension(100, 20));
        JSpinner s2 = new JSpinner();
        s2.setEnabled(false);
        s2.setPreferredSize(new Dimension(100, 20));
        JTextField field = ((JSpinner.NumberEditor) s2.getEditor()).getTextField();
        field.setEditable(false);
        field.setBackground(UIManager.getColor("FormattedTextField.background"));
        JSpinner s3 = new JSpinner();
        s3.setPreferredSize(new Dimension(100, 20));
        s3.setEnabled(false);
        s3.setBorder(null);
        JTextField tf = ((JSpinner.DefaultEditor) s3.getEditor()).getTextField();
        tf.setDisabledTextColor(Color.black);
        tf.setBackground(Color.white);
        tf.setBorder(new LineBorder(Color.blue, 1));
        s3.setBorder(new LineBorder(Color.red, 1));
        int n = s3.getComponentCount();
        if (n > 0) {
            Component[] components = s3.getComponents();
            String compName = "";
            for (int i = 0, l = components.length; i < l; i++) {
                if (components[i] instanceof JButton) {
                    JButton button = (JButton) components[i];
                    if (button.hasFocus()) {
                        String btnMane = button.getName();
                    }
                    button.setBorder(new LineBorder(Color.red, 1));
                    System.out.println("JButton");
                } else if (components[i] instanceof JComboBox) {
                    System.out.println("JComboBox");
                } else if (components[i] instanceof JTextField) {
                    System.out.println("JTextField");
                } else if (components[i] instanceof JFormattedTextField) {
                    System.out.println("JFormattedTextField");
                } else if (components[i] instanceof JTable) {
                    System.out.println("JTable");
                } else if (components[i] instanceof JScrollPane) {
                    System.out.println("JScrollPane");
                } else if (components[i] instanceof JPanel) {
                    JPanel panel = (JPanel) components[i];
                    panel.setBackground(Color.red);
                    panel.setBorder(null);
                    System.out.println("JPanel");
                }
            }
        }
        JPanel p = new JPanel();
        p.setBackground(Color.black);
        p.add(s0);
        p.add(s1);
        p.add(s2);
        p.add(s3);
        return p;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    public static void createAndShowGUI() {
        try {
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(laf.getName())) {
                    UIManager.setLookAndFeel(laf.getClassName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.getContentPane().add(new InactiveBackgroundTest().makeUI());
        f.setPreferredSize(new Dimension(120, 140));
        f.setLocationRelativeTo(null);
        f.pack();
        f.setVisible(true);
    }
}