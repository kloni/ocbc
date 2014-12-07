package test;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Arrays;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;
import de.uniba.wiai.lspi.util.logging.Logger;

public class Test {
	
	private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Test.class.getName());

	private static final class Battleship implements NotifyCallback {
		
		/**
		 * Logger for this class.
		 */
		private static final Logger logger = Logger.getLogger(Battleship.class);
		
		private Chord node;
		private String name;
		
		public Battleship(Chord node, String name) {
			this.node = node;
			this.name = name;
		}
				
		@Override
		public void retrieved(ID target) {
			logger.info("[" + name + "] retrieved: " + target.toString());
			node.broadcast(target, false);
		}

		@Override
		public void broadcast(ID source, ID target, Boolean hit) {
			logger.info("[" + name + "] broadcast: " + target.toString() + " " + hit);
		}
	}

	/**
	 * @param args
	 * @throws UnknownHostException 
	 * @throws MalformedURLException 
	 * @throws ServiceException 
	 */
	public static void main(String[] args) throws UnknownHostException, MalformedURLException, ServiceException {
		PropertiesLoader.loadPropertyFile();
		
		byte[] max = new byte[20];
		Arrays.fill(max, Byte.MAX_VALUE);
		
		BigInteger hi = new BigInteger(max);
		logger.info("Highest ID: " + hi);
		ID hid = new ID(hi.toByteArray());
		
		URL boot = addLocalNode("localhost:4252", null).getURL();
		addLocalNode("localhost:42522", boot);
		addLocalNode("localhost:42523", boot);
		addLocalNode("localhost:42524", boot);
		addLocalNode("localhost:42525", boot);
		Chord c6 = addLocalNode("localhost:42526", boot);
		
		c6.retrieve(hid);

	}

	private static Chord addLocalNode(String name, URL boot)
			throws MalformedURLException, ServiceException {
		URL url = new URL("ocrmi://" + name + "/");
		
		Chord node = new ChordImpl();
		node.setCallback(new Battleship(node, name));
		if(boot==null) {
			node.create(url);
			logger.info("[" + name + "] created: " + node.getID() + " predecessor=" + node.getPredecessorID());
		} else {
			node.join(url, boot);
			logger.info("[" + name + "] joined: " + node.getID() + " predecessor=" + node.getPredecessorID());
		}
		
		return node;
	}

}
