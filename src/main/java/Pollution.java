/**
 * Classe utilizzata per la simulazione dell'Inquinamento sulla mappa.
 * E' utilizzata a fini simulativi, in quanto in un caso reale, questi dati sarebbero ottenuti mediante i valori reali registrati dal sensore.
 * In questo caso, viene indicata la Pollution come l'Inquinamento dell'aria sulla nostra mappa, generato casualmente.
 */

public class Pollution implements WorldElement {

    double value = Math.random() * 100;
    /**
     * Metodo che permette all'Inquinamento di interagire con un sensore.
     * Al fine della simulazione, viene stabilito che se il nuovo valore di Pollution e' minore di quello attuale del sensore, allora il valore
     * nel sensore scendera' del 10%, altrimenti aumentera' del 10%.
     */
    public void interactWith(Sensor s) {

        Double currentP = s.getCurrentPValue();

        if (currentP < this.value)
        {
            s.setCurrentPValue(currentP * (1 + 0.1) );
        }

        if (currentP > this.value)
        {
            s.setCurrentPValue(currentP * (1 - 0.1) );
        }

        else
        {
            s.setCurrentPValue(this.value);
        }

    }

    @Override
    public Double getValue() {
        return value;
    }


}
