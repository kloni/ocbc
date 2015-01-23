package ocbc.strategy;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import ocbc.IDUtil;
import de.uniba.wiai.lspi.chord.data.ID;

public class WinStrategy extends Strategy{
	
	private Integer i, s;
	
	private Map<ID, Set<ID>>          hits       = new HashMap<ID, Set<ID>>();
	private Map<ID, Boolean[]>        shot       = new HashMap<ID, Boolean[]>();
	private Map<ID, Map<ID, Boolean>> unkownShot = new HashMap<ID, Map<ID, Boolean>>();
	private Map<ID, ID>               ranges     = new HashMap<ID, ID>();
	
	private ID self, predecessor, nextScanID;
	
	private BigInteger range;
	
	private Random rnd = new Random(System.currentTimeMillis());
	
	
	
	@Override
	public ID next() {
		
		// 1. Enemy down?
		for(ID key : hits.keySet()){ 
			if(hits.get(key).size() >= 9)
				if(key.compareTo(self) != 0){
				Boolean[] nshot = shot.get(key);
				if(nshot != null){
					Boolean[] hits = shot.get(key);
					for(int i = 0; i < hits.length; i++ ){ 
						if(hits[i] == null){ return IDUtil.getIntervallID(ranges.get(key), key, i);  }
					}
				}else{
					Map<ID, Boolean> ushot = unkownShot.get(key);
					ID lowest  = null;
					ID highest = null;
					for( ID ukey : ushot.keySet()){
						if(lowest  == null || lowest.compareTo( ukey) < 0){ lowest  = ukey; }
						if(highest == null || highest.compareTo(ukey) > 0){ highest = ukey; }
					}
					
					return ID.valueOf(highest.toBigInteger().add(lowest.toBigInteger()).divide(BigInteger.valueOf(2)));
				}
				
			} 
			
		}

		if(self.compareTo(nextScanID) > 0 && nextScanID.compareTo(predecessor) > 0){
			// own range
			ID  highestId = ID.valueOf(self.toBigInteger().add(BigInteger.ONE));
			int highestNo = 0;
			
			for(ID key : hits.keySet()){
				if(hits.get(key).size() > highestNo && key.compareTo(self) != 0){ 
					highestId = key;
					highestNo = hits.get(key).size();
					
				} 
			}
			
			if(highestId.equals(ID.valueOf(self.toBigInteger().add(BigInteger.ONE)))){
				BigInteger r;
				do {
					r = new BigInteger(range.bitLength(), rnd);
				} while(!inMap(ID.valueOf(r)) && IDUtil.inRange(this.predecessor, self, ID.valueOf(r)));
				ID returnId = ID.valueOf(r);
				return returnId;
			}
			
			if(!inMap(highestId)){return highestId;}
			
			BigInteger r;
			do {
				r = new BigInteger(range.bitLength(), rnd);
				ID next = ID.valueOf(self.toBigInteger().add(BigInteger.ONE).add(r));
				
				for( ID to : this.ranges.keySet() ){
					ID from =  this.ranges.get(to);
					if(inMap(next) && IDUtil.inRange(from, to, next) && IDUtil.inRange(from, to, highestId)){ return next; }
						System.out.println(inMap(next) + "" + IDUtil.inRange(from, to, next) + IDUtil.inRange(from, to, highestId));
				}
				
			} while(true);
			
			
		}
		else{
			// foreign range
			return nextScanID;
			
		}
		
	}
	
	private boolean inMap(ID next){
		
		for( ID to : this.ranges.keySet() ){
			
			ID from =  this.ranges.get(to);
			if(IDUtil.inRange(from, to, next)){
				int index = IDUtil.getIndex(from, to, next);
				if(index < 0 )
				{continue;}
				if( this.shot.get(to)[index] != null ){return true;}
			}
			
		}
		
		return false;
	}

	@Override
	public boolean inform(ID node, ID target, boolean hit) {
		System.err.println(target);
		if(target.compareTo(nextScanID) == 0){ 
			nextScanID = ID.valueOf(node.toBigInteger().add(BigInteger.ONE)); 
			ranges.put(node, target);
			
			// unknownShot > shot
			Map<ID, Boolean> ushot = unkownShot.get(node);
			if(ushot!=null){
				for(ID key : ushot.keySet()){
					Boolean nhit = ushot.get(key);
					Boolean[] holdShots = new Boolean[100]; 
					shot.put(node, holdShots);
					holdShots[IDUtil.getIndex(target, node, key)] = nhit;
				}
				
				// clear unknown shot list
				unkownShot.remove(node);
			}else{
				Boolean[] holdShots = new Boolean[100]; 
				shot.put(node, holdShots);
				holdShots[IDUtil.getIndex(target, node, target)] = hit;
			}
			
			
		}else{
			// Schuss speichern
			Boolean[] nshot = shot.get(node);
			
			if(nshot == null){
				// unknown range
				// save tmp shot
				Map<ID, Boolean> nunknownShot = unkownShot.get(node);
				if(nunknownShot == null){ 
					nunknownShot = new HashMap<ID, Boolean>(); 
					unkownShot.put(node, nunknownShot);
				}
				
				nunknownShot.put(target, hit);
			}else{
				// known range
				ID lowerBound = ranges.get(node);
				int index = IDUtil.getIndex(lowerBound, node, target);
				nshot[index] = hit;
				
			}
		}
		
		
		
		if(hit) {
			Set<ID>   nhits = hits.get(node);
			
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
		this.self        = myId;
		
		this.nextScanID  = ID.valueOf(this.self.toBigInteger().add(BigInteger.ONE));
		this.range       = IDUtil.range(myId, predecessor);
		
	}

	@Override
	public void finalize() {}

}
