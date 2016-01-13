import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class GraphStatistic {
	
	ArrayList<String> nodeList;
	ArrayList<ArrayList<String>> edgeList;
	
	/**
     * to input the given csv files
     * @param path
     * @return
     */
    public ArrayList<String> input_nodes(String path){ //Bsp: path = "/Users/me/Downloads/test.csv"
        
        //Outer ArrayList which is going to contain all lines
        ArrayList<String> csv_data = new ArrayList<String>();
        
        //String csvFile = path;
        BufferedReader br = null;
        String line = "";
        String csvSplitBy = ",";
        
        
        try {
            br = new BufferedReader(new FileReader(path));
            
            while ((line = br.readLine()) != null){
                
                /**
                 *Reads csv lines, splits them at ',' and puts'em in String[].
                 *Puts String[] contents into arrayList_line Object.
                 *Puts arrayList_line Object into csv_data.
                 */
                String[] data = line.split(csvSplitBy);
           
                csv_data.add(data[0]);
                
                
                
                
            }
            
            
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
      
        return csv_data;
    }
    
    /**
     * to input the given csv files
     * @param path
     * @return
     */
    public ArrayList<ArrayList<String>> input_edges(String path){ //Bsp: path = "/Users/me/Downloads/test.csv"
        
        //Outer ArrayList which is going to contain all lines
        ArrayList<ArrayList<String>> csv_data = new ArrayList<ArrayList<String>>();
        
        //String csvFile = path;
        BufferedReader br = null;
        String line = "";
        String csvSplitBy = ",";
//        int counter = 0;
        
        try {
            br = new BufferedReader(new FileReader(path));
            
            while ((line = br.readLine()) != null){
                
                /**
                 *Reads csv lines, splits them at ',' and puts'em in String[].
                 *Puts String[] contents into arrayList_line Object.
                 *Puts arrayList_line Object into csv_data.
                 */
                ArrayList<String> arrayList_line = new ArrayList<String>();
                String[] data = line.split(csvSplitBy);
                for(int i=0; i< data.length; i++){
                    arrayList_line.add(i, data[i]);
                }
                csv_data.add(arrayList_line);
//                counter++;
                
            }
            
            
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
      
        return csv_data;
    }
    
    /**
     * 
     * @return
     */
    public ArrayList<Integer> nodeDegreeDistribution() {
    	int hoch = 0; //höchste Grad der Knoten
    	ArrayList<Integer> numEdge = new ArrayList<Integer>();
    	ArrayList<Integer> dist = new ArrayList<Integer>(); // so groß wie der Knoten mit der Höchsten Kantenzahl
    	
    	for(int i = 0; i < nodeList.size(); i++){
    		
    		int counter = 0;
    		for(int j = 0; j < edgeList.size(); j++){
    			
    			Boolean b = edgeList.get(j).contains(nodeList.get(i));
 
    			if (b){
    				counter++;
    			}
    		}
    		if (counter > hoch){
    			hoch = counter;
    		}
    		
    		numEdge.add(counter);
    	}
    	
    	for(int i = 0; i <= hoch; i++){
    		int counter = 0;
    		
    		for(int j = 0; j < numEdge.size(); j++){
    			if (i == numEdge.get(j)){
    				counter++;
    			}
    		}
    		dist.add(counter);
    	}
    	System.out.println("nodeDegreeDistribution calculated");
    	return dist;
    }
    
    /**
     * 
     * @param nodeDegreeDistribution
     */
    public void printDD(ArrayList<Integer> nodeDegreeDistribution){
    	BufferedWriter bw = null;
    	
    	try{
    		bw = new BufferedWriter(new FileWriter("output.txt"));
    		for(int i = 0; i < nodeDegreeDistribution.size();i++){
    			bw.append(nodeDegreeDistribution.get(i).toString());
    			bw.newLine();
    		}
    	} catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Writing done");
    }

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		GraphStatistic gs = new GraphStatistic();
		ArrayList<Integer> a;
		gs.nodeList=gs.input_nodes("WebKB-wisconsin-link1.csv");
		gs.edgeList=gs.input_edges("WebKB-wisconsin-link1.rn");
		a = gs.nodeDegreeDistribution();
		gs.printDD(a);
		System.out.println("feddisch");
		
		
	}

}
