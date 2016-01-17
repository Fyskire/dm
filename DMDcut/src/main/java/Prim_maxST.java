import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;


public class Prim_maxST {
	
	private PriorityQueue pq = new PriorityQueue();
	private LinkedList<ArrayList<WeightedEdge>> adj = new LinkedList<ArrayList<WeightedEdge>>();
	
	/**
	 * Puts WeightedEdges in PriorityQueue
	 * @param path
	 */
	public void input(String path){
		BufferedReader br = null;
		String line = "";
		String splitBy = " ";
		
		try{
			br = new BufferedReader(new FileReader(path));
			
			while (!(line = br.readLine()).equals("*Edges")){}
            
            while ((line = br.readLine()) != null){
            	
            	String[] data = line.split(splitBy);
            	WeightedEdge w = new WeightedEdge(1.0, new Tuple(Integer.valueOf(data[0]), Integer.valueOf(data[1])));
            	pq.add(w);
            	

            }
            
//            Baue adjazenzliste auf
            for(int i = 0; i < pq.size(); i++){ 
            	int x =pq.get(i).edge.x;
            	
            	for(int j = 0; j < pq.size(); j++){
//            		if(pq.get(j).edge)
            	}
            }
//            Ende des aufbaus
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
                    e.printStackTrace();
					}
            }
        }

        System.out.println("Input done");
      pq.printAll();
    }
	
	
	/**
	 * Algorithm to build Max Spanning Tree
	 * Based on https://de.wikipedia.org/wiki/Algorithmus_von_Prim#Algorithmus
	 * Inifinty is replaced by '-1'
	 * @param path
	 */
	public void getMaxST(String path){
		input(path);
		
		LinkedList<Integer> wert = new LinkedList<Integer>();
		LinkedList<Integer> pi = new LinkedList<Integer>();
		
		for(int u = 0; u < pq.size(); u++){
			wert.add(u, -1); //-1 is infinite
			pi.add(u, 0);
		}
		
		wert.set(0, 0); //Sets startingpoint
		
		while(pq.size() != 0){
			WeightedEdge u = pq.getFirst();
			
		}
		
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Prim_maxST p = new Prim_maxST();
		p.input("karate.paj");
	}

}
