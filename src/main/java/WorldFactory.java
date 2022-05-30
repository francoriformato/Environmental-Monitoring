/**
 * Classe utilizzata per l'implementazione del Factory pattern.
 * Permette di creare su richiesta un oggetto Pollution o Temperature a seconda della necessita'.
 */
public class WorldFactory {

    public WorldFactory() {
    }

    public WorldElement getElement (WorldValueEnum type) {

        WorldElement retval = null;

        switch (type) {
            case Pollution -> {
                return new Pollution();
            }
            case Temperature -> {
                return new Temperature();
            }
        }

        return retval;
    }
}
