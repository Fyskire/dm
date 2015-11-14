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
    private double threshold;
    private ArrayList<ArrayList<TreeSet<Integer>>> negativeBorderList;
    private ArrayList<ArrayList<TreeSet<Integer>>> positiveBorderList;
    private ArrayList<ArrayList<Integer>> countList; // counts the appearance of itemsets 
    private ArrayList<ArrayList<TreeSet<Integer>>> closedSets;
    private ArrayList<ArrayList<TreeSet<Integer>>> freeSets;
    
    /**
     * Constructor
     * @param path
     * @param threshold
     */
    public Apriori(String path, double threshold) {
        configure(path, threshold);
    }
    
    /**
     * to input the given csv files
     * @param path
     * @return
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
        int min = (int)(Math.ceil(data.size() * threshold)); // calculate minsup
        int[] setcounts = new int[candidateList.get(k).size()];
        
        //counting
        for (int i = 0; i < data.size(); i++) { // go through dataset
            for (int j = 0; j < candidateList.get(k).size(); j++) { // go through new itemsets
                
                TreeSet<Integer> cur = candidateList.get(k).get(j); //current itemset
                int itemcount = 0; // to count if all items of the set in the current line
                for (Iterator<Integer> it2 = cur.iterator(); it2.hasNext();) { // go through itemset
                    itemcount += data.get(i).get(it2.next());
                }
                if (itemcount == cur.size()) {
                    setcounts[j]++;
                }
                
            }
        }
        
        ArrayList<TreeSet<Integer>> kBorders = new ArrayList<TreeSet<Integer>>();
        negativeBorderList.add(kBorders);
        ArrayList<TreeSet<Integer>> frequentKItems = new ArrayList<TreeSet<Integer>>();
        ArrayList<Integer> kItemsCounts = new ArrayList<Integer>();
        //removing
        for (int j = 0; j < candidateList.get(k).size(); j++) {
            TreeSet<Integer> cur = candidateList.get(k).get(j);
            if (setcounts[j] < min) { //compare count with minsup
                kBorders.add(cur); // add to negative Border
                //it.remove();
            } else {
            	frequentKItems.add(cur);
            	kItemsCounts.add(setcounts[j]);
            }
        }
        
        
        candidateList.set(k, frequentKItems);
        countList.add(kItemsCounts);
        
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
                    
                    //prune step
                    boolean subsetsFrequent = true;
                    for (Iterator<Integer> itemIt = cur.iterator(); itemIt.hasNext();) { // go through itemset
                        // build subsets and check if they are frequent
                        TreeSet<Integer> subset = new TreeSet<Integer>();
                        subset.addAll(cur);
                        subset.remove(itemIt.next());
                        if (!candidateList.get(k-1).contains(subset)) {
                            subsetsFrequent = false;
                            break;
                        }
                    }
                    if (subsetsFrequent) {
                        candidates.add(cur);
                    }
                }
                
            }
        }
    }
    
    /**
     * start the apriori algorithm
     */
    public void start_algorithm() {
        candidateList = new ArrayList<ArrayList<TreeSet<Integer>>>();
        negativeBorderList = new ArrayList<ArrayList<TreeSet<Integer>>>();
        k = 0;
        countList = new ArrayList<ArrayList<Integer>>();
        
        createFirstCandidates();
        findFrequent();
        
        while (!candidateList.get(k).isEmpty()) {
            k++;
            createCandidates();
            findFrequent();
        }
        
        findPositiveBorders();
        findFreeSets();
    }
    
    /**
     * configure the algorithm
     * @param path
     * @param threshold
     */
    public void configure(String path, double threshold) {
        data = input_csv(path);
        this.threshold = threshold;
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
            System.out.println(candidateList.get(i).size() + " itemsets with " + (i+1) + " items:");
            
            //print each itemset
            /*
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
                
            }*/
            
        }
        
    }
    
    /**
     * to print the negative borders to the console
     */
    public void printNegativeBorder(){
        System.out.println("Negative borders are: ");
        for(int i = 0; i < negativeBorderList.size(); i++){
            System.out.println(negativeBorderList.get(i).size() + " negative Borders with " + (i+1) + " items:");
            
            //print each Border
            /*
            for(int j = 0; j < negativeBorderList.get(i).size(); j++){
                System.out.println(negativeBorderList.get(i).get(j));
            }*/
        }
    }
    
    /**
     * to find positive borders and CLOSED sets
     */
    public void findPositiveBorders() {
        positiveBorderList = new ArrayList<ArrayList<TreeSet<Integer>>>();
        closedSets = new ArrayList<ArrayList<TreeSet<Integer>>>();
        for (int i = 0; i < candidateList.size()-1; i++) {
            ArrayList<TreeSet<Integer>> iBorders = new ArrayList<TreeSet<Integer>>(); //positive Border with i+1 items
            positiveBorderList.add(iBorders);
            ArrayList<TreeSet<Integer>> closedISets = new ArrayList<TreeSet<Integer>>(); //closed Sets with i+1 items
            closedSets.add(closedISets);
            for (int j = 0; j < candidateList.get(i).size(); j++) {
                boolean positiveBorder = true;
                boolean closed = true;
                for (int l = 0; l < candidateList.get(i+1).size(); l++) { // check if a more specific set is frequent
                    if (candidateList.get(i+1).get(l).containsAll(candidateList.get(i).get(j))) {
                        positiveBorder = false;
                        if (countList.get(i).get(j) == countList.get(i+1).get(l)) { // check if closed
                        	closed = false;
                        }
                    }
                }
                if (positiveBorder) {
                    iBorders.add(candidateList.get(i).get(j));
                }
                if (closed) {
                    closedISets.add(candidateList.get(i).get(j));
                }
            }
        }
    }
    
    /**
     * to print the positive borders to the console
     */
    public void printPositiveBorder() {
        System.out.println("Positive borders are: ");
        for(int i = 0; i < positiveBorderList.size(); i++){
            System.out.println(positiveBorderList.get(i).size() + " positive Borders with " + (i+1) + " items:");
            
            //print each Border
            /*
            for(int j = 0; j < positiveBorderList.get(i).size(); j++){
                System.out.println(positiveBorderList.get(i).get(j));
            }*/
        }
    }
    
    /**
     * to print frequent closed sets (which were found with findPositiveBorders-method) to the console
     */
    public void printClosedSets() {
    	System.out.println("Closed Sets are: ");
        for(int i = 0; i < closedSets.size(); i++){
            System.out.println(closedSets.get(i).size() + " closed Sets with " + (i+1) + " items:");
            
            //print closed Set
            /*
            for(int j = 0; j < closedSets.get(i).size(); j++){
                System.out.println(closedSets.get(i).get(j));
            }*/
        }
    }
    
    /**
     * to find free sets
     */
    public void findFreeSets() {
    	freeSets = new ArrayList<ArrayList<TreeSet<Integer>>>();
    	for (int i = 0; i < candidateList.size()-1; i++) {
    	    freeSets.add(null);
    	}
    	
    	for (int i = candidateList.size()-2; i > 0; i--) {
    		ArrayList<TreeSet<Integer>> freeISets = new ArrayList<TreeSet<Integer>>();
    		freeSets.set(i, freeISets);
    		for (int j = 0; j < candidateList.get(i).size(); j++) {
    			TreeSet<Integer> set = candidateList.get(i).get(j);
    			boolean free = true;
    			for (Iterator<Integer> itemIt = set.iterator(); itemIt.hasNext();) { // go through items
                    // build subsets and check their occurrence
                    TreeSet<Integer> subset = new TreeSet<Integer>();
                    subset.addAll(set);
                    subset.remove(itemIt.next());
                    int index = candidateList.get(i-1).indexOf(subset);
                    if (index >= 0) {
                        if (countList.get(i).get(j) == countList.get(i-1).get(index)) {
                        	free = false;
                        	break;
                        }
                    }
                }
    			
    			if (free) {
    				freeISets.add(candidateList.get(i).get(j));
    			}
    			
    		}
    	}
    	
    	// add the 1-item-sets with less appearance than max possible
    	if (!freeSets.isEmpty()) {
        	ArrayList<TreeSet<Integer>> freeISets = new ArrayList<TreeSet<Integer>>();
            freeSets.set(0, freeISets);
        	for (int j = 0; j < candidateList.get(0).size(); j++) {
        		
        		if (countList.get(0).get(j) < data.size()) {
        			freeISets.add(candidateList.get(0).get(j));
        		}
        	}
    	}
    }
    
    /**
     * to print frequent free sets to the console
     */
    public void printFreeSets() {
    	System.out.println("Free Sets are: ");
        for(int i = 0; i < freeSets.size(); i++){
            System.out.println(freeSets.get(i).size() + " free Sets with " + (i+1) + " items:");
            
            //print free Set
            /*
            for(int j = 0; j < freeSets.get(i).size(); j++){
                System.out.println(freeSets.get(i).get(j));
            }*/
        }
    }
    
    /**
     * main
     * @param args
     */
    public static void main(String[] args) {
        Apriori a = new Apriori("C:\\Users\\John\\Downloads\\dm1.csv", 0.5);
        
        long start = System.currentTimeMillis();
        a.start_algorithm();
        long end = System.currentTimeMillis();
        System.out.println("Run-time: " + (end-start) + " ms");
        System.out.println();
        
        //a.printData();
        a.printFrequentSets();
        System.out.println();
        a.printPositiveBorder();
        System.out.println();
        a.printNegativeBorder();
        System.out.println();
        a.printClosedSets();
        System.out.println();
        a.printFreeSets();
        
    }

}
