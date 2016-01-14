
import java.util.Collections;
import java.util.LinkedList;


public class PriorityQueue {

	private LinkedList<WeightedEdge> queue = new LinkedList<WeightedEdge>();
	
	public void add(WeightedEdge we){
		queue.add(we);
		this.sort();
	}
	
	/**
	 * Gibt das erste Element der Queue aus und löscht es danach aus der Queue
	 * @return
	 */
	public WeightedEdge takeFirst(){
		if(queue.size() <= 0 && !(queue == null)){
			System.out.println("Queue ist size == 0 oder null");
			return null;
		} else {
			WeightedEdge a = queue.get(0);
			queue.poll();
			return a;
		}
	}
	
	public WeightedEdge getFirst(){
		return queue.getFirst();
	}
	
	public WeightedEdge get(int pos){
		return queue.get(pos);
	}
	
	private void sort(){
		if(queue.size() > 1){
			Collections.sort(queue);
//			System.out.println("sorted");
		}
	}
	
	public void printAll(){
		for(int i = 0; i < queue.size(); i++){
			System.out.print(queue.get(i).weight + " ");
		}
		System.out.println("");
		System.out.println("Printing of queue finished");
	}
	
	public int size(){
		return queue.size();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		WeightedEdge w1 = new WeightedEdge(1, new Tuple(1,2));
//		WeightedEdge w2 = new WeightedEdge(2, new Tuple(2,3));
//		WeightedEdge w3 = new WeightedEdge(3, new Tuple(3,1));
//		
//		PriorityQueue pq = new PriorityQueue();
//		pq.add(w3);
//		pq.add(w1);
//		pq.add(w2);
//		pq.printAll();
//		pq.get();
//		pq.printAll();
//		pq.add(new WeightedEdge(0.5, new Tuple(1,4)));
//		pq.printAll();
	}
	
}
