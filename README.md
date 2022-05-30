# Environmental-Monitoring
Project developed for Java &amp; Object Oriented Programming course at UniPa "Università degli Studi di Napoli Parthenope".

### Assignment
----
In a city, there are different control units on the roads that monitors three parameters: pollution, temperature and number of cars passing in front of the control unit.

For each parameter, the admin can set a guard threshold.

The system can be in three different states:
- Green : if all three parameters are under their guard thresold;
- Yellow : if the first two parameters (pollution & temperature) are over their guard threshold;
- Red : if all three parameters are over their guard thresolds

When in red state, those two strategies (choosen beforehand) can be applied:
- allow traffic only with alternate number plates: a device automatically checks the cars and proceeds by sending a report to the local police in the event of an infringement;
OR
- the traffic flow is sent on another path, so every car that still uses the road with a control unit in red state is fined.

The system also allows you to add new sensors to the network and graph the parameters for a fixed period.

# My implementation of requirements
- The Control Units will be identified with generic Point Of Interest on the map, that will be specialized into Sensors (so in a wider range, the software could be used also with different units that don't have sensors / do different things);
- The Sensors will register the values of the parameters, obtained from the outer world, and a Sensor Manager will manage them;
- Every Sensor will have a guard threshold for each parameter (maxTValue, maxCValue, maxPValue);
- The state of each sensor can be seen from the user's interface;
- ODD, EVEN, BLOCKED, NONE for each road to choose the strategy when a red code happens;
- A Fines System will eventually send fines to cars that don't respect the rules;
- Graphs of Temperature and Pollution made with JFreeChart.

# Displaying the map: HERE API
In order to correctly display the map and the locations of the Points of Interest, the HERE API was used.
The map is requested by defining the URL of the map (from the MapURL class), simply by concatenating strings in order to obtain the correct URL to make a request to HERE.
Subsequently, the URL is passed to ImageLoader, which with the imgLoad method allows you to locally download the map image (so as not to continually call HERE API with the same API Key, as a limited number of uses are available).
Finally, the imgDraw method returns the correct map image:

- If there is no sensor at the first launch of the app, no map will be shown and WorldSimulation will wait for the first sensor to be inserted, otherwise the various activities of the simulated world will not start;
- With only one sensor, the map will be centered on its position;
- With more sensors, the map will expand to allow viewing of all points.

# Direct and Reverse Geocoding
In this software, it is necessary to transform the name of an address where the sensor will be positioned into its coordinates expressed as Latitude and Longitude (to avoid having to directly enter latitude and longitude when inserting a sensor). 
This is a direct geocoding process that is done by Geocoder, according to the HERE API documentation.
Reverse geocoding was used to get the name of a street, given its coordinates. It is useful when you need to check the policies set for a road, since - in the DB - the sensor is registered with Latitude and Longitude, while it is not possible to do the same thing for the roads (registered as Address, Policy).

# Database : MySQL
The database was implemented via the Gradle dependency: 'mysql: mysql-connector-java: 8.0.25'.
This gives you the possibility to use a MySQL and make requests to the DB.
The information contained in the DB are:
- Car loading, based on the maximum number of cars to be generated for the simulation;
- Saving fines;
- Saving of Temperatures and Pollution values randomly generated for the simulation;
- The use of hystoricaltemperature and hystoricalpollution to save the daily average of temperature and pollution (to then draw the graphs);
- The roads and their policies;
-The list of sensors, with their position, status, which street they are in and the recorded values.

# Design Pattern : Iterator
Iterator is a behavioral pattern used to provide a standard way of traversing a group of objects, without exposing the internal representation, to the application.
The pattern is implemented through the classes:
- SensorStatusEnum.java, which defines the contract for the collection to iterate, defining the states in which a sensor can be;
- Sensor.java, where the private variable SensorStatusEnum state is defined;
- SensorCollection.java, which declares methods for adding and deleting sensors and a method that returns the iterator.
- SensorIterator.java, which declares methods that allow to know if there is a next sensor and to obtain the next sensor;
- SensorCollectionImpl.java, which implements SensorCollection, in order to define its methods and hide the internal implementation.
Its use within the application is when loading the Sensors table for the graphical interface.

# Design Pattern : Factory Method
This pattern is used when we have a superclass with multiple subclasses and need to return one of the subclasses as needed.
Allows you to shift the responsibility of instantiating the class from the client to the factory class.
The pattern was implemented through the classes:
WorldElement.java, is an interface that declares the method for interacting with the sensors and the method for obtaining the value of the element, it also defines the default method for loading a value into the database;
Temperature.java, overrides the method of interacting with the sensors and the method to get for the temperature value;
Pollution.java, performs the same as Temperature.java but for the pollution values;
WorldValueEnum.java, is an enumeration that indicates the entities that can manifest themselves in the simulation;
WorldFactory.java, allows the instantiation of a Pollution or Temperature object as needed.

In this way, we will be able to generate new temperature or pollution values randomly, according to the instructions in WorldSimulation.java.
In particular, a random number is generated and you have 50% to change the temperature or the pollution.

# Temperature and Pollution Graphs : JFreeChart
The JFreeChart library was used for the design and correct display of the Temperature and Pollution graphs.
The use of this library is realized in the DrawChart method of WorldSimulation, using the data contained in the DB and extracted through loadAVG, which allows to obtain the average daily values of Temperature and Pollution.
