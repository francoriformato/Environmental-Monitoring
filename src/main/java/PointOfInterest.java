import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Classe astratta che va poi a specializzarsi nei Sensori.
 * Indica un generico punto di interesse sulla mappa, nel caso della nostra simulazione il Sensore.
 * E' possibile ampliarne l'uso in caso si volessero segnare differenti punti di interesse sulla mappa.
 */

public abstract class PointOfInterest {

    private static int pNameCount = countPOI();

    int pName;

    Double pLat;
    Double pLong;

    public PointOfInterest() {
    }

    /**
     * Metodo che conta i Punti di Interesse sulla mappa.
     * @return il numero di punti di interesse segnati.
     */
    private static int countPOI() {

        int poiNumber = 0;

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/EMDB","root","root");

            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select COUNT(PNAME) from sensor");

            if(rs.next()){

                poiNumber = rs.getInt(1);

            }

        }catch(Exception e){ //noinspection ThrowablePrintedToSystemOut
            System.out.println(e);}

        return poiNumber;
    }

    public PointOfInterest(int pName, Double pLat, Double pLong) {
        this.pName = pName;
        this.pLat = pLat;
        this.pLong = pLong;
    }

    public PointOfInterest(ObjectMapper mapper, Geocoder geocoder, String roadName) throws IOException, InterruptedException {

        pName = pNameCount;
        pNameCount++;

        //call geocoder
        String response = geocoder.GeocodeSync(roadName);
        JsonNode responseJsonNode = mapper.readTree(response);

        JsonNode items = responseJsonNode.get("items");

        for (JsonNode item : items) {
            JsonNode address = item.get("address");
            String label = address.get("label").asText();
            JsonNode position = item.get("position");

            String lat = position.get("lat").asText();
            String lng = position.get("lng").asText();
            System.out.println(label + " is located at " + lat + "," + lng + ".");

            pLat = Double.parseDouble(lat);
            pLong = Double.parseDouble(lng);

        }

    }

    /**
     * Metodo che ricerca un Punto di Interesse tramite geocodifica.
     * @return una stringa con le coordinate del punto indicato.
     */

    public String searchPOI (ObjectMapper mapper, ReverseGeocoder reverseGeocoder, String searchedLat, String searchedLong) throws IOException, InterruptedException {

        //call geocoder
        String response = reverseGeocoder.ReverseGeocodeSync("" + searchedLat + "," + searchedLong + "");
        JsonNode responseJsonNode = mapper.readTree(response);

        JsonNode items = responseJsonNode.get("items");


        for (JsonNode item : items) {
            JsonNode address = item.get("address");
            String street = address.get("street").asText();
            JsonNode position = item.get("position");

            String lat = position.get("lat").asText();
            String lng = position.get("lng").asText();
            System.out.println(lat + " " + lng + " is: " + street);

            return street;

        }

        return "Not found.";

    }

    public Integer getpName() {
        return pName;
    }

    public Double getpLat() {
        return pLat;
    }

    public Double getpLong() {
        return pLong;
    }

    @Override
    public String toString() {
        return "&poix" + this.getpName() + "=" + this.getpLat() + "%2C" + this.getpLong() + "%3Bwhite%3Bblack%3B15%3B";
    }


}



