import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class GraphStatistic {
    
    ArrayList<String> nodeList;
    ArrayList<ArrayList<String>> edgeList;
    
    HashMap<String,Integer> nodes;
    ArrayList<ArrayList<Integer>> edgeIndices;
    
    /**
     * to input the given csv file
     * @param path
     * @return
     */
    public ArrayList<String> input_nodes(String path) {
        
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
     * to input the given rn files
     * @param path
     * @return
     */
    public ArrayList<ArrayList<String>> input_edges(String path) {
        
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
            for(ArrayList<String> edge: edgeList){
                
                Boolean b = edge.contains(nodeList.get(i));
 
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
    
    /*
    public void createDistMatrix() {
        List<HashMap<Integer,Integer>> distMatrix = new ArrayList<HashMap<Integer,Integer>>();
        
        for(int i = 0; i < nodeList.size(); i++) {
            HashMap<Integer,Integer> distLine = new HashMap<Integer,Integer>();
            for (int j = i; j < nodeList.size(); j++) {
                for (int k = 0; k < edgeList.size(); k++) {
                    if (edgeList.get(k).contains(nodeList.get(i)) && edgeList.get(k).contains(nodeList.get(j))) {
                        distLine.put(j,Integer.parseInt(edgeList.get(k).get(2)));
                    }
                }
            }
            distMatrix.add(distLine);
        }
    }*/
    
    /**
     * calculates Diameter without edge weight
     */
    public void calculateDiameter() {
        int diameter = 0;
        boolean connected = true;
        
        for (int i = 0; i < nodeList.size(); i++) {
            System.out.println(i);
            boolean[] encountered = new boolean[nodeList.size()];
            int[] distance = new int[nodeList.size()];
            Queue<Integer> queue = new LinkedList<Integer>();
            
            queue.add(i);
            while(!queue.isEmpty()) {
                int currentIndex = queue.poll();
                encountered[currentIndex] = true;
                
                for (int nextIndex : edgeIndices.get(currentIndex)) {
                    if (!encountered[nextIndex]) {
                        encountered[nextIndex] = true;
                        queue.add(nextIndex);
                        distance[nextIndex] = distance[currentIndex] + 1;
                        if (diameter < distance [nextIndex]) {
                            diameter = distance[nextIndex];
                        }
                    }
                }
                
            }
            
            if (connected) {
                for (boolean a : encountered) {
                    if (!a) {
                        connected = false;
                        break;
                    }
                }
            }
        }
        
        
        System.out.println("Durchmesser: " + diameter);
        System.out.println("Komplett zusammenhängend: " + connected);
    }
    
    /**
     * to get data structures for better performance
     */
    public void help() {
        nodes = new HashMap<String, Integer>();
        for(int i = 0; i < nodeList.size(); i++){
            nodes.put(nodeList.get(i), i);
        }
        
        
        edgeIndices = new ArrayList<ArrayList<Integer>>(); // contains indices adjacent nodes
        for(int i = 0; i < nodeList.size(); i++){
            ArrayList<Integer> connectedNodes = new ArrayList<Integer>();
            String currentName = nodeList.get(i);
            for(ArrayList<String> edge: edgeList){
                
                if (edge.contains(currentName)) {
                    String nextName;
                    if (currentName.equals(edge.get(0))) {
                        nextName = edge.get(1);
                    } else {
                        nextName = edge.get(0);
                    }
                    int nextIndex = nodes.get(nextName);
                    connectedNodes.add(nextIndex);
                }
                 
            }
            edgeIndices.add(connectedNodes);
        }
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        GraphStatistic gs = new GraphStatistic();
        //ArrayList<Integer> a;
        gs.nodeList=gs.input_nodes("WebKB-wisconsin-link1.csv");
        gs.edgeList=gs.input_edges("WebKB-wisconsin-link1.rn");
        //a = gs.nodeDegreeDistribution();
        //gs.printDD(a);
        
        gs.help();
        gs.calculateDiameter();
        
        System.out.println("finish");
        
        long end = System.currentTimeMillis();
        System.out.println("Run-time: " + (end-start) + " ms");
        System.out.println();
    }

}
