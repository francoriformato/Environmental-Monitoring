import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
/**
 * Classe che rappresenta i sensori utilizzati per l'applicazione.
 * Estende PointOfInterest, dato che i Sensori sono particolari Punti di Interesse.
 * Inoltre, viene per essi utilizzato il pattern Iterator.
 */
public class Sensor extends PointOfInterest {

    private Double maxPValue;
    private Double maxTValue;
    private Integer maxCValue;

    private Double currentPValue;
    private Double currentTValue;
    private Integer currentCValue;

    private String inRoad;

    private SensorStatusEnum  state;

    public Sensor(int pName, Double pLat, Double pLong, Double maxPValue, Double maxTValue, Integer maxCValue, Double currentPValue, Double currentTValue, Integer currentCValue, SensorStatusEnum sensorStatus, String inRoad) {
        super(pName, pLat, pLong);
        this.maxPValue = maxPValue;
        this.maxTValue = maxTValue;
        this.maxCValue = maxCValue;
        this.currentPValue = currentPValue;
        this.currentTValue = currentTValue;
        this.currentCValue = currentCValue;
        this.state = sensorStatus;
        this.inRoad = inRoad;
    }

    public Sensor() throws IOException, InterruptedException {
        String DB = "com.mysql.cj.jdbc.Driver";
        ObjectMapper mapper = new ObjectMapper();
        Geocoder geocoder = new Geocoder();
        ReverseGeocoder reverseGeocoder = new ReverseGeocoder();

        String maxCValueI, maxTValueI, maxPValueI, roadNameI, roadName, roadStrategyI, roadStrategy;
        int maxCValue;
        double maxTValue, maxPValue;

        roadNameI = JOptionPane.showInputDialog("Road Name: ");
        if (roadNameI == null)
        {
            roadName = "";
        }
        else {
            roadName = (roadNameI);
        }

        roadStrategyI = JOptionPane.showInputDialog("Road Strategy when Sensor is RED: ODD, EVEN, NONE or BLOCKED ");
        if (roadStrategyI == null || !roadStrategyI.equals("ODD") && !roadStrategyI.equals("EVEN") && !roadStrategyI.equals("NONE") && !roadStrategyI.equals("BLOCKED"))
        {
            roadStrategy = "";
        }
        else {
            roadStrategy = (roadStrategyI);
        }

        maxCValueI = JOptionPane.showInputDialog("Number of CARS threshold value: ");
        if (String.valueOf(maxCValueI).equals("") || maxCValueI == null)
        {
            maxCValue = 0;
        }
        else {
            maxCValue = Integer.parseInt(maxCValueI);
        }

        maxTValueI = JOptionPane.showInputDialog("TEMPERATURE threshold value: ");
        if (String.valueOf(maxTValueI).equals("") || maxTValueI == null)
        {
            maxTValue = 0;
        }
        else {
            maxTValue = Double.parseDouble(maxTValueI);
        }

        maxPValueI = JOptionPane.showInputDialog("POLLUTION threshold value: ");
        if (String.valueOf(maxPValueI).equals("") || maxPValueI == null)
        {
            maxPValue = 0;
        }
        else {
            maxPValue = Double.parseDouble(maxPValueI);
        }

        if (roadName.length() == 0 || roadName.equals("") || roadStrategy.equals(""))
        {
            JOptionPane.showMessageDialog(null, "Unable to load sensor.", "Sensor NOT received", JOptionPane.PLAIN_MESSAGE );
        }
        else {
            Sensor sensor = new Sensor(mapper,geocoder,reverseGeocoder, maxCValue, maxTValue, maxPValue, roadName, roadStrategy);
            JOptionPane.showMessageDialog(null, "Sensor : " + sensor.getpName() + " at: " + sensor.getInRoad() + " received." , "Sensor Received!", JOptionPane.PLAIN_MESSAGE );
            SensorsManagement.loadSensortoDB(DB, sensor);
        }


    }

    public Sensor(ObjectMapper mapper, Geocoder geocoder, ReverseGeocoder reverseGeocoder, Integer maxCValue, Double maxTValue, Double maxPValue, String roadName, String roadStrategy) throws IOException, InterruptedException {
        super(mapper, geocoder, roadName);

        loadSensor(maxCValue, maxTValue, maxPValue);
        setInRoad(mapper, reverseGeocoder, roadStrategy);
        this.setSensorStatus(SensorStatusEnum.GATHERING);

    }

    public String getInRoad() {
        return inRoad;
    }

    public void setInRoad(ObjectMapper mapper, ReverseGeocoder reverseGeocoder, String roadStrategy) throws IOException, InterruptedException{

        String latitude = String.valueOf(this.getpLat());
        String longitude = String.valueOf(this.getpLong());

        this.inRoad = searchPOI(mapper, reverseGeocoder, latitude, longitude);


        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/EMDB","root","root");


            String query = " insert into roads (Address, policy)"
                         + " values (? , ?)";

            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString (1, this.inRoad);
            preparedStmt.setString(2, roadStrategy);

            preparedStmt.execute();

            con.close();

        }catch(Exception e){ System.out.println(e);}


    }

    public Double getMaxPValue() {
        return maxPValue;
    }

    public void setMaxPValue(Double maxPValue) {
        this.maxPValue = maxPValue;
    }

    public Double getMaxTValue() {
        return maxTValue;
    }

    public void setMaxTValue(Double maxTValue) {
        this.maxTValue = maxTValue;
    }

    public Integer getMaxCValue() {
        return maxCValue;
    }

    public void setMaxCValue(Integer maxCValue) {
        this.maxCValue = maxCValue;
    }

    public void loadSensor(Integer maxCValue, double maxTValue, double maxPValue)
    {
        this.setMaxCValue(maxCValue);
        this.setMaxTValue(maxTValue);
        this.setMaxPValue(maxPValue);

        this.setCurrentCValue(0);
        this.setCurrentTValue(0D);
        this.setCurrentPValue(0D);

    }

    public SensorStatusEnum getSensorStatus() {
        return state;
    }

    public void setSensorStatus(SensorStatusEnum sensorStatus) {
        this.state = sensorStatus;
    }

    public Double getCurrentPValue() {
        return currentPValue;
    }

    public void setCurrentPValue(Double currentPValue) {
        this.currentPValue = currentPValue;

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/EMDB","root","root");

            Statement stmt = con.createStatement();
            stmt.executeUpdate("UPDATE sensor SET currentPValue = '" + this.getCurrentPValue() + "' WHERE PNAME = " + this.getpName());


        }catch(Exception e){ System.out.println(e);}

    }

    public Double getCurrentTValue() {
        return currentTValue;
    }

    public void setCurrentTValue(Double currentTValue) {
        this.currentTValue = currentTValue;

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/EMDB","root","root");

            Statement stmt = con.createStatement();
            stmt.executeUpdate("UPDATE sensor SET currentTValue = '" + this.getCurrentTValue() + "' WHERE PNAME = " + this.getpName());


        }catch(Exception e){ System.out.println(e);}

    }

    public Integer getCurrentCValue() {
        return currentCValue;
    }

    public void setCurrentCValue(Integer currentCValue) {
        this.currentCValue = currentCValue;

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/EMDB","root","root");

            Statement stmt = con.createStatement();
            stmt.executeUpdate("UPDATE sensor SET currentCValue = '" + this.getCurrentCValue() + "' WHERE PNAME = " + this.getpName());


        }catch(Exception e){ System.out.println(e);}


    }

}



