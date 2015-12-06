import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class EMAlgorithm {
	
	private ArrayList<Double> csv;
	
	private ArrayList<ArrayList<Double>> init_coords = new ArrayList<ArrayList<Double>>();

	private ArrayList<Double> weights1 = new ArrayList<Double>(); //Gewichte zum Tupel 1
	private ArrayList<Double> weights2 = new ArrayList<Double>(); //Gewichte zum Tupel 2
	
	/**
	 * Splitted an gegebenem String und speichert in ArrayList<Double>
	 * @param path
	 * @param splitBy = String an dem gesplitted wird.
	 * @return ArrayList<Double> data
	 */
	public ArrayList<Double> parseCSV(String path, String splitBy){
        
		ArrayList<Double> data = new ArrayList<Double>();
        BufferedReader br = null;
        String line = "";
        //String csvSplitBy = splitBy;
        
        try {
        	br = new BufferedReader(new FileReader(path));
        	
        	while ((line = br.readLine()) != null){
        		String[] splittedLine = line.split(splitBy);
        		Double d = Double.parseDouble(splittedLine[1]);
        		data.add(d);
        	}
        
        //Setzt das Ergebnis nochmal global.
        //csv = data; 
        	
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
        
		return data;
	}
	
	/**
	 * Computes mu for csv with a fixed weight for every value.
	 * Formel aus der Vorlesung 5, Folie 28.
	 * 
	 * @param csv_data
	 * @param weights
	 * @return mu
	 */
	public double calculateMu(ArrayList<Double> csv_data, ArrayList<Double> weights){
		
		double zaehler = 0;
		double nenner = 0;
		
		//Nenner
		for(int i = 0; i < csv_data.size(); i++){
			nenner += weights.get(i);
		}
		
		//Rechnet alle Einträge zusammen
		for(int i = 0; i<csv_data.size(); i++){
			zaehler += (csv_data.get(i)*weights.get(i));
		}
		
		//returns mu
		return (zaehler/nenner);
	}
	
	/**
	 * Computes sigma^2 for a fixed weight for every value.
	 * Formel aus der Vorlesung 5, Folie 28.
	 * Nutzt calculateMu(x,y) und this.globalNenner.
	 * 
	 * @param csv_data
	 * @param weight
	 * @return sigma^2
	 */
	public double computeSigma2(ArrayList<Double> csv_data, ArrayList<Double> weights){

		double sum = 0;
		double nenner = 0;
		double mu = calculateMu(csv_data, weights);
		
		//Nenner
		for(int i = 0; i < csv_data.size(); i++){
			nenner += weights.get(i);
		}
		
		for(int i = 0; i<csv_data.size(); i++){
			sum += weights.get(i)*Math.pow((csv_data.get(i)-mu), 2);
		}
		
		return (sum/nenner);
	}
	
	/**
	 * Computes sigma for csvs with a fixed weight for every value.
	 * 
	 * @param csv_data
	 * @param weight
	 * @return sigma
	 */
	public double getSigma(ArrayList<Double> csv_data, ArrayList<Double> weights){
		return (Math.sqrt(computeSigma2(csv_data, weights)));
	}
	
	/**
	 * Berechnet die Potenz für das e. Fügt negatives Vorzeichen hinzu.
	 * 
	 * @param x
	 * @param mu
	 * @param sigmaSquared
	 * @return negative Potenz von e
	 */
	public double computePotenz(double x, double mu, double sigmaSquared){
		
		double a = ((Math.pow((x-mu), 2) / (2*sigmaSquared)));
		return (a - 2*a);
	}
	
	/**
	 * Berechnet die Formel f(x, mu, sigma)
	 * 
	 * @param x
	 * @param mu
	 * @param sigma
	 * @return f(x, mu, sigma)
	 */
	public double computeF(double x, double mu, double sigma){
		double b;
		double a = 1 / (Math.sqrt(2 * Math.PI) * sigma);
		b = a*Math.pow(Math.E, computePotenz(x, mu, (sigma*sigma)));
		
		return b;
	}
	
	
	/**
	 * Berechnet log-likelihood
	 * 
	 * @param weight = die Gewichtung zu jedem element aus data
	 * @param data = ArrayList mit Elementen aus der csv
	 * @param P_k = Wahrscheinlichkeit pro Cluster. Wir haben zwei Cluster, mit jeweils 0.5
	 * @param coords = Startparameter, mittelpunkt der beiden Cluster.
	 * @return
	 */
	public double logLikelihood(ArrayList<Double> weight, ArrayList<Double> data, double P_k, ArrayList<ArrayList<Double>> coords){
		double a = 0;
		double b = 0;
		
		int numCluster = coords.size();
		
		for(int i = 0; i < data.size(); i++){
			for(int k = 0; k < numCluster; k++){
				a += P_k*computeF(data.get(i), coords.get(k).get(0), coords.get(k).get(1));
			}
			b += Math.log(a);
		}

		return b;
		
//		for(int i = 0; i < data.size(); i++){
//			for(int k = 0; k < numCluster; k++){
//				a += P_k*computeF(data.get(i), coords.get(k).get(0), coords.get(k).get(1));
//			}
//			b = b*a;
//		}
//		
//		b = Math.log(b);
//		
//		return b;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EMAlgorithm e = new EMAlgorithm();
		e.csv=e.parseCSV("C:\\Users\\Yorrick\\workspace\\EMAlgorithm\\src\\data.csv", "   ");
		
		//Initialbelegung der beiden Tupel wird gebildet.
		//Der EM speichert hier die Tupel der jeweiligen Iteratio.n
		double mu1 = 1;
		double sigma1 = 1;
		double mu2 = 4;
		double sigma2 = 1;
		
		//Befühle beide Gewichtslisten mit Initialgewichten
		for(int i = 0; i<e.csv.size(); i++){
			e.weights1.add(0.5);
			e.weights2.add(0.5);
		}
		
		//Bildung der Coord Tupel für die Log-Likelihood
		ArrayList<Double> c1 = new ArrayList();
		c1.add(mu1);
		c1.add(sigma1);
		
		ArrayList<Double> c2 = new ArrayList();
		c2.add(mu2);
		c2.add(sigma2);
		
		e.init_coords.add(c1);
		e.init_coords.add(c2);
		
		double log = e.logLikelihood(e.weights1, e.csv, 0.5, e.init_coords);
		System.out.println("log-likelihood: " + log);
		
		
		//Beginn des EM, wird 20 mal iteriert.
		for(int j = 0; j < 20; j++){
			//E-Phase
			//calculation of new weights
			
			//Listen in die die neuen Gewichte geschrieben werden.
			ArrayList<Double> x = new ArrayList();
			ArrayList<Double> y = new ArrayList();
			
			for(int i = 0; i < e.csv.size(); i++){
				
//				System.out.println(e.computeF(e.data.get(i), mu1, sigma1));
				
				//Berechnet die neuen Gewichte
				double w1 = (e.computeF(e.csv.get(i), mu1, sigma1) * 0.5) / log;
				double w2 = (e.computeF(e.csv.get(i), mu2, sigma2) * 0.5) / log;
				
//				double w1 = (e.computeF(e.data.get(i), mu1, sigma1) * 0.5) / (e.weights1.get(i));
//				double w2 = (e.computeF(e.data.get(i), mu2, sigma2) * 0.5) / (e.weights2.get(i));
//				double w1 = (e.computeF(e.data.get(i), 1, 1) * 0.5) / (e.computeF(e.data.get(i), 1, 1)*0.5 + e.computeF(e.data.get(i),4,1)*0.5);
//				double w2 = (e.computeF(e.data.get(i), 4, 1) * 0.5) / (e.computeF(e.data.get(i), 1, 1)*0.5 + e.computeF(e.data.get(i),1,1)*0.5);
//				System.out.println("w1: " + w1 + ", " + w2 + " sum: " + (w1+w2));
				
				//Fügt sie in die Listen ein
				x.add(w1);
				y.add(w2);
				
			}
			
			//Überschreibt die alten Gewichte
			e.weights1 = x;
			e.weights2 = y;
			
			//M-Phase
			//calculation of coords
			
			//mu
			mu1 = e.calculateMu(e.csv, e.weights1);
			mu2 = e.calculateMu(e.csv, e.weights2);
			//sigma
			sigma1 =e.getSigma(e.csv, e.weights1);
			sigma2 =e.getSigma(e.csv, e.weights2);
			
			System.out.println((j+1) + ". (mu1, sigma1): " + mu1 + ", " + sigma1);
			System.out.println((j+1) + ". (mu2, sigma2): " + mu2 + ", " + sigma2);
		
		}
		
//		System.out.println("mu = " + e.calculateMu(e.csv, 0.5));
//		System.out.println("Sigma^2 = " + e.computeSigma2(e.csv, 0.5));
//		System.out.println("Sigma = " + e.getSigma(e.csv, 0.5));
//		System.out.println("Potenz von e: " + e.computePotenz(2, 3, 4));
		
//		System.out.println("ClusterProb von 15.2393: " + e.computeF(15.2393, 1, 1));
		
		
	}
	
	
}
