package ocbc;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import de.uniba.wiai.lspi.chord.com.Node;

public class FingerUtil {
	
	public static List<Node> fixTable(Node self, Node predecessor, List<Node> unordered) {
		SortedSet<Node> sorted = new TreeSet<Node>(unordered);
		List<Node> result = new ArrayList<Node>();
		result.addAll(sorted.tailSet(self));
		result.addAll(sorted.headSet(self));
		return result;
	}

}
