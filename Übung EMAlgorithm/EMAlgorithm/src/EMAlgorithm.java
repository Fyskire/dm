import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class EMAlgorithm {
	
	//enthält eingelesene Daten
	private ArrayList<Double> data;
	
	private ArrayList<ArrayList<Double>> init_coords = new ArrayList<ArrayList<Double>>(); //Kann gelöscht werden?
	
	private double mu1, mu2, sigma1, sigma2, pA, pB, log1, log2;

	private int iterations;
	
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
        System.out.println("The csv has " + data.size() + " entries.");
        
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
	 * Berechnet das e mit seiner Potenz
	 * @param x
	 * @param mu
	 * @param sigmaSquare
	 * @return e^-(x)
	 */
	public double computeE(double x, double mu, double sigma){
		double a = - ((x-mu)*(x-mu)) / (2*sigma*sigma);
		return (Math.exp(a));
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
		double a = computeE(x,mu, sigma) / (Math.sqrt(2 * Math.PI) * sigma);
		return a;
	}
	
	
	/**
	 * Berechnet log-likelihood //Löschen
	 * 
	 * @param weight = die Gewichtung zu jedem element aus data
	 * @param data = ArrayList mit Elementen aus der csv
	 * @param P_k = Wahrscheinlichkeit pro Cluster. Wir haben zwei Cluster, mit jeweils 0.5
	 * @param coords = Startparameter, mittelpunkt der beiden Cluster.
	 * @return
	 */
//	public double logLikelihood(ArrayList<Double> weight, ArrayList<Double> data, double P_k, ArrayList<ArrayList<Double>> coords){
//		double a = 0;
//		double b = 0;
//		
//		int numCluster = coords.size();
//		
//		for(int i = 0; i < data.size(); i++){
//			for(int k = 0; k < numCluster; k++){
//				a += P_k*computeF(data.get(i), coords.get(k).get(0), coords.get(k).get(1));
//			}
//			b += Math.log(a);
//		}
//
//		return b;
//	}
	
	/**
	 * Berechnet log-likelihood wie in VL 5, Folie 29 für 2 Cluster
	 * @return log-likelihood
	 */
	public double logLikelihood(){
		double erg = 0.0;
		
		for(int i = 0; i < data.size(); i++){
			double x = data.get(i);
			double a = computeF(x, mu1, sigma1);
			double b = computeF(x, mu2, sigma2);
			
			erg += Math.log(pA * a + pB * b);
		}
		
		return erg;	
	}
	
	
	/**
	 * Setzt die in der Aufgabe vorgegebenen Werte
	 */
	public void setDefaultValues(){
		iterations = 20;
		mu1 = 1.0;
		sigma1 = 1.0;
		mu2 = 4.0;
		sigma2 = 1.0;
		
		pA = 0.5;
		pB = 0.5;	
	}
	
	public void runAlgorithm(int numberOfIterations, String path, String split){
		System.out.println("Starte Algorithmus, mit " + numberOfIterations + " Iterationen.");
		data = parseCSV(path, split);
		weights1 = data; //Dient nur dazu, dass weights1 gleich viele Einträge wie data hat und initialisiert ist.
		weights2 = data;
		for(int i = 0; i < numberOfIterations; i++){
			eStep();
			mStep();
			System.out.println((i+1) + ". Iteration: Cluster A (" + mu1 + ", " + sigma1 + "); Cluster B (" + mu2 + ", " + sigma2 + ")" + " ll: " + logLikelihood());
		}
		System.out.println("Algorithmus beendet.");
	}
	
	/**
	 * Berechnet die Wahrscheinlichkeit w_i, mit der Element x in Cluster c_i liegt.
	 * Für Formeln siehe VL5, Folie 25
	 */
	public void eStep(){
		for(int i = 0; i < data.size(); i++){
			double x_i = data.get(i);
			double PrXzuA = computeF(x_i, mu1, sigma1);
			double PrXzuB = computeF(x_i, mu2, sigma2);
			double PrX = PrXzuA * pA + PrXzuB * pB;
			double w_i =  PrXzuA * pA / PrX;
			double w_i2 = PrXzuB * pB /PrX;
			
			//System.out.println("Sollte 1 sein: w_i + w_i2);
			
			weights1.set(i, w_i);//hier mal mit debugger beobachten
			weights2.set(i, (1-w_i));
		}
	}
	
	/**
	 * Berechner die neuen mus und sigmas
	 */
	public void mStep(){
		
		mu1 = 0.0;
        mu2 = 0.0;
        sigma1 = 0.0;
        sigma2 = 0.0;
		
		mu1 = calculateMu(data, weights1);
		mu2 = calculateMu(data, weights2);
		
		sigma1 = getSigma(data, weights1);
		sigma2 = getSigma(data, weights2);	
	}
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EMAlgorithm e = new EMAlgorithm();
//		e.data=e.parseCSV("C:\\Users\\Yorrick\\workspace\\EMAlgorithm\\src\\data.csv", "   ");
		
		//setze vorgegebene Werte
		e.setDefaultValues();
		
		//Starte Algorithmus
		e.runAlgorithm(20, "C:\\Users\\Yorrick\\workspace\\EMAlgorithm\\src\\data.csv", "   ");
		
		
		
		
		
//		//Initialbelegung der beiden Tupel wird gebildet.
//		//Der EM speichert hier die Tupel der jeweiligen Iteration
//		double mu1 = 1;
//		double sigma1 = 1;
//		double mu2 = 4;
//		double sigma2 = 1;
//		
//		
//		//Befühle beide Gewichtslisten mit Initialgewichten
//		for(int i = 0; i<e.data.size(); i++){
//			e.weights1.add(0.5);
//			e.weights2.add(0.5);
//		}
//		
//		//Bildung der Coord Tupel für die Log-Likelihood
//		ArrayList<Double> c1 = new ArrayList();
//		c1.add(mu1);
//		c1.add(sigma1);
//		
//		ArrayList<Double> c2 = new ArrayList();
//		c2.add(mu2);
//		c2.add(sigma2);
//		
//		e.init_coords.add(c1);
//		e.init_coords.add(c2);
//		
//		System.out.println("Initialbelegung ist: (" + e.init_coords.get(0).get(0) + ", " + e.init_coords.get(0).get(1) 
//				+ "); (" + e.init_coords.get(1).get(0) + ", " + e.init_coords.get(1).get(1) + ")");
//		
//		double log = e.logLikelihood(e.weights1, e.data, 0.5, e.init_coords);
//		System.out.println("log-likelihood: " + log);
//		
//		
//		//Beginn des EM, wird 20 mal iteriert.
//		for(int j = 0; j < 20; j++){
//			//E-Phase
//			//calculation of new weights
//			
//			//Listen in die die neuen Gewichte geschrieben werden.
//			ArrayList<Double> x = new ArrayList();
//			ArrayList<Double> y = new ArrayList();
//			
//			for(int i = 0; i < e.data.size(); i++){
//				
////				System.out.println(e.computeF(e.data.get(i), mu1, sigma1));
//				
//				//Berechnet die neuen Gewichte
//				double w1 = (e.computeF(e.data.get(i), mu1, sigma1) * 0.5) / log;
//				double w2 = (e.computeF(e.data.get(i), mu2, sigma2) * 0.5) / log;
//				
//				//Fügt sie in die Listen ein
//				x.add(w1);
//				y.add(w2);
//				
//			}
//			
//			//Überschreibt die alten Gewichte
//			e.weights1 = x;
//			e.weights2 = y;
//			
//			//M-Phase
//			//calculation of coords
//			
//			//mu
//			mu1 = e.calculateMu(e.data, e.weights1);
//			mu2 = e.calculateMu(e.data, e.weights2);
//			//sigma
//			sigma1 =e.getSigma(e.data, e.weights1);
//			sigma2 =e.getSigma(e.data, e.weights2);
//			
//			System.out.println((j+1) + ". (mu1, sigma1): " + mu1 + ", " + sigma1);
//			System.out.println((j+1) + ". (mu2, sigma2): " + mu2 + ", " + sigma2);
//		
//		}
		
//		System.out.println("mu = " + e.calculateMu(e.csv, 0.5));
//		System.out.println("Sigma^2 = " + e.computeSigma2(e.csv, 0.5));
//		System.out.println("Sigma = " + e.getSigma(e.csv, 0.5));
//		System.out.println("Potenz von e: " + e.computePotenz(2, 3, 4));
		
//		System.out.println("ClusterProb von 15.2393: " + e.computeF(15.2393, 1, 1));
		
		
	}
	
	
}
