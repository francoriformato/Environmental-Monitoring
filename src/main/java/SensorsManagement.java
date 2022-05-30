import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
/**
 * Interfaccia per la gestione dei Sensori.
 * Permette di ottenere la lista dei sensori presenti nel DB, di caricare la Tabella dei sensori al fine della sua visualizzazione,
 * di controllare il codice (ROSSO, GIALLO, VERDE) di un determinato sensore, di selezionare un sensore casuale dal DB, di caricare i sensori nel DB e
 * di ottenere il numero di sensori presenti.
 */

public interface SensorsManagement extends SensorIterator, SensorCollection {

    static SensorCollection getSensorList() {

        SensorCollection sensors = new SensorCollectionImp();

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/EMDB","root","root");

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from sensor");

            while(rs.next()) {
                {
                    Sensor sensor = new Sensor(rs.getInt("PNAME"), rs.getDouble("LAT"), rs.getDouble("LNG"), rs.getDouble("maxPValue"), rs.getDouble("maxTValue"), rs.getInt("maxCValue"), rs.getDouble("currentPValue"), rs.getDouble("currentTValue"), rs.getInt("currentCValue"), SensorStatusEnum.valueOf(rs.getString("status")), rs.getString("inRoad"));
                    sensors.addSensor(sensor);
                }
            }

        }catch(Exception e){ System.out.println(e);}

        return sensors;
    }

    static void loadSensorTable(DefaultTableModel dtm) {

        SensorCollection sensors = SensorsManagement.getSensorList();
        SensorIterator baseIterator = sensors.iterator(SensorStatusEnum.ALL);

        while (baseIterator.hasNext())
        {
            Sensor s = baseIterator.next();
            dtm.addRow(new Object[] { String.valueOf(s.getpName() + 1), s.getInRoad(), s.getCurrentCValue(), s.getCurrentTValue(),
                    s.getCurrentPValue(), s.getSensorStatus() });
        }


    }

    static String checkSensorCode(Sensor s)
    {
        s.setSensorStatus(SensorStatusEnum.valueOf("GATHERING"));

        if ( (s.getCurrentPValue() < s.getMaxPValue()) && (s.getCurrentTValue() < s.getMaxTValue()) && (s.getCurrentCValue() < s.getMaxCValue()))
        {
            s.setSensorStatus(SensorStatusEnum.valueOf("GREEN"));
        }

        if ( (s.getCurrentPValue() >= s.getMaxPValue()) && (s.getCurrentTValue() >= s.getMaxTValue()) && (s.getCurrentCValue() <= s.getMaxCValue()))
        {
            s.setSensorStatus(SensorStatusEnum.valueOf("YELLOW"));
        }

        if ( (s.getCurrentPValue() > s.getMaxPValue()) && (s.getCurrentTValue() > s.getMaxTValue()) && (s.getCurrentCValue() > s.getMaxCValue()))
        {
            s.setSensorStatus(SensorStatusEnum.valueOf("RED"));
        }

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/EMDB","root","root");

            Statement stmt = con.createStatement();
            stmt.executeUpdate("UPDATE sensor SET status = '" + s.getSensorStatus() + "' WHERE PNAME = " + s.getpName());


        }catch(Exception e){ System.out.println(e);}


        return String.valueOf(s.getSensorStatus());
    }

    static Sensor selectRandomSensor(String DBString) throws IOException, InterruptedException
    {
        try{
            Class.forName(DBString);
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/EMDB","root","root");

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from sensor ORDER BY rand() limit 1");

            while(rs.next()) {
                {
                    Sensor sensor = new Sensor(rs.getInt("PNAME"), rs.getDouble("LAT"), rs.getDouble("LNG"), rs.getDouble("maxPValue"), rs.getDouble("maxTValue"), rs.getInt("maxCValue"), rs.getDouble("currentPValue"), rs.getDouble("currentTValue"), rs.getInt("currentCValue"), SensorStatusEnum.valueOf(rs.getString("status")), rs.getString("inRoad"));
                    System.out.println("Ritornato il sensore: " + sensor.getpName() );
                    return sensor;
                }
            }

        }catch(Exception e){ System.out.println(e);}

        return null;

    }

    static void loadSensortoDB(String DBString, Sensor s) {

        //MYSQL DB CONNECTION
        try{
            Class.forName(DBString);
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/EMDB","root","root");


            String query = " insert into SENSOR (PNAME, LAT, LNG, maxPValue, maxTValue, maxCValue, currentPValue, currentTValue, currentCValue, status, inRoad)"
                    + " values (? , ? , ? , ? , ? , ? , ? , ? , ? , ?, ?)";

            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = con.prepareStatement(query);

            preparedStmt.setInt (1, s.getpName());
            preparedStmt.setDouble(2, s.getpLat());
            preparedStmt.setDouble(3, s.getpLong());
            preparedStmt.setDouble(4, s.getMaxPValue());
            preparedStmt.setDouble(5, s.getMaxTValue());
            preparedStmt.setInt(6, s.getMaxCValue());
            preparedStmt.setDouble(7, s.getCurrentPValue());
            preparedStmt.setDouble(8, s.getCurrentTValue());
            preparedStmt.setInt(9, s.getCurrentCValue());
            preparedStmt.setString(10, String.valueOf(s.getSensorStatus()));
            preparedStmt.setString(11, s.getInRoad());

            preparedStmt.execute();

            con.close();

        }catch(Exception e){ System.out.println(e);}

        String mapURL;
        Connection con = null;
        Statement statement = null;

        try {
            ArrayList<String> specialPoints = new ArrayList<>();
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/emdb", "root","root");
            statement = con.createStatement();
            String sql;
            sql = "select PNAME, LAT, LNG from sensor";
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                specialPoints.add("&poix" + resultSet.getString("PNAME") + "=" + resultSet.getString("LAT") + "%2C" + resultSet.getString("LNG") + "%3Bwhite%3Bblack%3B15%3B");
            }


            mapURL = MapURL.SelectLocation(specialPoints);
            URL imgURL = new URL(mapURL);
            ImageLoader.imgLoad(imgURL);
            System.out.println(imgURL);

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    static Integer getNumberofSensors (String DBString) {

        int sensorsNumber = 0;

        try{
            Class.forName(DBString);
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/EMDB","root","root");

            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select COUNT(PNAME) from sensor");

            if(rs.next()){

                sensorsNumber = rs.getInt(1);

            }

        }catch(Exception e){ //noinspection ThrowablePrintedToSystemOut
            System.out.println(e);}

        return sensorsNumber;

    }

}
