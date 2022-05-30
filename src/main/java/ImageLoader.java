import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static javax.imageio.ImageIO.read;
/**
 * Classe utilizzata per il caricamento di immagini all'interno dell'applicazione.
 * In particolare, le immagini sono usate per mostrare la mappa dei punti sensore.
 */
public class ImageLoader
{
    public static void imgLoad (URL url)
    {
        try(InputStream in = url.openStream()){
            Files.copy(in, Paths.get(System.getProperty("user.dir") + "./src/img/localMap.jpg"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Metodo che permette il caricamento della corretta immagine della mappa.
     * Se non ci sono sensori inseriti, allora mostrare l'immagine localDefault, altrimenti mostrare la mappa dei sensori.
     */
    public static ImageIcon imgDraw(String url)
    {
        Image image = null;

        try {
            if (new File(url).exists())
            {
                image = read(new File(url));
            }
            else
            {
                image = read(new File(System.getProperty("user.dir") + "./src/img/localDefault.jpg"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ImageIcon(image);

    }
}