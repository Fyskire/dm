import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Mining
 * gSpan
 * @author John-Kevin Kraemer, Yorrick Reinhart, Jens Henninger
 *
 */
public class ToxicMolecules {
    private List<String> moleculeNames;
    private List<Integer> toxicList;
    private List<ArrayList<Boolean>> subsList;
    private List<ArrayList<String>> trainingDfsList;
    
    /**
     * to input molecules.groundTruth
     */
    public void input_groundTruth(){
        moleculeNames = new ArrayList<String>();
        toxicList = new ArrayList<Integer>();
        
        String path = "molecules.groundTruth";
        BufferedReader br = null;
        String line = "";
        String txtSplitBy = " ";
        
        try {
            br = new BufferedReader(new FileReader(path));
            
            while ((line = br.readLine()) != null){
                
                String[] data = line.split(txtSplitBy);
                moleculeNames.add(data[0]);
                toxicList.add(Integer.parseInt(data[1]));
                
                
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
    }
    
    /**
     * to read molecules.sdf and split it
     */
    public void read_sdf_split(){
        List<String> molLines = new ArrayList<String>();
        
        String path = "molecules.sdf";
        BufferedReader br = null;
        String line = "";
        int i = 0;
        
        try {
            br = new BufferedReader(new FileReader(path));
            
            while ((line = br.readLine()) != null){
                
                molLines.add(line);
                
                if (line.equals("$$$$")) { // write new sdf
                    BufferedWriter wr = null;
                    
                    String[] temp = molLines.get(0).split(";");
                    StringBuilder builder = new StringBuilder(temp[0]);
                    builder.append(temp[1]);
                    String name = builder.substring(1);
                    
                    wr = new BufferedWriter(new FileWriter("SDF"+File.separator+i+"_"+name+".sdf"));
                    i++;
                    for (String str : molLines) {
                        wr.write(str);
                        wr.newLine();
                    }
                    wr.close();
                    
                    molLines.clear();
                }
                
                
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
    }
    
    /**
     * to create training.sdf with all molecules with an even index
     */
    public void createTrainingSDF() {
        BufferedWriter wr = null;
        String line = "";
        
        try {
            wr = new BufferedWriter(new FileWriter("SDF"+File.separator+"training.sdf"));
            
            for (int i = 0; i < moleculeNames.size(); i+=2) {
                BufferedReader br = new BufferedReader(new FileReader("SDF"+File.separator+i+"_"+moleculeNames.get(i)+".sdf"));
                while ((line = br.readLine()) != null){
                    wr.write(line);
                    wr.newLine();
                }
                br.close();
            }
            
            wr.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    /**
     * create with sdf-file and perl script a gsp file
     * 
     */
    public void sdfToGsp(String sdfPath, String gspPath) {
        String command = "perl sdf2gsp.pl " + sdfPath + " > " + gspPath;
        if (isWindowsSystem()) {
            //System.out.println("Windows System");
            printWindowsCommand(command);
        } else if (isLinuxSystem()) {
            //System.out.println("Linux System");
            printLinuxCommand(command);
        } else {
            System.out.println("Unkown System");
            System.exit(1);
        }
    }
    
    /**
     * checks if its a Windows System
     * @return
     */
    public static boolean isWindowsSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.indexOf("windows") <= 0;
    }
    
    /**
     * checks if its a Linux System
     * @return
     */
    public static boolean isLinuxSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.indexOf("linux") <= 0;
    }
    
    /**
     * printWindowsCommand
     * @param command
     */
    public static void printWindowsCommand(String command) {
        try {
            Process prcs = Runtime.getRuntime().exec("cmd /c " + command);
            prcs.waitFor(); // slows it down, but avoids Runtime Conditions
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * printLinuxCommand
     * @param command
     */
    public static void printLinuxCommand(String command) {
        try {
            Process prcs = Runtime.getRuntime().exec(command);
            prcs.waitFor(); // slows it down, but avoids Runtime Conditions
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * create all gsp files
     */
    public void allSdfToGsp() {
        sdfToGsp("SDF"+File.separator+"training.sdf", "GSP"+File.separator+"training.gsp");
        
        for (int i = 0; i < moleculeNames.size(); i++) {
            sdfToGsp("SDF"+File.separator+i+"_"+moleculeNames.get(i)+".sdf", "GSP"+File.separator+i+"_"+moleculeNames.get(i)+".gsp");
        }
    }
    
    /**
     * execute gSpan Command
     * @param maxEdges
     * @param minSup
     * @param input
     * @param output
     * @param logPath
     */
    public void gSpanCommand(int maxEdges, int minSup, String input, String output, String logPath) {
        String command = "gSpan -m " + maxEdges + " " + minSup + " " + input + " " + output + " > " + logPath;
        if (isWindowsSystem()) {
            //System.out.println("Windows System");
            printWindowsCommand(command);
        } else if (isLinuxSystem()) {
            //System.out.println("Linux System");
            printLinuxCommand(command);
        } else {
            System.out.println("Unknown System");
            System.exit(1);
        }
    }
    
    /**
     * checks if subgraphs in molecules or not; for every molecule (dated)
     */
//    public void checkSubOccurrence() {
//        String currentSubs = "gSpanSubgraphs"+File.separator+"currentSub.txt";
//        subsList = new ArrayList<ArrayList<Boolean>>();
//        
//        
//        for (int i = 0; i < moleculeNames.size(); i++) {
//            System.out.println(i);
//            // create subgraphs of i-th molecule
//            // maxEdges here hard coded with 10, could be read out of trainingLog.txt
//            gSpanCommand(10, 1, "GSP"+File.separator+i+"_"+moleculeNames.get(i)+".gsp", currentSubs, "gSpanSubgraphs"+File.separator+"currentLog.txt");
//            // checks which searched subgraphs in the i-th molecule
//            subsList.add( containsSubs("gSpanSubgraphs"+File.separator+"trainingSub.txt", currentSubs) );
//        }
//        
//    }
    
    /**
     * checks if subgraphs in a certain molecule (dated)
     * @param wantedSubs
     * @param moleculeSubs
     * @return
     */
    public ArrayList<Boolean> containsSubs(String wantedSubs, String moleculeSubs) {
        ArrayList<Boolean> containList = new ArrayList<Boolean>();
        BufferedReader br = null;
        String line = "";
        
        try {
            br = new BufferedReader(new FileReader(wantedSubs));
            ArrayList<String> DFS = new ArrayList<String>();
            // while-loop to go through whole trainingSub.txt
            while ((line = br.readLine()) != null){
                
                if (!line.equals("") && !line.startsWith("(")) { // line with index and occurrence number
                    continue;
                }
                
                if (line.startsWith("(")) { // DFS
                    DFS.add(line);
                    continue;
                }
                
                if (line.equals("") && DFS.size() > 0) { // first empty line after DFS
                    containList.add(containsDFS(DFS, moleculeSubs));
                    DFS.clear();
                }
                
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
        
        return containList;
    }
    
    /**
     * checks if gSpan-Result contains certain DFS (dated)
     * @param DFS
     * @param moleculeSubs
     * @return
     */
    public boolean containsDFS(ArrayList<String> DFS, String moleculeSubs) {
        BufferedReader br = null;
        String line = "";
        int i = 0;
        
        try {
            br = new BufferedReader(new FileReader(moleculeSubs));
            // while-loop to go through whole currentSub.txt
            while ((line = br.readLine()) != null){
                
                if (line.equals(DFS.get(i))) {
                    i++;
                    if (i == DFS.size()) {
                        return true;
                    }
                    continue;
                }
                i = 0;
                
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
        
        return false;
        
    }
    
    /**
     * checks if subgraphs in molecules or not; for every molecule (updated)
     */
    public void newCheckSubOccurrence(int maxKanten) {
        String currentSubs = "gSpanSubgraphs"+File.separator+"currentSub.txt";
        subsList = new ArrayList<ArrayList<Boolean>>();
        
        trainingDfsList = createDfsList("gSpanSubgraphs"+File.separator+"trainingSub.txt");
        
        for (int i = 0; i < moleculeNames.size(); i++) {
            System.out.println(i);
            // create subgraphs of i-th molecule
            // maxEdges here hard coded with 10, could be read out of trainingLog.txt
            gSpanCommand(maxKanten, 1, "GSP"+File.separator+i+"_"+moleculeNames.get(i)+".gsp", currentSubs, "gSpanSubgraphs"+File.separator+"currentLog.txt");
            
            // checks which searched subgraphs in the i-th molecule
            ArrayList<Boolean> containList = new ArrayList<Boolean>();
            ArrayList<ArrayList<String>> currentDfsList = createDfsList(currentSubs);
            for (ArrayList<String> dfs : trainingDfsList) {
                containList.add(currentDfsList.contains(dfs));
            }
            
            // to clear currentSub.txt because there are 2 sdfs which create no gSpan Result
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(currentSubs));
                bw.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            subsList.add( containList );
        }
        
    }
    
    /**
     * creates dfsList
     */
    public ArrayList<ArrayList<String>> createDfsList(String gSpanResult) {
        ArrayList<ArrayList<String>> dfsList = new ArrayList<ArrayList<String>>();
        
        BufferedReader br = null;
        String line = "";
        
        try {
            br = new BufferedReader(new FileReader(gSpanResult));
            ArrayList<String> DFS = new ArrayList<String>();
            // while-loop to go through whole trainingSub.txt
            while ((line = br.readLine()) != null){
                
                if (!line.equals("") && !line.startsWith("(")) { // line with index and occurrence number
                    continue;
                }
                
                if (line.startsWith("(")) { // DFS
                    DFS.add(line);
                    continue;
                }
                
                if (line.equals("") && DFS.size() > 0) { // first empty line after DFS
                    dfsList.add(DFS);
                    DFS = new ArrayList<String>();
                }
                
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
        return dfsList;
    }
    
    /**
     * to write the arffs
     */
    public void writeArff() {
        // write training arff
        BufferedWriter wr = null;
        try {
            wr = new BufferedWriter(new FileWriter("Arff"+File.separator+"moleculeTraining.arff"));
            wr.write("@relation molecules");
            wr.newLine();
            wr.newLine();
            wr.write("@attribute name string");
            wr.newLine();
            wr.write("@attribute toxic {0, 1}");
            wr.newLine();
            
            for (int i = 0; i < subsList.get(0).size(); i++) { // write attributes for every checked subgraph
                wr.write("@attribute subgraph"+i+" {0, 1}");
                wr.newLine();
            }
            
            wr.newLine();
            wr.write("@data");
            wr.newLine();
            
            for (int i = 0; i < moleculeNames.size(); i+=2) {
                wr.write(moleculeNames.get(i));
                wr.write(",");
                wr.write(toxicList.get(i).toString());
                for (int j = 0; j < subsList.get(i).size(); j++) {
                    wr.write(",");
                    wr.write(subsList.get(i).get(j)? "1" : "0");
                }
                wr.newLine();
            }
            
            wr.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // write test arff
        wr = null;
        try {
            wr = new BufferedWriter(new FileWriter("Arff"+File.separator+"moleculeTest.arff"));
            wr.write("@relation molecules");
            wr.newLine();
            wr.newLine();
            wr.write("@attribute name string");
            wr.newLine();
            wr.write("@attribute toxic {0, 1}");
            wr.newLine();
            
            for (int i = 0; i < subsList.get(0).size(); i++) { // write attributes for every checked subgraph
                wr.write("@attribute subgraph"+i+" {0, 1}");
                wr.newLine();
            }
            
            wr.newLine();
            wr.write("@data");
            wr.newLine();
            
            for (int i = 1; i < moleculeNames.size(); i+=2) { // difference to training: starts with i=1
                wr.write(moleculeNames.get(i));
                wr.write(",");
                wr.write(toxicList.get(i).toString());
                for (int j = 0; j < subsList.get(i).size(); j++) {
                    wr.write(",");
                    wr.write(subsList.get(i).get(j)? "1" : "0");
                }
                wr.newLine();
            }
            
            wr.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * to create all.gsp with all molecules; analog to find molecule.gsp
     * just to find the 2 defective molecules
     * -> Renatus1998jmc_6b (223) and Renatus1998jmc_6a (226)
     */
    public void creategsp() {
        BufferedWriter wr = null;
        String line = "";
        
        try {
            wr = new BufferedWriter(new FileWriter("GSP"+File.separator+"all.gsp"));
            
            for (int i = 0; i < moleculeNames.size(); i++) {
                BufferedReader br = new BufferedReader(new FileReader("GSP"+File.separator+i+"_"+moleculeNames.get(i)+".gsp"));
                while ((line = br.readLine()) != null){
                    wr.write(line);
                    wr.newLine();
                }
                br.close();
            }
            
            wr.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        
        ToxicMolecules tm = new ToxicMolecules();
        tm.input_groundTruth(); // read molecules.groundTruth and save name and toxicity in lists
        tm.read_sdf_split(); // take molecules.sdf and create for each molecule a seperate sdf
        tm.createTrainingSDF(); // take every second molecule and create a sdf for a training set
        tm.allSdfToGsp(); // create gsp out of all new sdf
        // createTrainingsSubgraphs
        // maxEdges 10 to avoid to large files at occurrence checking later
        // minSup 75 because its nearly the half
        tm.gSpanCommand(10, 75, "GSP"+File.separator+"training.gsp", "gSpanSubgraphs"+File.separator+"trainingSub.txt", "gSpanSubgraphs"+File.separator+"trainingLog.txt");
        tm.newCheckSubOccurrence(10); // check the occurrence of training subgraphs in each molecule and write it in lists
        tm.writeArff(); // create the arff file for Weka
        
        long end = System.currentTimeMillis();
        System.out.println("Run-time: " + (end-start) + " ms");
        System.out.println();
        
        System.out.println("done");

    }

}
