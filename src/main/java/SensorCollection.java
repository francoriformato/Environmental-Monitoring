/**
 * Interfaccia per l'implementazione del pattern Iterator.
 */

public interface SensorCollection {
    SensorIterator iterator(SensorStatusEnum state);

    void addSensor(Sensor s);

    void removeSensor(Sensor s);

}
