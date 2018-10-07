package xmlParsing;


import java.util.HashMap;

/**
 * Created by George on 16/05/2018.
 */
public class RunParsing {
    public static void main(String args[]){
        SAXParserForMovies saxParserForMovies = new SAXParserForMovies();
        saxParserForMovies.runParsing();
        SAXParserForStars saxParserForStars = new SAXParserForStars();
        HashMap<String,String> name_id_map = saxParserForStars.runParsing();
        SAXParserForCasts saxParserForCasts = new SAXParserForCasts();
        saxParserForCasts.runParsing(name_id_map,saxParserForStars.getCurrentMaxStarId());
    }
}
