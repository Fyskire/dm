package apriori;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
/**
 * Data Mining
 * Apriori V2
 * @author John-Kevin Kraemer, Yorrick Reinhart, Jens Henninger
 *
 */
public class Apriori {
    
    private ArrayList<ArrayList<Integer>> data; //input data
    //Integer = Item; TreeSet = Itemset; kth innerList contains all frequent k+1-itemsets
    private ArrayList<ArrayList<TreeSet<Integer>>> candidateList; //Integer = Item; 
    private int k; //current items in a set +1
    private double treshold;
    private ArrayList<TreeSet<Integer>> negativeBorderList;
    
    /**
     * Constructor
     * @param path
     * @param treshold
     */
    public Apriori(String path, double treshold) {
        configure(path, treshold);
    }
    
    /**
     * to input the given csv files
     * @param path
     * @return csv_data
     */
    public ArrayList<ArrayList<Integer>> input_csv(String path){ //Bsp: path = "/Users/me/Downloads/test.csv"
        
        //Outer ArrayList which is going to contain all lines
        ArrayList<ArrayList<Integer>> csv_data = new ArrayList<ArrayList<Integer>>();
        
        //String csvFile = path;
        BufferedReader br = null;
        String line = "";
        String csvSplitBy = ",";
        int counter = 0;
        
        try {
            br = new BufferedReader(new FileReader(path));
            
            while ((line = br.readLine()) != null){
                
                /**
                 *Reads csv lines, splits them at ',' and puts'em in String[].
                 *Puts String[] contents into arrayList_line Object.
                 *Puts arrayList_line Object into csv_data.
                 */
                ArrayList<Integer> arrayList_line = new ArrayList<Integer>();
                String[] data = line.split(csvSplitBy);
                for(int i=0; i< data.length; i++){
                    arrayList_line.add(i, Integer.parseInt(data[i]));
                }
                csv_data.add(counter, arrayList_line);
                counter++;
                
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
     * to generate the itemsets with 1 item
     */
    public void createFirstCandidates() {
        ArrayList<TreeSet<Integer>> candidates = new ArrayList<TreeSet<Integer>>();
        for (int i = 0; i < data.get(0).size(); i++) {
            TreeSet<Integer> candidate = new TreeSet<Integer>();
            candidate.add(i);
            candidates.add(candidate);
        }
        candidateList.add(candidates);
        
    }
    
    /**
     * scan database and remove not frequent itemsets with k+1 items
     */
    public void findFrequent() {
        int min = (int)(Math.ceil(data.size() * treshold)); // calculate minsup
        
        for (Iterator<TreeSet<Integer>> it1 = candidateList.get(k).iterator(); it1.hasNext();) { // go through new itemsets
            
            TreeSet<Integer> cur = it1.next(); //current itemset
            
            int count1 = 0; // to count the lines which contain the itemset
            for (int i = 0; i < data.size(); i++) { // go through dataset
                int count2 = 0; // to count if all items of the set in the current line
                for (Iterator<Integer> it2 = cur.iterator(); it2.hasNext();) { // go through itemset
                    count2 += data.get(i).get(it2.next());
                }
                if (count2 == cur.size()) {
                    count1++;
                }
            }
            
            if (count1 < min) { //compare count with minsup
            	
            	negativeBorderList.add(cur);
                it1.remove();
            }
        }
    }
    
    /**
     * generate new item sets by joining the by 1 smaller itemsets
     */
    public void createCandidates() {
        ArrayList<TreeSet<Integer>> candidates = new ArrayList<TreeSet<Integer>>();
        candidateList.add(candidates);
        //join step
        for (int i = 0; i < candidateList.get(k-1).size()-1; i++) {
            for (int j = i+1; j < candidateList.get(k-1).size(); j++) {
                TreeSet<Integer> cur = new TreeSet<Integer>(); //new itemset
                boolean joinable = true;
                Iterator<Integer> it1 = candidateList.get(k-1).get(i).iterator();
                Iterator<Integer> it2 = candidateList.get(k-1).get(j).iterator();
                // check if all item positions are equal except the last one 
                for (int l = 0; l < k-1; l++) {
                    if (it1.next() != it2.next()) {
                        joinable = false;
                        break;
                    }
                }
                //check if the second set has a greater last item
                if (it1.next() >= it2.next()) {
                    joinable = false;
                }
                // if so join
                if (joinable) {
                    cur.addAll(candidateList.get(k-1).get(i));
                    cur.addAll(candidateList.get(k-1).get(j));
                    candidates.add(cur);
                }
                
            }
        }
        
        //prune step
        for (Iterator<TreeSet<Integer>> setIt = candidateList.get(k).iterator(); setIt.hasNext();) { // go through new itemsets
            TreeSet<Integer> setToReview = setIt.next(); //current itemset to check subsets
            
            for (Iterator<Integer> itemIt = setToReview.iterator(); itemIt.hasNext();) { // go through itemset
                // build subsets and check if they are frequent
                TreeSet<Integer> subset = new TreeSet<Integer>();
                subset.addAll(setToReview);
                subset.remove(itemIt.next());
                if (!candidateList.get(k-1).contains(subset)) {
                    setIt.remove();
                    break;
                }
            }
            
        }
    }
    
    /**
     * start the apriori algorithm
     */
    public void start_algorithm() {
        candidateList = new ArrayList<ArrayList<TreeSet<Integer>>>();
        negativeBorderList = new ArrayList<TreeSet<Integer>>();
        createFirstCandidates();
        findFrequent();
        
        while (!candidateList.get(k).isEmpty()) {
            k++;
            createCandidates();
            findFrequent();
        }
    }
    
    /**
     * configure the algorithm
     * @param path
     * @param treshold
     */
    public void configure(String path, double treshold) {
        data = input_csv(path);
        this.treshold = treshold;
    }
    
    /**
     * to print the data to the console
     */
    public void printData() {
        System.out.println("Data:");
        for (Iterator<ArrayList<Integer>> it1 = data.iterator(); it1.hasNext();) {
            for (Iterator<Integer> it2 = it1.next().iterator(); it2.hasNext();) {
                System.out.print(it2.next());
                if (it2.hasNext()) {
                    System.out.print(",");
                } else {
                    System.out.println();
                }
            }
        }
    }
    
    /**
     * to print the frequent sets to the console
     */
    public void printFrequentSets() {
        System.out.println("Frequent Candidates:");
        for (int i = 0; i < candidateList.size(); i++) {
            System.out.println("with " + (i+1) + " items:");
            for (int j = 0; j < candidateList.get(i).size(); j++) {
                System.out.print("{");
                for (Iterator<Integer> it2 = candidateList.get(i).get(j).iterator(); it2.hasNext();) {
                    System.out.print(it2.next());
                    if (it2.hasNext()) {
                        System.out.print(",");
                    } else {
                        System.out.print("}");
                    }
                }
                System.out.println();
                
            }
        }
        
    }
    
    public void printNegativeBorder(){
    	System.out.println("Negative borders are: ");
    	for(int i = 0; i < negativeBorderList.size(); i++){
    		System.out.println(negativeBorderList.get(i));
    	}
    }
    
    
    /**
     * main
     * @param args
     */
    public static void main(String[] args) {
        Apriori a = new Apriori("U:\\eclipse\\apriori\\dm2.csv", 0.4);
        
        long start = System.currentTimeMillis();
        a.start_algorithm();
        long end = System.currentTimeMillis();
//        System.out.println("Run-time: " + (end-start) + " ms");
        
//        a.printData();
        a.printFrequentSets();
        a.printNegativeBorder();
        System.out.println("Run-time: " + (end-start) + " ms");
    }

}
