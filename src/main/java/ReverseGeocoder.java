import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
/**
 * Classe utilizzata per la geocodifica inversa tramite hereapi.
 * Permette di ottenere il nome di una strada, date le sue coordinate.
 */
public class ReverseGeocoder {

    private static final String GEOCODING_RESOURCE = "https://revgeocode.search.hereapi.com/v1/revgeocode";
    private static final String API_KEY = "5FVJymcUtc6gijInnkp1pTYjK-YOoKyvpVNT8PjVBf8";

    public String ReverseGeocodeSync(String query) throws IOException, InterruptedException {

        HttpClient httpClient = HttpClient.newHttpClient();

        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

        String requestUri = GEOCODING_RESOURCE + "?apiKey=" + API_KEY + "&at=" + encodedQuery + "&lang=en-US";

        HttpRequest revGeocodingRequest = HttpRequest.newBuilder().GET().uri(URI.create(requestUri))
                .timeout(Duration.ofMillis(2000)).build();

        HttpResponse geocodingResponse = httpClient.send(revGeocodingRequest,
                HttpResponse.BodyHandlers.ofString());

        return String.valueOf(geocodingResponse.body());
    }

}