import java.sql.*;
/**
 * Interfaccia che permette la gestione del sistema di Multe.
 * Permette di controllare le Politiche di una determinata strada e di inviare una multa ad una determinata Auto.
 */
public interface FinesSystem {

    /**
     * Metodo che permette di controllare la Politica di una strada.
     * Prende in Input l'indirizzo da controllare e @return la politica di quella determinata strada.
     */
    static String checkRoad(String address) {
        String roadPolicy = "";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/EMDB","root","root");

            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select policy from roads WHERE Address = '" + address + "'");

            if(rs.next()){

                roadPolicy = rs.getString(1);

            }

        }catch(Exception e){ //noinspection ThrowablePrintedToSystemOut
            System.out.println(e);}

        return roadPolicy;

    }

    /**
     * Metodo che invia una Multa ad un'Auto.
     * E' possibile implementare maggiori dettagli per scegliere l'ammontare della multa, di base e' stato scelto "200".
     */

    static void sendFine(Auto auto) {

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/EMDB","root","root");


            String query = " insert into fines (amount, FLicensePlate)"
                         + " values (?, ?)";

            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setDouble (1, 200);
            preparedStmt.setString(2, auto.getLicensePlate());

            preparedStmt.execute();

            con.close();

        }catch(Exception e){ //noinspection ThrowablePrintedToSystemOut
            System.out.println(e);}


    }
}
