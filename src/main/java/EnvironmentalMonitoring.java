import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Punto di partenza del software.
 * Ha al suo interno la String per la connessione al DB, il numero massimo di Auto da generare al fine della simulazione
 * e la creazione dell'interfaccia dell'applicazione
 */
public class EnvironmentalMonitoring {

    public static void main(String[] args) {
            String DB = "com.mysql.cj.jdbc.Driver";
            Integer maxCars = 50; //Numero massimo di Auto da caricare nella simulazione

            //INTERFACCIA
            JFrame mainframe = new JFrame();
            mainframe.setSize(1920, 1080);

            //Immagine della mappa
            JLabel img = new JLabel(ImageLoader.imgDraw(System.getProperty("user.dir") + "./src/img/localMap.jpg"));

            //Creazione JTable per la visualizzazione dei sensori
             JTable jt = new JTable();
             DefaultTableModel dtm = new DefaultTableModel(0, 0);

             // Header della tabella
             String[] header = new String[] { "SensorID","In road", "Number of Cars","Temperature", "Pollution", "Status" };

             dtm.setColumnIdentifiers(header);
             jt.setModel(dtm);

            SensorsManagement.loadSensorTable(dtm);

            jt.setBounds(20,30,20,30);
            JScrollPane sp=new JScrollPane(jt);


            Container contentPanel = mainframe.getContentPane();
            GroupLayout groupLayout = new GroupLayout(contentPanel);
            contentPanel.setLayout(groupLayout);

        //Evento per la pressione del bottone 1 : Aggiungi nuovo sensore
        ActionListener b1Event = e -> {
            mainframe.setVisible(false);
            try {
                new Sensor();
                dtm.setRowCount(0);
                SensorsManagement.loadSensorTable(dtm);
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
            SwingUtilities.updateComponentTreeUI(mainframe);
            mainframe.invalidate();
            mainframe.validate();
            mainframe.repaint();
            img.setIcon(ImageLoader.imgDraw(System.getProperty("user.dir") + "./src/img/localMap.jpg"));
            mainframe.setVisible(true);
        };

        //Evento per la pressione del bottone 2 : Aggiorna dati
        ActionListener b2Event = e -> {
            mainframe.setVisible(false);
            dtm.setRowCount(0);
            SensorsManagement.loadSensorTable(dtm);
            SwingUtilities.updateComponentTreeUI(mainframe);
            mainframe.invalidate();
            mainframe.validate();
            mainframe.repaint();
            img.setIcon(ImageLoader.imgDraw(System.getProperty("user.dir") + "./src/img/localMap.jpg"));
            mainframe.setVisible(true);
        };


        //Evento per la pressione del bottone 3 : Mostra grafico della Temperatura
        ActionListener b3Event = e -> {
            try {
                WorldSimulation.DrawChart(DB, "hystoricaltemperature", "AVGTempValue", "gentemperature");
            } catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        };

        //Evento per la pressione del bottone 4 : Mostra grafico dell'Inquinamento
        ActionListener b4Event = e -> {
            try {
                WorldSimulation.DrawChart(DB, "hystoricalpollution", "AVGPollValue", "genpollution");
            } catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        };

        JButton moreSensors = new JButton("Add new Sensor");

        moreSensors.addActionListener(b1Event);

        JButton reloadTable = new JButton("Update data");

        reloadTable.addActionListener(b2Event);

        JButton drawGraphT = new JButton("Temperature Graph");

        drawGraphT.addActionListener(b3Event);

        JButton drawGraphP = new JButton("Pollution Graph");

        drawGraphP.addActionListener(b4Event);


            groupLayout.setHorizontalGroup(
                    groupLayout.createSequentialGroup()
                            .addComponent(img)
                            .addGap(5, 10, 30)
                            .addComponent(sp)
                            .addComponent(moreSensors)
                            .addComponent(reloadTable)
                            .addComponent(drawGraphT)
                            .addComponent(drawGraphP)
                            );
            groupLayout.setVerticalGroup(
                    groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(img)
                            .addComponent(sp)
                            .addComponent(moreSensors)
                            .addComponent(reloadTable)
                            .addComponent(drawGraphT)
                            .addComponent(drawGraphP)
                            );
            mainframe.pack();
            mainframe.setVisible(true);

            CarsManagement.loadCars(DB, maxCars);

            System.out.println("Ci sono: " + CarsManagement.getNumberofCars(DB) + " auto!");

            //Se non ci sono sensori all'avvio, fai partire la procedura di inserimento del primo sensore.
            if (SensorsManagement.getNumberofSensors(DB) == 0)
            {
                moreSensors.doClick();
            }

            WorldSimulation.startWorldActivities(DB);


    }
}