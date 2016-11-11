package sdma.rtc.ui.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import sdma.rtc.ui.Main;



public class InstrumentProcess extends Thread{
	
	private ProcessBuilder pb = null;

	public InstrumentProcess() {
		
		//create shell script for runAgent.sh
		File runFile = new File(Main.getConfiguration().getScalsBaseDir()+"JP2/runAgent.sh");
		//make it an executable script
		runFile.setExecutable(true);
		//add commands to script and initialize
		List<String> commands = new ArrayList<String>();
		commands.add("./runAgent.sh");
		pb = new ProcessBuilder(commands);
		pb.directory(new File(Main.getConfiguration().getScalsBaseDir()+"JP2/"));
		pb.redirectErrorStream(true);
	}

	@Override
	public void run() {
		Process process;
		try {
			//start process
			process = pb.start();
			// Read output
			StringBuilder out = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = null, previous = null;
			while ((line = br.readLine()) != null)
				if (!line.equals(previous)) {
					previous = line;
					out.append(line).append('\n');
					System.out.println(line);
				}

			if (process.waitFor() == 0) {
				System.out.println("Done and Exit!");
				System.exit(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
