package net.aclrian.messdiener.window.planerstellen;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import com.toedter.calendar.JDateChooser;

import net.aclrian.messdiener.utils.References;
import net.aclrian.messdiener.utils.Utilities;
import net.aclrian.messdiener.window.WMainFrame;

import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.Date;
import java.awt.event.ActionEvent;

public class WWMessenFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6698847508119750726L;
	private JPanel contentPane;
	private JDateChooser dateChooser = new JDateChooser();
	private JDateChooser dc = new JDateChooser();

	
	/**
	 * Create the frame.
	 */
	public WWMessenFrame(WMainFrame wmf) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("W"+References.ae+"hle den Zeitraum f"+References.ue+"r den neuen Plan");
		setIconImage(WMainFrame.getIcon(new References()));
		setBounds(Utilities.setFrameMittig(433, 192));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		dc.setSize(100, 30);
		dc.setLocation(192, 54);
		contentPane.add(dc);
		
		
		dateChooser.setBounds(192, 12, 100, 30);
		contentPane.add(dateChooser);
		
		JLabel lblStartPlan = new JLabel("Start Plan:");
		lblStartPlan.setBounds(98, 12, 86, 30);
		contentPane.add(lblStartPlan);
		
		JLabel lblEndePlan = new JLabel("Ende Plan:");
		lblEndePlan.setBounds(98, 54, 76, 30);
		contentPane.add(lblEndePlan);
		
		JButton btnWeiter = new JButton("Weiter");
		btnWeiter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				wmf.berechen();
			}
		});
		btnWeiter.setBounds(148, 107, 117, 25);
		contentPane.add(btnWeiter);
		
	}

	public Date[] berechnen(WMainFrame wmf) {
		Date[] d = new Date[2];
		
		d[0] = dateChooser.getDate();
		d[1] = dc.getDate();
		return d;		
	}
}
