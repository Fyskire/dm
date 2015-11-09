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
        //removing
        int j = -1;
        for (Iterator<TreeSet<Integer>> it = candidateList.get(k).iterator(); it.hasNext();) {
            j++;
            TreeSet<Integer> cur = it.next();
            if (setcounts[j] < min) { //compare count with minsup
                kBorders.add(cur); // add to negative Border
                it.remove();
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
        
        createFirstCandidates();
        findFrequent();
        
        while (!candidateList.get(k).isEmpty()) {
            k++;
            createCandidates();
            findFrequent();
        }
        
        findPositiveBorders();
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
     * to find positive borders
     */
    public void findPositiveBorders() {
        positiveBorderList = new ArrayList<ArrayList<TreeSet<Integer>>>();
        for (int i = 0; i < candidateList.size()-1; i++) {
            ArrayList<TreeSet<Integer>> iBorders = new ArrayList<TreeSet<Integer>>(); //positive Border with i+1 items
            positiveBorderList.add(iBorders);
            for (int j = 0; j < candidateList.get(i).size(); j++) {
                boolean positiveBorder = true;
                for (int l = 0; l < candidateList.get(i+1).size(); l++) { // check if a more specific set is frequent
                    if (candidateList.get(i+1).get(l).containsAll(candidateList.get(i).get(j))) {
                        positiveBorder = false;
                    }
                }
                if (positiveBorder) {
                    iBorders.add(candidateList.get(i).get(j));
                }
            }
        }
    }
    
    /**
     * to print the positive borders to the console
     */
    public void printPositiveBorder(){
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
     * main
     * @param args
     */
    public static void main(String[] args) {
        Apriori a = new Apriori("C:\\Users\\John\\Downloads\\dm4.csv", 0.4);
        
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
        
        
    }

}
