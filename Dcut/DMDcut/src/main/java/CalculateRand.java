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
        
        //calculate NMI
        // I(U,V)
        double iuv = 0;
        double hu = 0;
        double hv = 0;
        for(HashSet<Integer> clusterI : cutClusters1){
            //P(i)
            double pi = clusterI.size() / (double)numOfEl;
            hu += pi * Math.log(pi);
            for(HashSet<Integer> clusterJ : cutClusters2){
                //P(j)
                double pj = clusterJ.size() / (double)numOfEl;
                hv += pj * Math.log(pj);
                // P(i,j)
                ArrayList<Integer> clusterInter = new ArrayList<Integer>();
                clusterInter.addAll(clusterI);
                clusterInter.retainAll(clusterJ);
                double pij = clusterInter.size() / (double)numOfEl;
                if ( pij > 0)
                    iuv += pij * Math.log(pij/(pi*pj));
            }
        }
        hu *= -1;
        hv *= -1;
        
        double nmi = iuv / Math.sqrt(hu*hv);
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
