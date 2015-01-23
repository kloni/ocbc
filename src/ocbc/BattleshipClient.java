package ocbc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;
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
	
	private static final Logger logger = Logger.getLogger(BattleshipClient.class);
	
	ChordImpl chord;
	Strategy strategy;
	MyShips ships;
	
	private Integer s, i;
	
	public BattleshipClient(String localUrl, String bootstrapUrl, Integer i, Integer s, String strategy) throws MalformedURLException, ServiceException {
		this.strategy = Strategy.getInstance(strategy);
		this.i = i;
		this.s = s;
		
		chord = new ChordImpl();
		chord.setCallback(this);
		
		URL local = new URL(localUrl);
		String protocol = local.getProtocol();
		if(("ocrmi".equals(protocol) || "ocsocket".equals(protocol)) && local.getPort()==1) {
			local = new URL(protocol + "://" + local.getHost() + ":" + (new Random(System.currentTimeMillis()).nextInt(60000)+4243) + "/");
		}
		
		if(bootstrapUrl==null) {
			chord.create(local);
			logger.info(chord + " created");
		} else {
			chord.join(local, new URL(bootstrapUrl));
			logger.info(chord + " joined, " + " predecessor=" + chord.getPredecessorID());
		}
	}
	
	/**
	 * To be called, after nodes have joined
	 */
	public void init() {
		strategy.init(chord.getID(), chord.getPredecessorID(), IDUtil.ids(chord.getFingerTable()), i, s);
		ships = new MyShips(chord.getID(), chord.getPredecessorID(), i, s);
	}

	@Override
	public void retrieved(ID target) {
		if(ships==null) init();
		logger.info(chord + " retrieved: " + target.toString());
		chord.broadcast(target, ships.shoot(target));
		chord.retrieve(strategy.next());
	}

	@Override
	public void broadcast(ID source, ID target, Boolean hit) {
		if(ships==null) init();
		logger.info(chord + " broadcast from " + source + ": " + target.toString() + " " + hit);
		if(strategy.inform(source, target, hit)) {
			logger.info("Sunk " + source);
			System.out.println("Finished: " + new Date());
			System.out.println(source);
			chord.leave();
			System.exit(0);
		}
	}

	@Override
	public String toString() {
		return chord.getURL() + " [" + chord.getID() + "]";
	}

}
