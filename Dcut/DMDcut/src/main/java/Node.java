import java.util.ArrayList;
import java.util.HashSet;

public class Node {
	
	boolean checked = false;
	Node connect = null;
	double density;
	HashSet<Node> neighbours = null;

	public void addNeighbourSet(HashSet e){
		neighbours = e;
//		System.out.println(e);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
