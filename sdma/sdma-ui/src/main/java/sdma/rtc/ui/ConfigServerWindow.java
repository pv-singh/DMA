package sdma.rtc.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;



public class ConfigServerWindow extends JFrame implements ActionListener {

	// config name for cluster
	private JLabel clusterConfigNameLb = null;
	private JTextField clusterConfigNameTf = null;

	// configuration for cluster socket parameters
	private JLabel clusterHostLb = null;
	private JTextField clusterHostTf = null;
	private JLabel clusterPortLb = null;
	private JTextField clusterPortTf = null;

	private JButton saveConfigBt = null;

	public ConfigServerWindow() {

		super("Server Cluster Configuration");

		initUI();

		this.setBounds((int) (getScreenSize().getWidth() - 400) / 2,
				(int) (getScreenSize().getHeight() - 200) / 2, 400, 200);
		this.setVisible(true);
	}

	private void initUI() {

		Container con = this.getContentPane();
		con.setLayout(null);

		clusterConfigNameLb = new JLabel("Config Name");
		clusterConfigNameTf = new JTextField();

		clusterConfigNameLb.setBounds(10, 10, 130, 25);
		clusterConfigNameTf.setBounds(160, 10, 120, 25);

		clusterHostLb = new JLabel("Host");
		clusterHostTf = new JTextField();

		clusterHostLb.setBounds(10, 45, 130, 25);
		clusterHostTf.setBounds(160, 45, 200, 25);

		clusterPortLb = new JLabel("Port");
		clusterPortTf = new JTextField();

		clusterPortLb.setBounds(10, 80, 130, 25);
		clusterPortTf.setBounds(160, 80, 120, 25);

		saveConfigBt = new JButton("Save");
		saveConfigBt.setBounds(160, 120, 80, 25);
		saveConfigBt.addActionListener(this);

		con.add(clusterConfigNameLb);
		con.add(clusterConfigNameTf);

		//con.add(clusterLocationLb);
		//con.add(localServerRb);
		//con.add(cloudServerRb);

		con.add(clusterHostLb);
		con.add(clusterHostTf);
		con.add(clusterPortLb);
		con.add(clusterPortTf);
		con.add(saveConfigBt);

	}

	private Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}

	public static void main(String[] args) {
		new ConfigServerWindow();
	}

	private void updaServerConfigs() {
		Properties prop = new Properties();
		File configFile = new File("server-location.properties");
		OutputStream output = null;
		if (configFile.exists()) {
			
			
			InputStream in;
			try {
				in = new FileInputStream("server-location.properties");
				prop.load(in);
				in.close();
				if(prop.get(clusterConfigNameTf.getText())!=null){
					prop.setProperty(clusterConfigNameTf.getText(),clusterHostTf.getText() + " "
									+ clusterPortTf.getText());
				}else{
					prop.put(clusterConfigNameTf.getText(),clusterHostTf.getText() + " "
									+ clusterPortTf.getText());
				}
				output = new FileOutputStream("server-location.properties");
				prop.store(output, null);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				if (output != null) {
					try {
						output.close();
						this.dispose();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		} else {

			try {
				output = new FileOutputStream("server-location.properties");
				prop.setProperty(clusterConfigNameTf.getText(),clusterHostTf.getText() + " "
								+ clusterPortTf.getText());
				prop.store(output, null);
			} catch (IOException io) {
				io.printStackTrace();
			} finally {
				if (output != null) {
					try {
						output.close();
						this.dispose();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		updaServerConfigs();
	}

}
