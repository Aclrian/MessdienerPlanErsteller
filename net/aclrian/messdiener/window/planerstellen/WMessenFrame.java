package net.aclrian.messdiener.window.planerstellen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import net.aclrian.messdiener.pictures.References;
import net.aclrian.messdiener.start.AProgress;
import net.aclrian.messdiener.utils.Utilities;

public class WMessenFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6698847508119750726L;
	private JPanel contentPane;
	private JDateChooser dateChooser;
	private JDateChooser dc = new JDateChooser();

	
	/**
	 * Create the frame.
	 */
	public WMessenFrame(AProgress ap) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("W"+References.ae+"hle den Zeitraum f"+References.ue+"r den neuen Plan");
		setIconImage(References.getIcon());
		setBounds(Utilities.setFrameMittig(433, 192));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		Date d = Calendar.getInstance().getTime();
		JTextFieldDateEditor jtfde1 = new JTextFieldDateEditor();
		 dateChooser = new JDateChooser(new JCalendar(d,Locale.GERMANY), d, "dd.MM.yyyy", jtfde1);
		 dc = new JDateChooser();
	
		
		if (dateChooser.getDateEditor() instanceof JTextFieldDateEditor) {
			System.out.println("sdsd");
			((JTextFieldDateEditor) dateChooser.getDateEditor()).addFocusListener(new FocusListener() {
				
				@Override
				public void focusLost(FocusEvent e) {
				Calendar c = Calendar.getInstance();
				c.setTime(dateChooser.getDate());
				c.add(Calendar.MONTH, 1);
				dc.setDate(c.getTime());
				}
				
				@Override
				public void focusGained(FocusEvent e) {}
			});
		}			
		
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
				ap.berechen();
			}
		});
		btnWeiter.setBounds(148, 107, 117, 25);
		contentPane.add(btnWeiter);
	//	dateChooser.getDateEditor().addPropertyChangeListener(
				/*
			    new PropertyChangeListener() {
			        @Override
			        public void propertyChange(PropertyChangeEvent e) {
			            System.out.println(e.getPropertyName() + e.getNewValue());
			        	if ("date".equals(e.getPropertyName())) {
			                System.out.println(e.getPropertyName()
			                    + ": " + (Date) e.getNewValue());
			            }
			        }
			    });*/
	}

	public Date[] berechnen() {
		Date[] d = new Date[2];
		
		d[0] = dateChooser.getDate();
		d[1] = dc.getDate();
		return d;		
	}
}
