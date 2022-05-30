import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Classe astratta per definire l'URL della mappa, caricando l'url secondo la documentazione di hereapi.
 */
abstract class MapURL {

    private static final String GEOVIEWER = "https://image.maps.ls.hereapi.com/mia/1.6/mapview";
    private static final String API_KEY = "5FVJymcUtc6gijInnkp1pTYjK-YOoKyvpVNT8PjVBf8";

    public static @NotNull String SelectLocation(ArrayList<String> poi) {

        String listString = String.join(", ", poi);
        String strNew = listString.replace(", ", "");

        return GEOVIEWER + "?" + strNew + "&w=512&h=512" + "&apiKey=" + API_KEY;

    }

}