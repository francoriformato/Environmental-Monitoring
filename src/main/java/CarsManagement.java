import java.sql.*;
/**
 * Interfaccia per la gestione dell'insieme di auto.
 * Permette di richiedere al DB il numero di auto, di selezionare un'auto casuale e di caricare una nuova lista di auto.
 */
public interface CarsManagement {

    /**
     * Metodo che permette di ottenere il numero di auto caricate nel DB.
     * @return un numero intero che indica il numero di auto.
     */
    static Integer getNumberofCars (String DBString) {

        int carNumber = 0;

        try{
            Class.forName(DBString);
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/EMDB","root","root");

            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select COUNT(LicensePlate) from CARS");

            if(rs.next()){

                carNumber = rs.getInt(1);

            }

        }catch(Exception e){
            System.out.println(e);}

        return carNumber;

    }

    /**
     * Metodo che permette di selezionare un'Auto casuale che sia gia' presente nel DB.
     * @return un Auto con License Plate casuale tra quelle nel DB.
     */
    static Auto selectRandomCar(String DBString) {

        Auto auto = new Auto();

        try{
            Class.forName(DBString);
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/EMDB","root","root");

            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select LicensePlate from CARS ORDER BY rand() limit 1");

            if(rs.next()){

                auto.setLicensePlate(rs.getString(1));

            }

        }catch(Exception e){ //noinspection ThrowablePrintedToSystemOut
            System.out.println(e);}

        return auto;

    }

    /**
     * Metodo che carica un maxCarsNumber di Auto nel DB.
     * Il numero di auto da caricare al fine della simulazione sarà stabilito nel main.
     */

    static void loadCars(String DBString, Integer maxCarsNumber) {

        while (getNumberofCars(DBString) < maxCarsNumber)
        {
            Auto auto = new Auto();

            //MYSQL DB CONNECTION
            try{
                Class.forName(DBString);
                Connection con= DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/EMDB","root","root");


                String query = " insert into CARS (LicensePlate)"
                             + " values (?)";

                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = con.prepareStatement(query);
                preparedStmt.setString (1, auto.getLicensePlate());

                preparedStmt.execute();

                con.close();

            }catch(Exception e){
                System.out.println(e);}

        }

        }

}
