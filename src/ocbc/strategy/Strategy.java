package ocbc.strategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.uniba.wiai.lspi.chord.data.ID;

public abstract class Strategy {
	
	/**
	 * Get the next target.
	 * @return
	 */
	public abstract ID next();
	
	/**
	 * 
	 * @param node
	 * @param target
	 * @param hit
	 * @return
	 */
	public abstract boolean inform(ID node, ID target, boolean hit);
	
	/**
	 * Initialize the strategy with all known nodes and I and S parameters.
	 * @param knownNodeIds
	 * @param i
	 * @param s
	 */
	public abstract void init(ID myId, ID predecessor, Set<ID> knownNodeIds, Integer i, Integer s);
	
	/**
	 * Possibly store some data to use in a further run.
	 */
	public abstract void finalize();
	
	private static final Map<String, Strategy> strategyRegister = new HashMap<String, Strategy>();
	
	/**
	 * Get a strategy by name
	 * @param name
	 * @return
	 */
	public static final Strategy getInstance(String name) {
		return strategyRegister.get(name);
	}
	
	/**
	 * Register a strategy implementation under a certain name.
	 * @param name
	 * @param strategy
	 */
	public static final void register(String name, Strategy strategy) {
		strategyRegister.put(name, strategy);
	}

}
