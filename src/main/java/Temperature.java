/**
 * Classe utilizzata per la simulazione della Temperatura sulla mappa.
 * E' utilizzata a fini simulativi, in quanto in un caso reale, questi dati sarebbero ottenuti mediante i valori reali registrati dal sensore.
 * In questo caso, viene indicata la Temperature come la temperatura registrata sulla nostra mappa, generato casualmente.
 */
public class Temperature implements WorldElement{

    double value = Math.random() * 100;

    @Override
    public void interactWith(Sensor s) {
        Double currentT = s.getCurrentTValue();

        if (currentT < this.value)
        {
            s.setCurrentTValue(currentT * (1 + 0.1) );
        }

        if (currentT > this.value)
        {
            s.setCurrentTValue(currentT * (1 - 0.1) );
        }

        else
        {
            s.setCurrentTValue(this.value);
        }
    }

    @Override
    public Double getValue() {
        return value;
    }


}