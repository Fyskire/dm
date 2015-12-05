import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class EMAlgorithm {
	
	private ArrayList<Double> csv;
	private double globalNenner; 
	
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
	 * @param weight
	 * @return mu
	 */
	public double calculateMu(ArrayList<Double> csv_data, double weight){
		
		double zaehler = 0;
		double nenner;
		
		nenner = weight * (csv_data.size());
		
		//speichert nenner global, optimiert die laufzeit?
		//muss auskommentiert werden, falls Methoder wo anders hin übernommen wird.
		this.globalNenner = nenner;
		
		//Rechnet alle Einträge zusammen
		for(int i = 0; i<csv_data.size(); i++){
			zaehler += (csv_data.get(i));
		}
		
		//multipliziert die zusammen addierten csvs mit dem Gewicht
		zaehler = zaehler*weight;
		
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
	public double computeSigma2(ArrayList<Double> csv_data, double weight){

		double sum = 0;
		double mu = calculateMu(csv_data, weight);
		
		for(int i = 0; i<csv_data.size(); i++){
			sum += Math.pow((csv_data.get(i)-mu),2);
		}
		
		sum = sum*weight;
		
		return (sum/this.globalNenner);
	}
	
	/**
	 * Computes sigma for csvs with a fixed weight for every value.
	 * 
	 * @param csv_data
	 * @param weight
	 * @return sigma
	 */
	public double getSigma(ArrayList<Double> csv_data, double weight){
		return (Math.sqrt(computeSigma2(csv_data, weight)));
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
		
		double a = 1 / (Math.sqrt(2 * Math.PI) * sigma);
		a = a*Math.pow(Math.E, computePotenz(x, mu, (sigma*sigma)));
		
		return a;
	}
	
	public void startEM(double muA, double sigmaA, double muB, double sigmaB, String path){
		
		this.csv = parseCSV(path, "   ");
		
		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EMAlgorithm e = new EMAlgorithm();
		e.csv=e.parseCSV("U:\\eclipse\\EMAlg\\src\\main\\java\\data.csv", "   ");
		System.out.println("mu = " + e.calculateMu(e.csv, 0.5));
		System.out.println("Sigma^2 = " + e.computeSigma2(e.csv, 0.5));
		System.out.println("Sigma = " + e.getSigma(e.csv, 0.5));
		System.out.println("Potenz von e: " + e.computePotenz(2, 3, 4));
		
		System.out.println("ClusterProb von 15.2393: " + e.computeF(15.2393, 1, 1));
		
		
	}

}
