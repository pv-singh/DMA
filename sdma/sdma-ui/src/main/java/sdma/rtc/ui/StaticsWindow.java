package sdma.rtc.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class StaticsWindow extends JFrame implements ActionListener{

	private JLabel totalPacksLb = null;
	private JLabel totalClassesLb = null;
	private JLabel totalMethodsLb = null;
	private JLabel totalMessageLb = null;
	private JLabel avgLatencyLb = null;
	private JLabel responseTimeLb = null;
	private JLabel executionTimeLb = null;
	private JButton exportBt = null;

	private Integer totalPacksUsed = null;
	private Integer totalClassesUsed = null;
	private Integer totalMethodsCall = null;
	private Integer totalMessages = null;
	private Long avgLatency = null;
	private Long responseTime = null;
	private Integer executionTime = null;
	private Map<String, Integer> clsEC = null;
	private Map<String, Integer> mtlEC = null;
	private Map<String, Integer> mslEC = null;
	private Map<String, Integer> clsIC = null;
	private Map<String, Integer> mtlIC = null;
	private Map<String, Integer> mslIC = null;
	// screen dimensions
	private final static int screenWidth = 800;
	private final static int screenHeight = 450;

	public StaticsWindow(Integer totalPacksUsed, Integer totalClassesUsed,
			Integer totalMethodsCall, Integer totalMessages, Long avgLatency,
			Long responseTime, Integer executionTime,
			Map<String, Integer> clsEC, Map<String, Integer> mtlEC,
			Map<String, Integer> mslEC, Map<String, Integer> clsIC,
			Map<String, Integer> mtlIC, Map<String, Integer> mslIC) {
		this.totalPacksUsed = totalPacksUsed;
		this.totalClassesUsed = totalClassesUsed;
		this.totalMethodsCall = totalMethodsCall;
		this.totalMessages = totalMessages;
		this.avgLatency = avgLatency;
		this.responseTime = responseTime;
		this.executionTime = executionTime;
		this.clsEC = clsEC;
		this.mtlEC = mtlEC;
		this.mslEC = mslEC;
		this.clsIC = clsIC;
		this.mtlIC = mtlIC;
		this.mslIC = mslIC;
		initUI();
	}

	private void initUI() {

		Container con = this.getContentPane();
		con.setLayout(null);

		totalPacksLb = new JLabel("Total Packages Used : " + totalPacksUsed);
		totalClassesLb = new JLabel("Total Classes Used : " + totalClassesUsed);
		totalMethodsLb = new JLabel("Total Methods Called : "
				+ totalMethodsCall);
		totalMessageLb = new JLabel("Total Messages Exch : " + totalMessages);
		avgLatencyLb = new JLabel("Average Latency : " + avgLatency);
		responseTimeLb = new JLabel("Respnse Time : " + responseTime);
		executionTimeLb = new JLabel("Execution Time : " + executionTime);
		exportBt = new JButton("Export to Excel");

		totalPacksLb.setBounds(50, 25, 400, 25);
		totalClassesLb.setBounds(50, 50, 400, 25);
		totalMethodsLb.setBounds(50, 75, 400, 25);
		totalMessageLb.setBounds(50, 100, 400, 25);
		avgLatencyLb.setBounds(50, 125, 400, 25);
		responseTimeLb.setBounds(50, 150, 400, 25);
		executionTimeLb.setBounds(50, 175, 400, 25);
		exportBt.setBounds(50, 200, 400, 25);
		
		exportBt.addActionListener(this);

		con.add(totalPacksLb);
		con.add(totalClassesLb);
		con.add(totalMethodsLb);
		con.add(totalMessageLb);
		con.add(avgLatencyLb);
		con.add(responseTimeLb);
		con.add(executionTimeLb);
		con.add(exportBt);
		

		this.setBounds((int) (getScreenSize().getWidth() - screenWidth) / 2,
				(int) (getScreenSize().getHeight() - screenHeight) / 2,
				screenWidth, screenHeight);
		
		this.setVisible(true);

	}
	
	public void exportToXls(){
		
		//Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook(); 
         
        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("Dynamic Metric Data");
          
        //This data needs to be written (Object[])
        Map<String, Object[]> data = new TreeMap<String, Object[]>();
        data.put("1", new Object[] {"Class Name", "EC_CC", "EC_CM","EC_CD","IC_CC", "IC_CM","IC_CD"});
        int i=2;
        
        Iterator<String> keys=clsEC.keySet().iterator();
        while(keys.hasNext()){
        	String key=keys.next();
        	data.put(i+"", new Object[] {key,clsEC.get(key),mtlEC.get(key),mslEC.get(key),clsIC.get(key),mtlIC.get(key),mslIC.get(key)});
        	++i;
        }
        //Iterate over data and write to sheet
        Set<String> keyset = data.keySet();
        int rownum = 0;
        for (String key : keyset)
        {
            Row row = sheet.createRow(rownum++);
            Object [] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr)
            {
               Cell cell = row.createCell(cellnum++);
               if(obj instanceof String)
                    cell.setCellValue((String)obj);
               
                else if(obj instanceof Integer)
                    cell.setCellValue((Integer)obj);
            }
        }
        try
        {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File("/root/jp2/Analysis_Report.xlsx"));
            workbook.write(out);
            out.close();
            System.out.println("Analysis_Report.xlsx written successfully on disk.");
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
		
	
	
	private Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}

	public void actionPerformed(ActionEvent e) {
		exportToXls();
	}

}
