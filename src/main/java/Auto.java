import java.util.ArrayList;
import java.util.Random;

/**
 * Rappresenta un Auto e le sue caratteristiche.
 * Verra' usata per generare auto nel programma.
 */
public class Auto{

    static final ArrayList<String> autoList = new ArrayList<>();

    private String licensePlate;

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
    /**
     * Costruttore della classe Auto.
     * Permette di assegnare una targa (License Plate) casuale all'auto.
     */
    public Auto() {
        //Processo di generazione della targa di un'Auto
        int alpha1 = 'A' + (int)(Math.random() * ('Z' - 'A'));
        int alpha2 = 'A' + (int)(Math.random() * ('Z' - 'A'));
        int alpha3 = 'A' + (int)(Math.random() * ('Z' - 'A'));
        int digit1 = (int)(Math.random() * 10);
        int digit2 = (int)(Math.random() * 10);
        int digit3 = (int)(Math.random() * 10);
        int digit4 = (int)(Math.random() * 10);

        this.licensePlate= "" + (char)(alpha1) + ((char)(alpha2)) + ((char)(alpha3)) + digit1 + digit2 + digit3 + digit4;

        autoList.add(String.valueOf(this));
    }

    /**
     * Metodo che @return se la targa di un auto e' PARI o DISPARI.
     */

    public String licensePlateOddOrEven(){

        int lastNumberLP = Integer.parseInt(this.licensePlate.substring(this.licensePlate.length() - 1));

        if ( lastNumberLP % 2 == 0 ) {
            return "EVEN";
        } else {
            return "ODD";
        }
    }

    /**
     * Metodo che permette all'auto di interagire con un sensore.
     * Al fine della simulazione, viene introdotta una chance random che l'auto interagisca con il sensore.
     */

    public void interactWith(Sensor s) {
        Random r = new Random();
        float chance = r.nextFloat();

        if (chance <= 0.40f)
        {
            s.setCurrentCValue(s.getCurrentCValue() + 1);
        }
    }


}
