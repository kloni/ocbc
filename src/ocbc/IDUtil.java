package ocbc;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;

public class IDUtil {
	
	private static MessageDigest md;
	static {
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static synchronized byte[] hash(byte[] in) {
		md.reset();
		md.update(in);
		return md.digest();
	}
	
	public static ID rnd() {
		return new ID(hash(new BigInteger(160, new Random()).toByteArray()));
	}
	
	public static ID min() {
		byte[] b = new byte[20];
		Arrays.fill(b, (byte)0);
		return new ID(b);
	}
	
	public static ID max() {
		byte[] b = new byte[20];
		Arrays.fill(b, (byte)255);
		return new ID(b);
	}
	
	public static Set<ID> ids(Collection<Node> nodes) {
		Set<ID> ids = new HashSet<ID>();
		for(Node node : nodes) {
			ids.add(node.getNodeID());
		}
		return ids;
	}
	
	public static boolean inRange(ID from, ID to, ID target){
		if(to.compareTo(from)>0) {
			return (to.compareTo(target) >= 0 && target.compareTo(from) >= 0);
		} else {
			return (from.compareTo(target) <= 0 && target.compareTo(to) >= 0) || (target.compareTo(to) <= 0);
		}
	}
	
	public static BigInteger range(ID from, ID to) {
		BigInteger range;
		if(to.compareTo(from)>0) {
			range = to.toBigInteger().subtract(from.toBigInteger());
		} else {
			range = IDUtil.max().toBigInteger().subtract(from.toBigInteger()).add(to.toBigInteger());
		}
		return range;
	}
	
	public static ID getIntervallID(ID from, ID to, int index){
		return ID.valueOf(range(from, to).divide(BigInteger.valueOf(100)).multiply(BigInteger.valueOf(index)));
	}
	
	public static int getIndex(ID from, ID to, ID target){
		
		BigInteger range = range(from,to);
		BigInteger shot;
		BigInteger index;
		
		if(to.compareTo(from)>0) {
			shot = target.toBigInteger().subtract(from.toBigInteger());
		} else {
			if(to.compareTo(target)<0){
				shot = target.toBigInteger().subtract(from.toBigInteger());
			}else{
				shot = IDUtil.max().toBigInteger().subtract(from.toBigInteger()).add(target.toBigInteger());
			}
		}
		
		index = BigInteger.valueOf(100).multiply(shot).divide(range);
		
		if(index.intValue() - 1 < 0 )
		{ return 0; } 
		
		return index.intValue() - 1;
	}

}
