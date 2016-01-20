import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class DCTree {
	
	ArrayList<Node> v = new ArrayList<Node>(); //entspricht v aus dem paper. speichert ob checked oder nicht, index +1 entspricht namen der Knoten
	ArrayList<ArrayList<WeightedEdge>> ew = new ArrayList<ArrayList<WeightedEdge>>(); //entpsricht e und w aus dem paper.
	BufferedWriter br;
	ArrayList<WeightedEdge> edgeList = new ArrayList<WeightedEdge>();
	ArrayList<HashSet<Node>> partitions = new ArrayList<HashSet<Node>>(); // just for Yeast
	
	/**
	 * input .paj with nodes and edges
	 * @param path
	 */
	public void input(String path){
//		ArrayList<WeightedEdge> zwischenspeicher = new ArrayList();
		BufferedReader br = null;
		String line = "";
		String splitBy = " ";
		
		try{
			br = new BufferedReader(new FileReader(path));
			
			//Einlesen von Knoten in v
//			while (!(line = br.readLine()).equals("*Vertices")){}
			line = br.readLine();
			String[] data = line.split("Vertices ");
			int vSize = Integer.parseInt(data[1]);
			v = new ArrayList<Node>();
			
			for(int i = 0; i < vSize; i++){
				v.add(new Node());
			}
			//Einlesen von Knoten in v ist zu ende
			
			//springe zur Kantenbeschreibung
			while (!(line = br.readLine()).equals("*Edges")){}
            
			//Kanten einlesen um sie zu speichern
            while ((line = br.readLine()) != null){
            	
            	String[] data2 = line.split(splitBy);
            	WeightedEdge w = new WeightedEdge(1.0, new Tuple(Integer.valueOf(data2[0]), Integer.valueOf(data2[1])));
            	edgeList.add(w);
            }
            int eSize = edgeList.size();
            //edgeListe durchgehen und ew entsprechend hinzufuegen
            for(int i = 0; i < vSize; i++){//Für jeden existierenden Knoten
            	ArrayList<WeightedEdge> zwischenspeicher = new ArrayList<WeightedEdge>(); //Leere den zwischenspeicher
            	for(int j = 0; j < eSize; j++){ //Alle Eintraege von edgeList durchgehen
            		WeightedEdge el = edgeList.get(j); //Hole Kante aus der Liste
            		int x = el.edge.x;//Merke die beiden durch die Kante verbunden Knoten
            		int y = el.edge.y;
            		if((x == (i+1))){ //Entspricht x oder y dem aktuellen Knoten, so speichere die Kante im Zwischenspeicher
            			zwischenspeicher.add(el);
            		}
            		if((y == (i+1))){
            			zwischenspeicher.add(el);
            		}
            	}
            	ew.add(zwischenspeicher);//Fuege den Zwischenspeicher der ew Liste hinzu (Index ist Name des Knoten, analog zu Liste v)
            }
            
            
//          Lese Node neighbours ein
            for(int i = 0; i < vSize; i++){
            	HashSet<Node> zs = new HashSet<Node>();
            	
            	for(int j = 0; j < ew.get(i).size(); j++){
            		if(ew.get(i).get(j).edge.x == (i+1)){
            			zs.add(v.get((ew.get(i).get(j).edge.y)-1));
            		}
            	}
            	
            	for(int j = 0; j < ew.get(i).size(); j++){
            		if(ew.get(i).get(j).edge.y == (i+1)){
            			zs.add(v.get((ew.get(i).get(j).edge.x)-1));
            		}
            	}
            	v.get(i).addNeighbourSet(zs);
//            	v.get(i).neighbours = zs;
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
        System.out.println("Input done. ew size is: " + ew.size());
    }
	
	/**
	 * add similarity to edges; not used
	 */
	public void addSim(){
		for(int i = 0; i < edgeList.size(); i++){
			double sim = calcJac((v.get((edgeList.get(i).edge.x)-1)), (v.get((edgeList.get(i).edge.y)-1)));
			edgeList.get(i).similarity = (sim*edgeList.get(i).weight);
//			System.out.println(edgeList.get(i).similarity + ", " + i);
		}	
	}
	
	/**
	 * calculate Jaccard coefficient of two adjacent nodes
	 * @param n1
	 * @param n2
	 * @return Jaccard coefficient
	 */
	public double calcJac(Node n1, Node n2){
		
		HashSet<Node> hs1 = n1.neighbours;
		HashSet<Node> hs2 = n2.neighbours;
		HashSet<Node> union = new HashSet<Node>();
		union.addAll(hs1);
		HashSet<Node> schnitt = new HashSet<Node>();
		schnitt.addAll(hs1);
		
		union.addAll(hs2);
		schnitt.retainAll(hs2);
		
		double schnittSize = schnitt.size()+2;
		double unionSize = union.size();
		
		double jacKoef = schnittSize/unionSize;
		
		return jacKoef;
	}
	
	/**
	 * build dcTree; information sstored in nodes
	 */
	public void dcTree(){
		
		Node startNode = v.get(0);
		startNode.checked = true;
		ArrayList<Node> t = new ArrayList<Node>();
		t.add(startNode);
		
		while(t.size() < v.size()){
			
			double maxv = -1;
			Node p = null;
			Node q = null;
			
			for(int i = 0; i < t.size(); i++){
				Node u = t.get(i);
				
				for(Node vNode : u.neighbours){
					
					if(vNode.checked == false){
						
						double currentSim = calcJac(u, vNode);
						if (currentSim > maxv) {
							maxv = currentSim;
							p = vNode;
							q = u;
						}
					}
				}
			}
			
			if (maxv == -1) {
			    System.out.println( "Graph is not completely connected");
			    break; // break condition, if graph is not completely connected
			}
			
			p.checked = true;
			p.connect = q;
			p.density = maxv;
			t.add(p);
		}
		
		//print DCT
		System.out.println( (v.indexOf(t.get(0))+1) + " -> ");
		for(int i = 1; i < t.size(); i++) {
			System.out.println( (v.indexOf(t.get(i))+1) + " -> " + (v.indexOf(t.get(i).connect)+1));
		}
		/*
		//printtest
		System.out.println( calcJac(v.get(0), v.get(4)) );
		System.out.println( calcJac(v.get(0), v.get(10)) );*/
	}
	
	/**
	 * dcut
	 */
	public void dcut() {
		double minDcutValue = Double.MAX_VALUE;
		Node dcutNode = null;
		
		HashSet<Node> partition1 = new HashSet<Node>(); // just for yeast
		HashSet<Node> partition2 = new HashSet<Node>();
		
		for (Node u : v) { // iterate through nodes
			if (u.connect == null) { // skip first node in DCT (and unconnected nodes)
				continue;
			}
			
			double d = u.density; // d(C1, C2)
			
			// partition C1
			HashSet<Node> c1 = new HashSet<Node>();
			c1.add(u);
			int c1OldSize = -1;
			while(c1OldSize < c1.size()) {
				c1OldSize = c1.size();
				HashSet<Node> temp = new HashSet<Node>();
				for (Node vNode : c1) {
					if (vNode.connect != null && vNode != u) {
						temp.add(vNode.connect);
					}
				}
				c1.addAll(temp);
				for (Node vNode : v) {
					if (c1.contains(vNode.connect)) {
						c1.add(vNode);
					}
				}
			}
			
			// partition C2
			HashSet<Node> c2 = new HashSet<Node>();
			c2.add(u.connect);
			int c2OldSize = -1;
			while(c2OldSize < c2.size()) {
				c2OldSize = c2.size();
				HashSet<Node> temp = new HashSet<Node>();
				for (Node vNode : c2) {
					if (vNode.connect != null) {
						temp.add(vNode.connect);
					}			
				}
				c2.addAll(temp);
				for (Node vNode : v) {
					if (c2.contains(vNode.connect) && vNode != u) {
						c2.add(vNode);
					}
				}
			}
			
			//dcut value
			double dcutValue = d / Math.min(c1.size(), c2.size());
			
			if (dcutValue < minDcutValue) {
				minDcutValue = dcutValue;
				dcutNode = u;
				// just for Yeast
				partition1 = c1;
				partition2 = c2;
			}
		}
		
		partitions.add(partition1); // just for yeast
		partitions.add(partition2);
		
		System.out.println("Cut between " + (v.indexOf(dcutNode)+1) + " and " + (v.indexOf(dcutNode.connect)+1)); // output
		dcutNode.connect = null; // cut
	}
	
	/**
	 * for task 3; under construction
	 */
	public void yeast() {
		ArrayList<HashSet<Integer>> realClustering = inputClu(); // get real 13 clusters of yeast
		ArrayList<HashSet<Integer>> ourClustering = new ArrayList<HashSet<Integer>>();
		ArrayList<HashSet<Node>> ourNodeClustering = new ArrayList<HashSet<Node>>();
		
		// get Clusters out of partitions (should be 11 cuts and 12 cluster)
		for (int i = 0; i < partitions.size()-1; i++) {
			boolean isCluster = true;
			for (int j = i+1; j < partitions.size(); j++) {
				if (partitions.get(i).containsAll(partitions.get(j))) {
					isCluster = false;
					break;
				}
			}
			if(isCluster) {
				ourNodeClustering.add(partitions.get(i));
			}
		}
		ourNodeClustering.add(partitions.get(partitions.size()-1));
		
		// put all nodes, which are not in dct, in 13th cluster
		HashSet<Node> singletons = new HashSet<Node>();
		for (Node n : v) {
		    if (n.connect == null) {
		        boolean notInDct = true;
		        for (HashSet<Node> hsn : ourNodeClustering) {
		            if (hsn.contains(n)) {
		                notInDct = false;
		                break;
		            }
		        }
		        if (notInDct) {
		            singletons.add(n);
		        }
		    }
		}
		ourNodeClustering.add(singletons);
		
		// to have clusters in the same format
		for (HashSet<Node> hsn : ourNodeClustering) {
			HashSet<Integer> hsi = new HashSet<Integer>();
			for (Node n : hsn) {
				 hsi.add( v.indexOf(n) );
			}
			ourClustering.add(hsi);
		}
		
		CalculateRand.calculateRand(ourClustering, realClustering, 2361);
	}
	
	/**
	 * input yeast cluster
	 * @return
	 */
	public ArrayList<HashSet<Integer>> inputClu() {
		ArrayList<HashSet<Integer>> realCluster = new ArrayList<HashSet<Integer>>();
		for (int i = 0; i < 13; i++) {
			realCluster.add(new HashSet<Integer>());
		}
		
		BufferedReader br = null;
		String line = "";
		int j = 0;
		
		try {
			br = new BufferedReader(new FileReader("YeastModified.clu"));
			while ((line = br.readLine()) != null){
            	realCluster.get( Integer.valueOf(line)-1 ).add(j);
            	j++;
            }
			
			br.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return realCluster;
	}
	
	
	public static void main(String[] args) {
		DCTree t = new DCTree();
		t.input("YeastModified.paj");
		//t.addSim();
		t.dcTree();
		int k = 11; // how many cuts
		for ( int i = 0; i < k; i++) {
			t.dcut();
		}
		
		//just for yeast
		t.yeast();
		
	}

}
