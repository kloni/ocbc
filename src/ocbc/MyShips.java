package ocbc;

import java.math.BigInteger;
import java.util.Random;
import java.util.TreeSet;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.util.logging.Logger;

public class MyShips {
	
	private static final Logger logger = Logger.getLogger(MyShips.class);
	
	private TreeSet<BigInteger> hits;
	private TreeSet<BigInteger> positions;
	private BigInteger interval;
	private ID predecessor;

	public MyShips(ID myId, ID predecessor, Integer i, Integer s) {
		this.predecessor = predecessor;
		
		Random rnd = new Random(System.currentTimeMillis());
		hits = new TreeSet<BigInteger>();
		positions = new TreeSet<BigInteger>();
		int count = 0;
		while(count<s) {
			int pos = rnd.nextInt(i-count);
			pos = positions.headSet(BigInteger.valueOf(pos), true).size() + pos;
			while(positions.contains(BigInteger.valueOf(pos))) {
				pos = pos + 1;
			}
			positions.add(BigInteger.valueOf(pos));
			count++;
		}
		BigInteger range = null;
		range = IDUtil.range(predecessor, myId);
		interval = range.divide(BigInteger.valueOf(i));
		
		logger.info(positions);
		
	}
	
	private BigInteger getIntervalNo(ID id) {
		BigInteger diff = IDUtil.range(predecessor, id);
		return diff.divide(interval);
	}
	
	public boolean shoot(ID target) {
		BigInteger intervalNo = getIntervalNo(target);
		if(positions.contains(intervalNo)) {
			positions.remove(intervalNo);
			hits.add(intervalNo);
			logger.error(hits);
			return true;
		}
		return false;
	}
}
