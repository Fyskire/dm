import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DCTree {
	
	ArrayList<Node> v; //entspricht v aus dem paper. speichert ob checked oder nicht, index +1 entspricht namen der Knoten
	ArrayList<ArrayList<WeightedEdge>> ew; //entpsricht e und w aus dem paper.
	BufferedWriter br;
	ArrayList<WeightedEdge> edgeList;
	
	public void input(String path){
		BufferedReader br = null;
		String line = "";
		String splitBy = " ";
		
		try{
			br = new BufferedReader(new FileReader(path));
			
			//Einlesen von Knoten in v
			String[] data = line.split("*Verticles ");
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
            	WeightedEdge w = new WeightedEdge(1.0, new Tuple(Integer.valueOf(data[0]), Integer.valueOf(data[1])));
            	edgeList.add(w);
            }
            
            //edgeListe durchgehen und ew entsprechend hinzufuegen
            for(int x = 0; x < vSize; x++){
            	
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
    }

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
