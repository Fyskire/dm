import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DCTree {
	
	ArrayList<Node> v = new ArrayList<Node>(); //entspricht v aus dem paper. speichert ob checked oder nicht, index +1 entspricht namen der Knoten
	ArrayList<ArrayList<WeightedEdge>> ew = new ArrayList<ArrayList<WeightedEdge>>(); //entpsricht e und w aus dem paper.
	BufferedWriter br;
	ArrayList<WeightedEdge> edgeList = new ArrayList<WeightedEdge>();
	
	public void input(String path){
		ArrayList<WeightedEdge> zwischenspeicher = new ArrayList();
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
            	zwischenspeicher.clear(); //Leere den zwischenspeicher
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DCTree t = new DCTree();
		t.input("karate.paj");
	}

}
