package xmlParsing;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXParserForCasts extends DefaultHandler {

    private String tempVal;

    private HashMap<String,String> name_id_map;
    private int currentMaxStarId;
    private List<String> newStarsName;
    //
    private String tempMovieId;
    private String tempActorName;

    //csv writer for relation between stars and movies
    private PrintWriter pw;
    StringBuilder sb;

    //csv writer for new stars
    private PrintWriter pw_star;
    StringBuilder sb_star;

    public SAXParserForCasts(){
        newStarsName = new ArrayList<>();
        try {
            //create csv writer for cast
            pw = new PrintWriter(new File("casts.csv"));
            sb = new StringBuilder();
            sb.append("starId");
            sb.append(",");
            sb.append("movieId");
            sb.append("\n");
            //create csv writer for new stars
            pw_star = new PrintWriter(new File("new_stars.csv"));
            sb_star = new StringBuilder();
            sb_star.append("id");
            sb_star.append(",");
            sb_star.append("name");
            sb_star.append(",");
            sb_star.append("birthYear");
            sb_star.append("\n");

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void runParsing(HashMap<String,String> name_id_map,int currentMaxStarId){
        this.name_id_map = name_id_map;
        this.currentMaxStarId = currentMaxStarId;
        parseDocument();
        //finish write csv
        pw.write(sb.toString());
        pw.close();
        pw_star.write(sb_star.toString());
        pw_star.close();
        //load new stars
        loadNewStars();
        loadStarMovie();
        //load movie stars record
    }

    private void parseDocument() {
        System.out.println("Begin to Parsing casts!!!!");
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("casts124.xml",this);
        }catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tempVal = "";
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(qName.equalsIgnoreCase("f")){
            tempMovieId = tempVal;
        }else if(qName.equalsIgnoreCase("a")){
            tempActorName = tempVal;
            //add to csv
            String actorId = name_id_map.get(tempActorName);
            if(actorId == null) {
                if(tempActorName.startsWith("\n") && tempActorName.split("\n")[1] != null){
                    tempActorName = tempActorName.split("\n")[1];
                    System.out.println(tempActorName);
                }
                actorId = "nm" + ++currentMaxStarId;
                sb_star.append(actorId);
                sb_star.append(",");
                sb_star.append(tempActorName);
                sb_star.append(",");
                sb_star.append("");
                sb_star.append("\n");
            }
            sb.append(actorId);
            sb.append(",");
            sb.append(tempMovieId);
            sb.append("\n");
        }

    }


    public void loadNewStars(){
        try{
            Connection connection = DatabaseConnector.getConnection();
            Statement statement = connection.createStatement();
            String sql = "LOAD DATA LOCAL INFILE 'new_stars.csv' INTO TABLE stars FIELDS TERMINATED BY ',' LINES TERMINATED BY '\\n' ignore 1 lines " +
                    " (id,name,@birthYear) SET birthYear = NULLIF (@birthYear, '')";
            statement.execute(sql);
            statement.close();
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadStarMovie(){
        try{
            Connection connection = DatabaseConnector.getConnection();
            Statement statement = connection.createStatement();
            String sql = "LOAD DATA LOCAL INFILE 'casts.csv' INTO TABLE stars_in_movies FIELDS TERMINATED BY ',' LINES TERMINATED BY '\\n' ignore 1 lines ";
            statement.execute(sql);
            statement.close();
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
