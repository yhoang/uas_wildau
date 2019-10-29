import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.event.*;
import java.io.File;

public class AlignGUI extends JFrame {

	public AlignGUI() {
		initComponents();
	}

	private void initComponents() {
		
		// Initialisierung der Komponenten
		inputPanel = new JPanel();
		seq1Label = new JLabel();
		seq1ScrollPane = new JScrollPane();
		seq1TextArea = new JTextArea();
		seq2Label = new JLabel();
		seq2ScrollPane = new JScrollPane();
		seq2Textarea = new JTextArea();
		openSeq1Button = new JButton();
		openSeq2Button = new JButton();
		selectMatrixCombo = new JComboBox();
		selectMatrixLabel = new JLabel();
		startButton = new JButton();
		outputTabbedPane = new JTabbedPane();
		graphicalOutputPane = new JPanel();
		textOutputPane = new JPanel();
		menuBar = new JMenuBar();
		fileMenu = new JMenu();
		editMenu = new JMenu();
		helpMenu = new JMenu();
		newMenuItem = new JMenuItem();
		exitMenuItem = new JMenuItem();
		
		// Hauptpanel
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Alleiner");
		setPreferredSize(new Dimension(1024, 768));
		
		// Menu
		fileMenu.setText("File");
		fileMenu.setMnemonic('F');
		menuBar.add(fileMenu);

		editMenu.setText("Edit");
		editMenu.setMnemonic('E');
		menuBar.add(editMenu);

		helpMenu.setText("Help");
		helpMenu.setMnemonic('H');
		menuBar.add(helpMenu);
		newMenuItem.setText("New Alignment");
		newMenuItem.setMnemonic('N');
		newMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				outputTabbedPane.setVisible(false);
				seq1TextArea.setText("");
				seq2Textarea.setText("");
			}
		});
		exitMenuItem.setText("Exit");
		exitMenuItem.setMnemonic('X');
		exitMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		fileMenu.add(newMenuItem);
		fileMenu.add(exitMenuItem);
		setJMenuBar(menuBar);
		Laf();

		makeAboutUs();

		// Inputpanel definieren
		seq1Label.setText("1st Sequence");

		seq1TextArea.setColumns(40);
		seq1TextArea.setRows(5);
		seq1ScrollPane.setViewportView(seq1TextArea);

		seq2Label.setText("2nd Sequence");

		seq2Textarea.setColumns(40);
		seq2Textarea.setRows(5);
		seq2ScrollPane.setViewportView(seq2Textarea);

		openSeq1Button.setText("Open File");
		openSeq1Button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				openSeq1ButtonActionPerformed(event);
			}
		});

		openSeq2Button.setText("Open File");
		openSeq2Button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				openSeq2ButtonActionPerformed(event);
			}
		});

		selectMatrixCombo.setModel(new DefaultComboBoxModel(new String[] {
				"PAM30", "BLOSUM62", "EDNAMAT", "EDNAFULL" }));
		selectMatrixCombo
				.setToolTipText("<html><body><b>PAM30</b> and <b>BLOSUM62</b> for proteins <br><b>EDNAMAT</b> and <b>EDNAFULL</b> for DNA</body></html>");

		selectMatrixLabel.setText("Select Scoring Matrix");

		startButton.setText("Start Alignment");
		startButton.setMnemonic('S');
		startButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				startButtonActionPerformed(event);
			}
		});
		// Input Layout
		GroupLayout inputPanelLayout = new GroupLayout(inputPanel);
		inputPanel.setLayout(inputPanelLayout);
		inputPanelLayout
				.setHorizontalGroup(inputPanelLayout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								inputPanelLayout
										.createSequentialGroup()
										.addGroup(
												inputPanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addGroup(
																inputPanelLayout
																		.createSequentialGroup()
																		.addGap(10,
																				10,
																				10)
																		.addComponent(
																				seq1Label)
																		.addPreferredGap(
																				LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				seq1ScrollPane,
																				GroupLayout.PREFERRED_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.PREFERRED_SIZE)
																		.addGap(10,
																				10,
																				10)
																		.addComponent(
																				openSeq1Button))
														.addGroup(
																inputPanelLayout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addComponent(
																				seq2Label)
																		.addGap(5,
																				5,
																				5)
																		.addComponent(
																				seq2ScrollPane,
																				GroupLayout.PREFERRED_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.PREFERRED_SIZE)
																		.addGap(10,
																				10,
																				10)
																		.addComponent(
																				openSeq2Button)))
										.addGap(10, 10, 10)
										.addGroup(
												inputPanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING,
																false)
														.addGroup(
																inputPanelLayout
																		.createSequentialGroup()
																		.addComponent(
																				selectMatrixLabel)
																		.addGap(10,
																				10,
																				10)
																		.addComponent(
																				selectMatrixCombo,
																				GroupLayout.PREFERRED_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.PREFERRED_SIZE))
														.addComponent(
																startButton,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addContainerGap(10, Short.MAX_VALUE)));
		inputPanelLayout
				.setVerticalGroup(inputPanelLayout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								inputPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												inputPanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addComponent(
																seq1ScrollPane,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(seq1Label)
														.addGroup(
																inputPanelLayout
																		.createParallelGroup(
																				GroupLayout.Alignment.BASELINE)
																		.addComponent(
																				openSeq1Button)
																		.addComponent(
																				selectMatrixCombo,
																				GroupLayout.PREFERRED_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.PREFERRED_SIZE)
																		.addComponent(
																				selectMatrixLabel)))
										.addGap(10, 10, 10)
										.addGroup(
												inputPanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING,
																false)
														.addComponent(seq2Label)
														.addComponent(
																seq2ScrollPane)
														.addComponent(
																openSeq2Button)
														.addComponent(
																startButton,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addContainerGap(100, Short.MAX_VALUE)));
		
		// Outputpanel (Tabbed)
		outputTabbedPane.setVisible(false);
		outputTabbedPane.addTab("Graphical View", graphicalOutputPane);

		outputTabbedPane.addTab("Text View", textOutputPane);

		drw = new Canvas();
		TranslateHandler translater = new TranslateHandler();
		drw.addMouseListener(translater);
		drw.addMouseMotionListener(translater);
		drw.addMouseWheelListener(new ScaleHandler());

		outputTabbedPane.setPreferredSize(new Dimension(800, 500));
		textOutputScrollPane = new JScrollPane();
		textOutputTextArea = new JTextArea();
		textOutputScrollPane.setViewportView(textOutputTextArea);
		textOutputTextArea.setFont(new Font("Monospaced", Font.PLAIN,
				12));
		textOutputTextArea.setText(Aligner.getTextOut());
		textOutputTextArea.setEditable(false);
		textOutputPane.add(textOutputScrollPane);
		
		// Outputpanel Layout
		GroupLayout textOutputLayout = new GroupLayout(textOutputPane);
		textOutputPane.setLayout(textOutputLayout);
		textOutputLayout.setHorizontalGroup(textOutputLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(textOutputScrollPane));
		textOutputLayout.setVerticalGroup(textOutputLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(textOutputScrollPane));
		GroupLayout graphicalOutputLayout = new GroupLayout(
				graphicalOutputPane);
		graphicalOutputPane.setLayout(graphicalOutputLayout);

		graphicalOutputLayout.setHorizontalGroup(graphicalOutputLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(drw));
		graphicalOutputLayout.setVerticalGroup(graphicalOutputLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(drw));

		graphicalOutputPane.setBorder(BorderFactory
				.createTitledBorder("Alignment"));
		graphicalOutputPane.add(drw);
		
		//Mainpanel Layout
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.LEADING)
										.addComponent(inputPanel,
												GroupLayout.DEFAULT_SIZE,
												GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addComponent(outputTabbedPane))
						.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(inputPanel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(outputTabbedPane,
								GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
						.addContainerGap()));

		pack();
	}
	// Open File Button Sequence 1
	private void openSeq1ButtonActionPerformed(ActionEvent event) {
		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		int result = chooser.showOpenDialog(AlignGUI.this);
		if (result == JFileChooser.APPROVE_OPTION) {
			String filename = chooser.getSelectedFile().getPath();
			String sequence = Parser.readFile(filename);
			seq1TextArea.setText(sequence);
		}
	}
	// Open File Button Sequence 2
	private void openSeq2ButtonActionPerformed(ActionEvent event) {
		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		result = chooser.showOpenDialog(AlignGUI.this);
		if (result == JFileChooser.APPROVE_OPTION) {
			String filename = chooser.getSelectedFile().getPath();
			String sequence = Parser.readFile(filename);
			seq2Textarea.setText(sequence);
		}
	}
	// Start Alignmentbutton Action (Parsen, Alignment, Outputpanel sichtbar machen)
	private void startButtonActionPerformed(ActionEvent event) {
		String matrixName = (String) selectMatrixCombo.getSelectedItem();
		scoringMatrix = ScoringMatrix.getMatrix(matrixName);
		String Nucs = ScoringMatrix.getNucs(matrixName);
		String seq1 = seq1TextArea.getText();
		String seq2 = seq2Textarea.getText();
		if ((seq1.length() == 0) || (seq2.length() == 0)) {
			JOptionPane.showMessageDialog(AlignGUI.this,
					"Sequencefields must not be blank!", "Error",
					JOptionPane.ERROR_MESSAGE);
		} else {
			seq1 = Parser.simpleParse(seq1);
			seq2 = Parser.simpleParse(seq2);
			if (seq1.equals(seq2)) {
				JOptionPane.showMessageDialog(AlignGUI.this,
						"Sequence 1 equals Sequence 2!", "Error",
						JOptionPane.ERROR_MESSAGE);
			} else {
				outputTabbedPane.setVisible(true);
				Alignment = new Aligner(seq1, seq2, scoringMatrix, Nucs);
				textOutputTextArea.setText(Aligner.getTextOut());

			}	
		}
	}
	// About Us Dialog erstellen & ins HelpMenu
	private void makeAboutUs() {
		final JPanel aboutUsPane = new JPanel();
		BufferedImage myPicture = null;
		try {
			myPicture = ImageIO.read(new File("logo.png"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		aboutUsPane.add(new JLabel(new ImageIcon(myPicture)));
		aboutUsPane
				.add(new JLabel(
						"<html><body><h1>Wahlpflichtfach Java</h1><br><h2>Projektarbeit Alleiner 1.2</h2><br>J&ouml;rn Dietrich<br>Yen Hoang<br>Christian Rockmann<br>Vanessa Sch&ouml;ppler<br><br> WS 2011/12 TH Wildau</body></html>"));
		final Object[] options = { "Ok" };
		aboutUsMenuItem = new JMenuItem("About us",'A');
		helpMenu.add(aboutUsMenuItem);
		aboutUsMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showOptionDialog(null, aboutUsPane, "About Us",
						JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null,
						options, null);
			}
		});

	}
	// Look & Feel Auswahlmenu erstellen
	private void Laf() {
		plafGroup = new ButtonGroup();
		plafMenu = new JMenu("Look & Feel");
		UIManager.LookAndFeelInfo[] infos = UIManager
				.getInstalledLookAndFeels();
		for (UIManager.LookAndFeelInfo info : infos) {
			makeLafItems(info.getName(), info.getClassName());
		}
		editMenu.add(plafMenu);

	}
	// installierte Look & Feels-Items dem Menu hinzufügen
	private void makeLafItems(String name, final String lafName) {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(name);
		plafGroup.add(item);
		plafMenu.add(item);
		if (name.equals(UIManager.getLookAndFeel().getName())) {
			item.setSelected(true);
		}
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					UIManager.setLookAndFeel(lafName);
					SwingUtilities.updateComponentTreeUI(AlignGUI.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public static void main(String args[]) {
		// Nimbus als L&F-Präferenz
		for (UIManager.LookAndFeelInfo info : UIManager
				.getInstalledLookAndFeels()) {
			if ("Nimbus".equals(info.getName())) {
				try {
					UIManager.setLookAndFeel(info.getClassName());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		// GUI aufrufen
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				new AlignGUI().setVisible(true);
			}
		});
	}
	// Variablen
	private Image aboutUsImage;
	private JMenu editMenu;
	private JMenu fileMenu;
	private JMenu helpMenu;
	private JMenu plafMenu;
	private JPanel inputPanel;
	private ButtonGroup plafGroup;
	private JMenuItem newMenuItem;
	private JMenuItem exitMenuItem;
	private JMenuItem aboutUsMenuItem;
	private JMenuBar menuBar;
	private JButton openSeq1Button;
	private JButton openSeq2Button;
	private JTabbedPane outputTabbedPane;
	private JPanel graphicalOutputPane;
	private JPanel textOutputPane;
	private JComboBox selectMatrixCombo;
	private JLabel selectMatrixLabel;
	private JLabel seq1Label;
	private JScrollPane seq1ScrollPane;
	private JTextArea seq1TextArea;
	private JLabel seq2Label;
	private JScrollPane seq2ScrollPane;
	private JTextArea seq2Textarea;
	private JButton startButton;
	private JFileChooser chooser;
	private Aligner Alignment;
	private int result;
	private int[][] scoringMatrix;
	private JScrollPane textOutputScrollPane;
	private JTextArea textOutputTextArea;

	private static int size = 780;
	private static int zoom = 6;

	public static int getFrameSize() {
		return size;
	}

	public static int getZoom() {
		return zoom;
	}

	private static Canvas drw;
	// Canvas für Grafikoutput
	private static class Canvas extends JComponent {
		private double translateX;
		private double translateY;
		private double scale;

		Canvas() {
			translateX = 0;
			translateY = 0;
			scale = 1;
			setOpaque(true);
			setDoubleBuffered(true);
		}

		@Override
		public void paint(Graphics g) {

			AffineTransform tx = new AffineTransform();
			tx.translate(translateX, translateY);
			tx.scale(scale, 1);
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, binarySequence[0].length() * 100, getHeight());
			double xwidth = getWidth();
			g2.setTransform(tx);
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, (int) xwidth, 20);
			int y = -20;
			for (int a = 0; a < binarySequence.length; a++) {
				y += 50;
				for (int i = 0; i < binarySequence[a].length(); i++) {
					if (binarySequence[a].charAt(i) == '1') {
						g2.setColor(Color.green);

						g2.fillRect(
								i
										* (int) Math.ceil(xwidth
												/ binarySequence[a].length()),
								y,
								(int) Math.ceil(xwidth
										/ binarySequence[a].length()), 20);
						g2.setColor(Color.BLACK);
					}
				}

			}
			repaint();
		}

		private String[] binarySequence = Aligner.get_binary();
	}
	// Grafik scrollen
	private static class TranslateHandler implements MouseListener,
			MouseMotionListener {
		private int lastOffsetX;
		
		public void mousePressed(MouseEvent e) {
			lastOffsetX = e.getX();
		}

		public void mouseDragged(MouseEvent e) {
			int newX = e.getX() - lastOffsetX;
			lastOffsetX += newX;
			drw.translateX += newX;
			drw.repaint();
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseMoved(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}
	}
	// Zoom
	private static class ScaleHandler implements MouseWheelListener {
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
				drw.scale += (.1 * e.getWheelRotation());
				drw.scale = Math.max(0, drw.scale);
				drw.repaint();
			}
		}
	}
}