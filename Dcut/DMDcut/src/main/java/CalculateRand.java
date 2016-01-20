import java.util.ArrayList;
import java.util.HashSet;

public class CalculateRand {

    
    public static void calculateRand(ArrayList<HashSet<Integer>> cutClusters1, ArrayList<HashSet<Integer>> cutClusters2, int numOfNodes){
        
        // calculate RI
        int numOfEl = numOfNodes;
        int N1 = 0;
        int N2 = 0;
        int N3 = 0;
        int N4 = 0;
        for (int i = 1; i < numOfEl; i++) {
            for (int j = i+1; j <= numOfEl; j++) {
                
                boolean same1 = false;
                for(HashSet<Integer> cluster : cutClusters1){
                    if (cluster.contains(i) && cluster.contains(j)) {
                        same1 = true;
                        break;
                    }
                }
                
                boolean same2 = false;
                for(HashSet<Integer> cluster : cutClusters2){
                    if (cluster.contains(i) && cluster.contains(j)) {
                        same2 = true;
                        break;
                    }
                }
                
                if (same1) {
                    if (same2) {
                        N4++;
                    } else {
                        N2++;
                    }
                } else {
                    if (same2) {
                        N3++;
                    } else {
                        N1++;
                    }
                }
            }
        }
        
        double randIndex = (N1 + N4) / (double) (N1+N2+N3+N4);
        System.out.println("RI = " + randIndex);
        
        //calculate ARI
        double numOfCom = (double)numOfEl*(numOfEl-1)/2;
        double ari = (numOfCom * (N4+N1) - ((N4+N2)*(N4+N3) + (N3+N1)*(N2+N1))) /
                ( numOfCom*numOfCom -  ((N4+N2)*(N4+N3) + (N3+N1)*(N2+N1)));
        System.out.println("ARI = " + ari);
        
        
        // calculate NMI
        double ho = 0;
        for (HashSet<Integer> clusterK : cutClusters1) {
            double hoTemp = clusterK.size() / (double) numOfEl;
            hoTemp *= Math.log(hoTemp);
            ho += hoTemp;
        }
        ho *= -1;
        
        double hc = 0;
        for (HashSet<Integer> clusterJ : cutClusters2) {
            double hcTemp = clusterJ.size() / (double) numOfEl;
            hcTemp *= Math.log(hcTemp);
            hc += hcTemp;
        }
        hc *= -1;
        
        double ioc = 0;
        for (HashSet<Integer> clusterK : cutClusters1) {
            for (HashSet<Integer> clusterJ : cutClusters2) {
                HashSet<Integer> intersection = new HashSet<Integer>();
                intersection.addAll(clusterK);
                intersection.retainAll(clusterJ);
                double iocTemp = intersection.size() / (double) numOfEl;
                iocTemp *= Math.log( numOfEl*intersection.size() / (double) (clusterK.size()*clusterJ.size()) );
                if (!Double.isNaN(iocTemp))
                    ioc += iocTemp;
            }
        }
        
        double nmi = ioc / ((ho+hc)/2);
        System.out.println("NMI = " + nmi);
        
        
        // calculate Purity
        ArrayList<Integer> maxList = new ArrayList<Integer>();
        for (HashSet<Integer> cluster : cutClusters1) {
            int max = Integer.MIN_VALUE;
            for (HashSet<Integer> classification : cutClusters2) {
                HashSet<Integer> intersection = new HashSet<Integer>();
                intersection.addAll(cluster);
                intersection.retainAll(classification);
                if (intersection.size() > max) {
                    max = intersection.size();
                }
            }
            maxList.add(max);
        }
        double purity = 0;
        for (int i : maxList) {
            purity += i;
        }
        purity /= numOfEl;
        System.out.println("Cluster Purity = " + purity);
    }
    
    
    private static void printClusters(ArrayList<ArrayList<Integer>> clusters){
        //Output
        for(ArrayList<Integer> cluster : clusters){
            System.out.print("{");
            for(int v : cluster){
                System.out.print(v+" ");
            }
            System.out.print("}\n");
        }
    }

}
