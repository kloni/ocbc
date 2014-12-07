package ocbc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.TreeSet;

import ocbc.strategy.RandomStrategy;
import ocbc.strategy.Strategy;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;
import de.uniba.wiai.lspi.util.logging.Logger;

public class BattleshipClient implements NotifyCallback {
	
	static {
		PropertiesLoader.loadPropertyFile();
		Strategy.register("random", new RandomStrategy());
	}
	
	private static final Logger logger = Logger.getLogger(BattleshipClient.class);
	
	private ChordImpl chord;
	private Strategy strategy;
	private MyShips ships;
	
	public static void main(String[] args) throws MalformedURLException, ServiceException, NoSuchAlgorithmException {
		BattleshipClient ocbc;
		
		if(args.length>1) {
			ocbc = new BattleshipClient(args[0], args[1]);
			handleInput(ocbc);
			ocbc.chord.leave();
			
		} else if(args.length>0) {
			ocbc = new BattleshipClient(args[0]);
			handleInput(ocbc);
			ocbc.chord.leave();
			
		} else {
			System.out.println("Usage: BattleshipClient localUrl {bootstrapUrl}");
		}
	}
	
	public static void handleInput(BattleshipClient c) throws NoSuchAlgorithmException {
		int key = 0;
		while(key!='q') {
			try {
				key = System.in.read();
				switch (key) {
					case 'f':
						System.out.println("Finger table:");
						for(Node node : c.chord.getFixedFingerTable()) {
							System.out.println("  " + node);
						}
						break;
						
					case 'r':
						if(c.strategy==null) c.init();
						ID rndid = IDUtil.rnd();
						System.out.println("Retrieving " + rndid + " ...");
						c.chord.retrieve(c.strategy.next());
						break;
						
					case 'x':
						ID maxid = IDUtil.max();
						System.out.println("Retrieving " + maxid + " ...");
						c.chord.retrieve(maxid);
						break;
						
					case 'n':
						ID minid = IDUtil.min();
						System.out.println("Retrieving " + minid + " ...");
						c.chord.retrieve(minid);
						break;
						
					case 'p':
						System.out.println("Predecessor: " + c.chord.getPredecessorID());
						break;
						
					case 'i':
						c.init();
						break;
	
					default:
						break;
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	public BattleshipClient(String localUrl) throws MalformedURLException, ServiceException {
		this(localUrl, null);
	}
	
	public BattleshipClient(String localUrl, String bootstrapUrl) throws MalformedURLException, ServiceException {
		chord = new ChordImpl();
		chord.setCallback(this);
		
		if(bootstrapUrl==null) {
			chord.create(new URL(localUrl));
			logger.info(chord + " created");
		} else {
			chord.join(new URL(localUrl), new URL(bootstrapUrl));
			logger.info(chord + " joined, " + " predecessor=" + chord.getPredecessorID());
		}
	}
	
	public void init() {
		strategy = Strategy.getInstance("random");
		strategy.init(chord.getID(), chord.getPredecessorID(), IDUtil.ids(chord.getFingerTable()), 10, 5);
		ships = new MyShips(chord.getID(), chord.getPredecessorID(), 10, 5);
	}

	@Override
	public void retrieved(ID target) {
		if(strategy==null) init();
		logger.info(chord + " retrieved: " + target.toString());
		chord.broadcast(target, ships.shoot(target));
		chord.retrieve(strategy.next());
	}

	@Override
	public void broadcast(ID source, ID target, Boolean hit) {
		if(strategy==null) init();
		logger.info(chord + " broadcast from " + source + ": " + target.toString() + " " + hit);
		if(strategy.inform(source, target, hit)) {
			logger.error("Sunk " + source);
			chord.leave();
		}
	}

	@Override
	public String toString() {
		return chord.getURL() + " [" + chord.getID() + "]";
	}

}
