package ocbc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.util.logging.Logger;
import de.uniba.wiai.lspi.util.logging.Logger.LogLevel;

public class GraphTool {
	
	private static final Logger logger = Logger.getLogger(GraphTool.class);
	
	private static File getFile(Integer txid) {
		File dir = new File("dot");
		if(!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, txid + ".dot");
		if(!file.exists()) {
			writeLine(file, "digraph dht {");
		}
		return file;
	}
	
	public static void writeFinger(Integer txid, ID from, ID to) {
		if(logger.isEnabledFor(LogLevel.DEBUG)) {
			String n1 = from.toString().trim().replaceAll(" ", "").substring(0, 6);
			String n2 = to.toString().trim().replaceAll(" ", "").substring(0, 6);
			String line = "N" + n1 + " -> N" + n2 + " [style=dotted];";
			writeLine(getFile(txid), line);
		}
	}
	
	public static void writePredecessor(Integer txid, ID from, ID to) {
		if(logger.isEnabledFor(LogLevel.DEBUG)) {
			String n1 = from.toString().trim().replaceAll(" ", "").substring(0, 6);
			String n2 = to.toString().trim().replaceAll(" ", "").substring(0, 6);
			String line = "N" + n1 + " -> N" + n2 + ";";
			writeLine(getFile(txid), line);
		}
	}

	private static void writeLine(File f, String line) {
		Writer writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(f, true));
			writer.write(line + "\n");
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			if(writer!=null) {
				try {
					writer.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}
	}

}
