import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.jdbc.JDBCCategoryDataset;

import java.io.IOException;
import java.sql.*;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Interfaccia utilizzata per la gestione della Simulazione.
 * Contiene metodi per dare vita alla simulazione, per caricare i valori medi al fine di generare i grafici e per permetterne la visualizzazione.
 */


public interface WorldSimulation {
    static void startWorldActivities (String DB) {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           @Override
                           public void run() {

                               Sensor sR = null;

                               while (sR == null)
                               {
                                   try {
                                       sR = SensorsManagement.selectRandomSensor(DB);
                                   } catch (IOException | InterruptedException e) {
                                       e.printStackTrace();
                                   }
                               }

                               Auto auto = new Auto();
                               WorldFactory factory = new WorldFactory();

                               Random r = new Random();
                               float chance = r.nextFloat();

                               if (chance <= 0.50f)
                               {
                                   WorldElement temperature = factory.getElement(WorldValueEnum.Temperature);
                                   temperature.loadValue(DB, "gentemperature (TempDate, Value)");
                                   temperature.interactWith(sR);
                                   System.out.println("Generato: " + temperature);
                               }
                               else {
                                   WorldElement pollution = factory.getElement(WorldValueEnum.Pollution);
                                   pollution.loadValue(DB, "genpollution (PollDate, Value)");
                                   pollution.interactWith(sR);
                                   System.out.println("Generato: " + pollution);
                               }

                               auto.setLicensePlate(CarsManagement.selectRandomCar(DB).getLicensePlate());

                               auto.interactWith(sR);


                               if (SensorsManagement.checkSensorCode(sR).equals("RED")) {

                                   if (FinesSystem.checkRoad(sR.getInRoad()).equals("BLOCKED"))
                                   {
                                       FinesSystem.sendFine(auto);
                                   }

                                   if (FinesSystem.checkRoad(sR.getInRoad()).equals("ODD") && auto.licensePlateOddOrEven().equals("EVEN"))
                                   {
                                       FinesSystem.sendFine(auto);
                                   }

                                   if (FinesSystem.checkRoad(sR.getInRoad()).equals("EVEN") && auto.licensePlateOddOrEven().equals("ODD"))
                                   {
                                       FinesSystem.sendFine(auto);
                                   }

                               }
                               WorldSimulation.loadAVG(DB, "hystoricaltemperature", "AVGTempValue", "gentemperature");
                               WorldSimulation.loadAVG(DB, "hystoricalpollution", "AVGPollValue", "genpollution");

                           }
                       },
                0,
                1000);

    }

    static void loadAVG(String DBString, String dbwhere, String dbwhat, String dbwho) {

        Double newAVG = getNewAVG(DBString, dbwho);
        String query;

        java.util.Date date=new java.util.Date();
        java.sql.Date sqlDate=new java.sql.Date(date.getTime());

        //MYSQL DB CONNECTION
        try{
            Class.forName(DBString);
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/EMDB","root","root");

            if (avgExists(DBString, dbwhere) != 0)
            {
                query = "update " + dbwhere + " set " + dbwhat + " = '" + newAVG + "' where Day = '" + sqlDate + "'";
                PreparedStatement preparedStmt = con.prepareStatement(query);
                preparedStmt.execute();
            }
            else {
                query = " insert into " + dbwhere + " values ( ?, ?)";
                PreparedStatement preparedStmt = con.prepareStatement(query);
                preparedStmt.setDate (1, sqlDate);
                preparedStmt.setDouble (2, newAVG);
                preparedStmt.execute();
            }

            con.close();

        }catch(Exception e){ System.out.println(e);}

    }

    static Double getNewAVG(String DBString, String dbwhere) {

        Double avg = 0D;

        try{
            Class.forName(DBString);
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/EMDB","root","root");

            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select AVG(Value) from " + dbwhere);

            if(rs.next()){

                avg = rs.getDouble(1);

            }

        }catch(Exception e){ //noinspection ThrowablePrintedToSystemOut
            System.out.println(e);}

        return avg;

    }

    static Integer avgExists(String DBString, String dbwhere) {

        int avgCount = 0;

        try{
            Class.forName(DBString);
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/EMDB","root","root");

            Statement stmt=con.createStatement();
            java.util.Date date=new java.util.Date();
            java.sql.Date sqlDate=new java.sql.Date(date.getTime());

            ResultSet rs=stmt.executeQuery("select COUNT(Day) from " + dbwhere + " WHERE Day = '" + sqlDate + "'");

            if(rs.next()){

                avgCount = rs.getInt(1);
            }

        }catch(Exception e){
            System.out.println(e);}

        return avgCount;

    }

    static void DrawChart(String DBString, String dbwhere, String dbwhat, String dbwho) throws SQLException, ClassNotFoundException {

        loadAVG(DBString, dbwhere, dbwhat, dbwho);

        String query = "SELECT * from " + dbwhere;
        JDBCCategoryDataset dataset = new JDBCCategoryDataset(
                "jdbc:mysql://localhost:3306/EMDB", "com.mysql.jdbc.Driver",
                "root", "root");

        dataset.executeQuery(query);
        JFreeChart chart = ChartFactory.createLineChart("Daily Data", "Day", "Value",
                dataset, PlotOrientation.VERTICAL, true, true, false);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        ApplicationFrame f = new ApplicationFrame("Chart");
        f.setContentPane(chartPanel);
        f.pack();
        f.setVisible(true);

    }

}
