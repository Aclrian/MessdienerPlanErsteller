package net.aclrian.messdiener.panels;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import net.aclrian.messdiener.differenzierung.Pfarrei;
import net.aclrian.messdiener.pictures.References;
import net.aclrian.messdiener.start.AProgress;
import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.window.APanel;
import net.aclrian.messdiener.window.WAlleMessen;

public class Start extends APanel {

	private static final long serialVersionUID = 7258230476105946056L;

	private JLabel title = new JLabel("<html><body><h1>MessdienerplanErsteller</h1></body></html>");
	private JLabel unterueberschrift = new JLabel();
	private JLabel pfarreilabel = new JLabel();
	private JButton speicherortaendern = new JButton("<html><body><h3>Speicherort<br>"+References.ae+"ndern</h3></body></html>");
	private JButton plangenerieren = new JButton("<html><body><h3>Plan generieren</h3></body></html>");
	private JButton pfaendern = new JButton("<html><body><h3>Pfarrei<br>"+References.ae+"ndern</h3></body></html>");
	private JEditorPane ep;
	private URI uri;

	
	public Start(int dfbtnheigth, int dfbtnwidht, Pfarrei pf) {
		super(dfbtnwidht, dfbtnheigth, false);
		Font font = title.getFont();
		StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
	    style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
	    style.append("font-size:" + font.getSize() + "pt;");
	    // html content
	    URI url = null;
	    uri = null;
		try {
			url = new URI("https://github.com/Aclrian/MessdienerPlanErsteller/");
			uri = new URI("https://github.com/Aclrian/MessdienerPlanErsteller/");
		} catch (URISyntaxException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		ep = new JEditorPane("text/html", "<html><body style=\"" + style + "\">" //
	            + "<a href=\""+url+"\"><img src=\""+ References.class.getResource("GitHub_Logo.png") +"\" width=\"145\" height=\"126\"/></a>" //
	            + "</body></html>");
		ep = new JEditorPane("text/html", "<html>" + 
	    		"  <head></head><body>" + 
	    		"    <a color=\"white\" href=\"https://github.com/Aclrian/MessdienerPlanErsteller/\">"//+"View Source on: "//+
	    		+ "<img src=\""+ References.class.getResource("GitHub_Logo_White.png") +"\" width=\"100\" height=\"41\" border=\"0\">" + 
	    		"</a>  </body>" + 
	    		"</html>");
	    System.out.println(ep.getText());

	    // handle link events
	    ep.addHyperlinkListener(new HyperlinkListener()
	    {
	        @Override
	        public void hyperlinkUpdate(HyperlinkEvent e)
	        {
	            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
					try {
						Desktop.getDesktop().browse(uri);
					} catch (IOException e1) {
			 			new Erroropener(e1.getMessage());
						e1.printStackTrace();
					} // roll your own link launcher or use Desktop if J6+
	        }
	    });
	    ep.setEditable(false);
		unterueberschrift.setText("<html><body><h2>version: " + AProgress.VersionID + " von Aclrian</h2></body></html>");
		pfarreilabel.setText("<html><body><h2>für <i>" + pf.getName() + "</i></h2></body></html>");
		title.setHorizontalAlignment(JLabel.CENTER);
		unterueberschrift.setHorizontalAlignment(JLabel.RIGHT);
		pfarreilabel.setHorizontalAlignment(JLabel.CENTER);
		add(title);
		add(unterueberschrift);
		add(pfarreilabel);
		add(speicherortaendern);
		add(plangenerieren);
		add(ep);
		add(pfaendern);
	}

	@Override
	protected void graphics(int width, int heigth) {
		int drei = width / 3;
		int stdhoehe = heigth / 20;
		int abstandhoch = heigth / 100;
		int abstandweit = width / 100;
		int eingenschaften = width / 5;
		int haelfte = width / 2;
		title.setBounds(abstandweit, abstandhoch, width-2*abstandweit, stdhoehe);
		pfarreilabel.setBounds(abstandweit, 2*abstandhoch+stdhoehe, width-2*abstandweit, stdhoehe);
		unterueberschrift.setBounds(abstandweit, heigth-abstandhoch-stdhoehe, width-2*abstandweit, stdhoehe);
		//title.setBorder(WAlleMessen.b);
		speicherortaendern.setBounds(abstandweit, 6*stdhoehe, drei, 3*stdhoehe);
		plangenerieren.setBounds(drei+abstandweit, 11*stdhoehe, drei-abstandweit, 3*stdhoehe);
		pfaendern .setBounds(2*drei,6*stdhoehe,drei-abstandweit, 3* stdhoehe);
		ep.setBounds((int) (abstandweit*0+2),heigth-44, 100,41);
		//ep.setBorder(WAlleMessen.b);
	   // System.out.println(ep.getText());
	    
		//pfarreilabel.setBorder(WAlleMessen.b);
		//pfarreilabel.setBorder(WAlleMessen.b);
		
	}

}
