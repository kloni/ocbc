package ocbc.strategy;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import ocbc.IDUtil;
import de.uniba.wiai.lspi.chord.data.ID;

/**
 * Very stupid but simple random strategy
 * @author tom
 *
 */
public class RandomStrategy extends Strategy {
	
	private Integer i;
	private Integer s;
	private Map<ID, Set<ID>> hits = new HashMap<ID, Set<ID>>();
	private ID self, predecessor;
	private BigInteger range;
	private Random rnd = new Random(System.currentTimeMillis());
	
	public RandomStrategy() {
		
	}

	@Override
	public ID next() {
		BigInteger r;
		do {
			r = new BigInteger(range.bitLength(), rnd);
		} while(r.compareTo(range) >= 0);
		return ID.valueOf(self.toBigInteger().add(BigInteger.ONE).add(r));
	}

	@Override
	public boolean inform(ID node, ID target, boolean hit) {
		if(hit) {
			Set<ID> nhits = hits.get(node);
			if(nhits==null) {
				nhits = new HashSet<ID>();
				hits.put(node, nhits);
			}
			nhits.add(target);
			return nhits.size()==s;
		}
		return false;
	}

	@Override
	public void init(ID myId, ID predecessor, Set<ID> knownNodeIds, Integer i, Integer s) {
		this.i = i;
		this.s = s;
		this.predecessor = predecessor;
		this.self = myId;
		this.range = IDUtil.range(myId, predecessor);
	}

	@Override
	public void finalize() {
		
	}

}
