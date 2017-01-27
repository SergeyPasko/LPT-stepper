package spasko.lptStepper;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Created with IntelliJ IDEA. User: spasko Date: 10.09.14 Time: 15:06 To change
 * this template use File | Settings | File Templates.
 */
class JFrame_RushneUpravlinna extends JFrame {
	private static final long serialVersionUID = 1L;
	JPanel mainPanel;
	JLabel jlabel_contact;
	int storonaElementa = 90;
	int vusotaOkna = 3 * storonaElementa + 30;
	int shirinaOkna = 4 * storonaElementa;
	Thread potokRushnogoUpravlinna;
	Napravlenie_dvigenia napravlenie_dvigenia = null;
	boolean nowPressed = false;
	Dvijenie dvijenie = null;

	public JFrame_RushneUpravlinna() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			jbInit();
			vupolnenie();
			potokRushnogoUpravlinna.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws IOException {
		mainPanel = (JPanel) this.getContentPane();
		mainPanel.setLayout(null);
		dvijenie = new Dvijenie();
		this.setSize(new Dimension(shirinaOkna, vusotaOkna));
		mainPanel.setSize(new Dimension(shirinaOkna, vusotaOkna));
		this.setTitle("Hands stepped");
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Button with images
		addButton("Icons/VverhVlevo.png", "-X,-Y", 1, 1, 0, 0);
		addButton("Icons/Vverh.png", "-Y", 1, 1, 1, 0);
		addButton("Icons/VverhVpravo.png", "+X,-Y", 1, 1, 2, 0);
		addButton("Icons/Vlevo.png", "-X", 1, 1, 0, 1);
		addButton("Icons/Vpravo.png", "+X", 1, 1, 2, 1);
		addButton("Icons/VnuzVlevo.png", "-X,+Y", 1, 1, 0, 2);
		addButton("Icons/Vnuz.png", "+Y", 1, 1, 1, 2);
		addButton("Icons/VnuzVpravo.png", "+X,+Y", 1, 1, 2, 2);
		addButton("Icons/VverhZ.png", "-Z", 1, 1.5, 3, 0);
		addButton("Icons/VnuzZ.png", "+Z", 1, 1.5, 3, 1.5);

		jlabel_contact = new JLabel();
		jlabel_contact.setSize(storonaElementa, storonaElementa);
		jlabel_contact.setLocation(storonaElementa, mainPanel.getHeight() - 30 - storonaElementa * 2);
		if (dvijenie.nowKontact())
			jlabel_contact.setIcon(new ImageIcon(ImageIO.read(new File("Icons/Kontact.png"))));
		else
			jlabel_contact.setIcon(null);
		mainPanel.add(jlabel_contact);
	}

	private JButton addButton(final String iconFileName, final String msg, double razmerX, double razmerY,
			final double pozitsiaX, final double pozitsiaY) {
		JButton jButton = null;
		try {
			File fpng = new File(iconFileName);
			if (!fpng.exists())
				fpng = new File("src/main/resources/" + iconFileName);
			Image img = ImageIO.read(fpng);

			if (img.getWidth(null) >= img.getHeight(null))
				img = img.getScaledInstance(storonaElementa, storonaElementa * img.getHeight(null) / img.getWidth(null),
						15);
			else
				img = img.getScaledInstance(storonaElementa * img.getWidth(null) / img.getHeight(null), storonaElementa,
						15);
			jButton = new JButton(new ImageIcon(img));
		} catch (IOException ignored) {
		}
		jButton.setToolTipText(msg);
		jButton.setSize((int) (razmerX * storonaElementa), (int) (razmerY * storonaElementa));
		// Motor must running only while you pressed button, on this we
		// understand what button was pressed
		jButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				switch ((int) (pozitsiaX * 10 + pozitsiaY)) {
				case 0:
					napravlenie_dvigenia = Napravlenie_dvigenia.VVERH_VLEVO;
					break;
				case 10:
					napravlenie_dvigenia = Napravlenie_dvigenia.VVERH;
					break;
				case 20:
					napravlenie_dvigenia = Napravlenie_dvigenia.VVERH_VPRAVO;
					break;
				case 1:
					napravlenie_dvigenia = Napravlenie_dvigenia.VLEVO;
					break;
				case 21:
					napravlenie_dvigenia = Napravlenie_dvigenia.VPRAVO;
					break;
				case 2:
					napravlenie_dvigenia = Napravlenie_dvigenia.VNUZ_VLEVO;
					break;
				case 12:
					napravlenie_dvigenia = Napravlenie_dvigenia.VNUZ;
					break;
				case 22:
					napravlenie_dvigenia = Napravlenie_dvigenia.VNUZ_VPRAVO;
					break;
				case 30:
					napravlenie_dvigenia = Napravlenie_dvigenia.PODNAT_INSRUMENT;
					break;
				case 31:
					napravlenie_dvigenia = Napravlenie_dvigenia.OPUSTIT_INSRUMENT;
					break;
				default:
					napravlenie_dvigenia = null;
					break;
				}
				nowPressed = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				nowPressed = false;
			}
		});

		jButton.setLocation((int) (pozitsiaX * storonaElementa),
				(int) (mainPanel.getHeight() - 30 - storonaElementa * (3 - pozitsiaY)));
		mainPanel.add(jButton);
		return jButton;
	}

	// while button pressed motor running
	private void vupolnenie() {
		potokRushnogoUpravlinna = new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (nowPressed) {
						dvijenie.dvijenieNaOdinHag(napravlenie_dvigenia, false, dvijenie.scorostPerehoda);
						if (dvijenie.nowKontact())
							try {
								jlabel_contact.setIcon(new ImageIcon(ImageIO.read(new File("Icons/Kontact.png"))));
							} catch (IOException e) {
								e.printStackTrace();
							}
						else
							jlabel_contact.setIcon(null);
					} else
						java.util.concurrent.locks.LockSupport.parkNanos(10000);
				}
			}
		});
	}
}
