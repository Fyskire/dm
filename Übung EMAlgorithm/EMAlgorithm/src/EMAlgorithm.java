import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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
		weights1.addAll(data); //Dient nur dazu, dass weights1 gleich viele Einträge wie data hat und initialisiert ist.
		weights2.addAll(data);
		
		try{
		PrintWriter writer = new PrintWriter("results.txt", "UTF-8");	
		
		for(int i = 0; i < numberOfIterations; i++){
			eStep();
			mStep();
			
			//writes results to file
			writer.println((i+1) + ". Iteration: Cluster A (" + mu1 + ", " + sigma1 + "); Cluster B (" + mu2 + ", " + sigma2 + ")" + " logLikelihood: " + logLikelihood());	
			System.out.println((i+1) + ". Iteration: Cluster A (" + mu1 + ", " + sigma1 + "); Cluster B (" + mu2 + ", " + sigma2 + ")" + " logLikelihood: " + logLikelihood());
		}
		
		writer.close();
		} catch(IOException e1) {
	        System.out.println("Error during writing");
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
	 * Siehe VL5, Folie 25
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
		
		//setze vorgegebene Werte
		e.setDefaultValues();
		
		//Starte Algorithmus
		e.runAlgorithm(e.iterations, "C:\\Users\\Yorrick\\workspace\\EMAlgorithm\\src\\data.csv", "   ");
	}
	
	
}
