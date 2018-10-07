package xmlParsing;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;


public class SAXParserForStars extends DefaultHandler{
    List<Star> stars;

    private String tempVal;

    //to maintain context
    private Star tempStar;

    //to map id and name
    private HashMap<String,String> name_id_map;
    private static int currentMaxStarId; //record the current max star id

    //csv writer
    private PrintWriter pw;
    StringBuilder sb;
    public SAXParserForStars(){
        stars = new ArrayList<>();
        name_id_map = new HashMap<>();

        try {
            //create csv writer
            pw = new PrintWriter(new File("stars.csv"));
            sb = new StringBuilder();
            sb.append("id");
            sb.append(",");
            sb.append("name");
            sb.append(",");
            sb.append("birthYear");
            sb.append("\n");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setCurrentMaxStarId(){
        try {
            Connection connection = DatabaseConnector.getConnection();
            String sql = "select max(id) as max_id from stars";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            String maxId = "";
            while (resultSet.next()){
                maxId = resultSet.getString("max_id");
            }
            maxId = maxId.substring(2);//cut the nm
            currentMaxStarId = Integer.parseInt(maxId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public HashMap<String,String> runParsing(){
        System.out.println("Begin to Parsing casts!!!!");

        setCurrentMaxStarId();
        parseDocument();
        //finish write csv
        pw.write(sb.toString());
        pw.close();
        //
        print();
        loadDataIntoStar();
        return name_id_map;
    }

    public void print(){
        Iterator<Star> it = stars.iterator();
        while (it.hasNext()) {
            Star star = it.next();
            System.out.println(star.toString());

        }
    }
    private void parseDocument() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("actors63.xml",this);
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
        if(qName.equalsIgnoreCase("actor")){
            tempStar = new Star();
            tempStar.setId("nm"+ ++currentMaxStarId);
            sb.append("nm" + currentMaxStarId);
            sb.append(",");
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(qName.equalsIgnoreCase("actor")){
            stars.add(tempStar);
        }else if(qName.equalsIgnoreCase("stagename")){
            tempStar.setName(tempVal);
            name_id_map.put(tempVal, tempStar.getId());
            sb.append(tempStar.getName());
            sb.append(",");
        }else if(qName.equalsIgnoreCase("dob")){
            tempStar.setBirthyear(tempVal);
            sb.append(tempStar.getBirthyear());
            sb.append("\n");
        }

    }


    public void loadDataIntoStar(){
        //nm9423080
        if(currentMaxStarId > 9429943) {
            System.out.println("You have loaded new actor data into the database!");
        }else {
            try{
                Connection connection = DatabaseConnector.getConnection();
                Statement statement = connection.createStatement();
                String sql = "LOAD DATA LOCAL INFILE 'stars.csv' INTO TABLE stars FIELDS TERMINATED BY ',' LINES TERMINATED BY '\\n' ignore 1 lines " +
                        " (id,name,@birthYear) SET birthYear = NULLIF (@birthYear, '')";
                System.out.println(sql);
                statement.execute(sql);
                statement.close();
                connection.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public int getCurrentMaxStarId(){
        return currentMaxStarId;
    }
}
