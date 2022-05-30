import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
/**
 * Interfaccia utilizzata per l'implementazione del Factory Pattern.
 * Indica le generiche azioni che svolgeranno Temperature & Pollution.
 */
public interface WorldElement {

    void interactWith(Sensor s);

    default void loadValue(String DBString, String dbwhere) {

        //MYSQL DB CONNECTION
        try{
            Class.forName(DBString);
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/EMDB","root","root");


            String query = " insert into " + dbwhere
                         + " values (?, ?)";

            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = con.prepareStatement(query);

            java.util.Date date=new java.util.Date();
            java.sql.Date sqlDate=new java.sql.Date(date.getTime());

            preparedStmt.setDate (1, sqlDate);
            preparedStmt.setDouble (2, this.getValue());

            preparedStmt.execute();

            con.close();

        }catch(Exception e){ System.out.println(e);}

    }

    Double getValue();
}

