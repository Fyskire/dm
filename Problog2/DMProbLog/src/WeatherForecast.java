import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class WeatherForecast {
    ArrayList<ArrayList<String[]>> data;
    
    /**
     * to input wetterdaten_Wettermessung.csv
     */
    public void input() {
        
        data = new ArrayList<ArrayList<String[]>>();
        BufferedReader br = null;
        String line = "";
        
        try {
            br = new BufferedReader(new FileReader("wetterdaten_Wettermessung.csv"));
            line = br.readLine(); //skip first line
            int stations_ID = 0;
            ArrayList<String[]> station_data = new ArrayList<String[]>(); // contains all day_data for one station
            
            while ((line = br.readLine()) != null) {
                line = line.replace(",", ".");
                String[] day_data = line.split(";"); // input data of one day
                if (Integer.parseInt(day_data[0]) != stations_ID) {
                    stations_ID = Integer.parseInt(day_data[0]);
                    data.add(station_data);
                    station_data = new ArrayList<String[]>();
                }
                station_data.add(day_data);
            }
            data.add(station_data);
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
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
     * to write weatherExamples.pl
     */
    public void output() {
        
        BufferedWriter bw = null;
        
        try {
            bw = new BufferedWriter(new FileWriter("weatherExamples.pl"));
            
            /* in model.pl
            bw.write("t(_)::weather(sun,0) ; t(_)::weather(rain,0).");
            bw.newLine();
            bw.write("t(_)::weather(sun,T) ; t(_)::weather(rain,T) :- integer(T), T>0, Tprev is T-1, weather(sun,Tprev).");
            bw.newLine();
            bw.write("t(_)::weather(sun,T) ; t(_)::weather(rain,T) :- integer(T), T>0, Tprev is T-1, weather(rain,Tprev).");
            bw.newLine();
            bw.newLine();*/
            
            for (ArrayList<String[]> station : data) {
                for (int i = 0; i < station.size(); i++) {
                    String[] day = station.get(i);
                    /*
                    if (Double.parseDouble(day[10]) < 3) { // less than 3 sunshine hours
                        bw.write("evidence(weather(sun,"+i+"),false).");
                        bw.newLine();
                    } else {
                        bw.write("evidence(weather(sun,"+i+"),true).");
                        bw.newLine();
                    }*/
                    
                    if (Double.parseDouble(day[12]) < 2) { // less than 2mm rain
                        bw.write("evidence(weather(sun,"+i+"),true).");
                        bw.newLine();
                    } else {
                        bw.write("evidence(weather(rain,"+i+"),true).");
                        bw.newLine();
                    }
                    
                    if (Double.parseDouble(day[8]) > 3) { // more than 3 on Beaufort scale
                        bw.write("evidence(weather(windy,"+i+"),true).");
                        bw.newLine();
                    } else {
                        bw.write("evidence(weather(windy,"+i+"),false).");
                        bw.newLine();
                    }
                    
                    if (Double.parseDouble(day[5]) < 15) { // less than 15 degree Celsius 
                        bw.write("evidence(weather(cold,"+i+"),true).");
                        bw.newLine();
                    } else {
                        bw.write("evidence(weather(warm,"+i+"),true).");
                        bw.newLine();
                    }
                    
                }
                bw.write("-----");
                bw.newLine();
            }
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
    }

    public static void main(String[] args) {
        WeatherForecast wf = new WeatherForecast();
        wf.input();
        wf.output();
        System.out.println("done");

    }

}
