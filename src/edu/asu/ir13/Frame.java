package edu.asu.ir13;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLEditorKit;

public class Frame extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JButton btnVectorSimilarity;
	private JButton btnPageRank;
	private JButton btnAuthorityHub;
	private JScrollPane scrollPane;
	private JEditorPane editorPane;
	
	Map<Integer, Double> vectorSimilarity;
	StringBuilder authorityHub = new StringBuilder();
	
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Frame frame = new Frame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Frame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 469);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		btnVectorSimilarity = new JButton("Vector Similarity");
		btnVectorSimilarity.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!validation())
				{
					editorPane.setText("");
					try {
						vectorSimilarity = Ranking.getResults(textField.getText());
						vectorSimilarity = Utilities.sortByComparator(vectorSimilarity,10);
						
						Map<Integer,String> vectorSimilarityLinks = Utilities.getLinks(new ArrayList<Integer>(vectorSimilarity.keySet()));
						printLinks(vectorSimilarityLinks);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		btnPageRank = new JButton("PageRank");
		btnPageRank.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!validation())
				{
					editorPane.setText("");
					Map<Integer,Double> pageRank;
					try {
						Runtime runtime = Runtime.getRuntime();
					    // Run the garbage collector
					    runtime.gc();
					    // Calculate the used memory
					    long memory = runtime.totalMemory() - runtime.freeMemory();
						pageRank = PageRank.buildPageRank(textField.getText(), textField_1.getText());
						long memory1 = runtime.totalMemory() - runtime.freeMemory();
					    System.out.println("Used memory is bytes: " + Math.abs(memory-memory1));
						Map<Integer,String> pageRankLinks = Utilities.getLinks(new ArrayList<Integer>(pageRank.keySet()));
						printLinks(pageRankLinks);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		btnAuthorityHub = new JButton("Authority/Hub");
		btnAuthorityHub.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!validation())
				{
					editorPane.setText("");
					Map<Integer,Double>[] returnAuthorityHub = null;
					try {
						vectorSimilarity = Ranking.getResults(textField.getText());
						vectorSimilarity = Utilities.sortByComparator(vectorSimilarity,10);
						
						long start = System.nanoTime();
						returnAuthorityHub = AuthoritiesAndHubs.authoritiesAndHubs(vectorSimilarity);
						System.out.println(System.nanoTime() - start + " : nanoseconds to compute authorities and hubs");
			
						authorityHub.append("********** AUTHORITIES **********<br />");
						Map<Integer,String> authorityLinks = Utilities.getLinks(new ArrayList<Integer>(returnAuthorityHub[0].keySet()));
						printAuthorityHub(authorityLinks);
						authorityHub.append("<br /><br />********** HUBS *************<br />");
						Map<Integer,String> hubLinks = Utilities.getLinks(new ArrayList<Integer>(returnAuthorityHub[1].keySet()));
						printAuthorityHub(hubLinks);
						editorPane.setText(authorityHub.toString());
						authorityHub = new StringBuilder();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		textField = new JTextField();
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Oogle");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Calibri", Font.ITALIC, 30));
		
		scrollPane = new JScrollPane();
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setText("0.4");
		
		JLabel lblWValue = new JLabel("W value");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(249)
							.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
							.addGap(173))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(103)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(15)
									.addComponent(btnVectorSimilarity, GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
									.addGap(36)
									.addComponent(btnAuthorityHub, GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
									.addGap(42)
									.addComponent(btnPageRank, GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
									.addGap(29))
								.addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE))
							.addGap(40))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(73)
							.addComponent(textField, GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
							.addGap(9)))
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(27)
							.addComponent(lblWValue, GroupLayout.PREFERRED_SIZE, 62, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(9)
							.addComponent(textField_1, GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(55)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
							.addGap(11))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblWValue)
							.addPreferredGap(ComponentPlacement.RELATED)))
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(textField_1, GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnPageRank)
						.addComponent(btnVectorSimilarity)
						.addComponent(btnAuthorityHub))
					.addGap(37)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE))
		);
		
		editorPane = new JEditorPane();
		editorPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent event) {
				HyperlinkEvent.EventType eventType = event.getEventType();
		        if (eventType == HyperlinkEvent.EventType.ACTIVATED) {
		        	String url = event.getDescription();
		        	if (Desktop.isDesktopSupported()) {
		                Desktop desktop = Desktop.getDesktop();
		                try {
		                    URI uri = new URI(url);
		                    desktop.browse(uri);
		                } catch (IOException ex) {
		                    ex.printStackTrace();
		                } catch (URISyntaxException ex) {
		                    ex.printStackTrace();
		                }
		        	}
		        }
			}
		});
		editorPane.setEditorKit(new HTMLEditorKit());
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		scrollPane.setViewportView(editorPane);
		contentPane.setLayout(gl_contentPane);
	}
	
	/**
	 * Printing the results on to the text area
	 * @param map
	 */
	/*private void printResults(Map<Integer, Double> map)
	{
		int count = 0;
		for (Map.Entry<Integer,Double> entry : map.entrySet()) 
		{
			count += 1;
			try {
				appendString("Document ID : " + entry.getKey() + "\t" + " Value : " + entry.getValue() + "\n");
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		System.out.println("Total Number of results : " + count);
	}*/
	
	/**
	 * Printing the document as links on to the text area for authority/hub
	 * @param map
	 */
	private void printAuthorityHub(Map<Integer, String> map)
	{
		String url = null;
		for (Map.Entry<Integer,String> entry : map.entrySet()) 
		{
			url = entry.getValue();
			url = url.substring(69, url.length());
			url = url.replace("%25%25", "/");
			authorityHub.append(entry.getKey() + "&emsp" +"<a href='" + entry.getValue() +"'>" + url + "</a> <br/>");
		}
	}
	
	/**
	 * Printing the document as links on to the text area
	 * @param map
	 */
	private void printLinks(Map<Integer, String> map)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<table>");
		String url = null;
		for (Map.Entry<Integer,String> entry : map.entrySet()) 
		{
			url = entry.getValue();
			url = url.substring(69, url.length());
			url = url.replace("%25%25", "/");
			sb.append("<tr>");
			sb.append("<td>" + entry.getKey() + "</td>" + "<td>" +"<a href='" + entry.getValue() +"'>" + url + "</a> </td>");
			sb.append("</tr>");
//			sb.append("<br />");
		}
		sb.append("</table>");
		editorPane.setText(sb.toString());
	}
	
	/**
	 * Appends string in JEditorPane
	 */
	private void appendString(String str) throws BadLocationException
	{
	     StyledDocument document = (StyledDocument) editorPane.getDocument();
	     document.insertString(document.getLength(), str, null);
	                                                    // ^ or your style attribute  
	 }
	
	/**
	 * Validation
	 */
	private boolean validation()
	{
		if (textField.getText().equals(""))
		{
//			editorPane.setText("<a href='www.asu.edu/admissions/contact/index.html'>938</a>.");
			editorPane.setText("Cannot search empty string !!");
			return true;
		}
		
		return false;
	}
}
