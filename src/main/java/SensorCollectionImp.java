import java.util.ArrayList;
import java.util.List;
/**
 * Classe che implementa la SensorCollection, per l'utilizzo del pattern Iterator.
 * Viene utilizzata per modificare la lista dei sensori e per iterare su di essi a seconda di una determinata richiesta.
 */
public class SensorCollectionImp implements SensorCollection{
    private final List<Sensor> sensorsList;

    public SensorCollectionImp() {
        sensorsList = new ArrayList<>();
    }

    public void addSensor(Sensor s) {
        this.sensorsList.add(s);
    }

    public void removeSensor(Sensor s) {
        this.sensorsList.remove(s);
    }

    @Override
    public SensorIterator iterator(SensorStatusEnum type) {
        return new SensorIteratorImpl(type);
    }

    private class SensorIteratorImpl implements SensorIterator {

        private final SensorStatusEnum type;
        private final List<Sensor> sensors;
        private int position;

        public SensorIteratorImpl(SensorStatusEnum state) {
            this.type = state;
            this.sensors = sensorsList;
        }

        @Override
        public boolean hasNext() {
            while (position < sensors.size()) {
                Sensor s = sensors.get(position);
                if (s.getSensorStatus().equals(type) || type.equals(SensorStatusEnum.ALL)) {
                    return true;
                } else
                    position++;
            }
            return false;
        }

        @Override
        public Sensor next() {
            Sensor s = sensors.get(position);
            position++;
            return s;
        }

    }
}