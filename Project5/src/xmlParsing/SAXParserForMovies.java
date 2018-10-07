package xmlParsing;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXParserForMovies extends DefaultHandler{


    List<Movie> movies;

    private String tempVal;

    //to maintain context
    private Movie tempMovie;
    private String currentDirectorName; //record the current director's name

    //map the genres and id
    HashMap<String,Integer> genre_id_map;
    //use set to store all genres (make sure there is no duplicate)
    Set<String> allGenres;

    public SAXParserForMovies(){
        movies = new ArrayList<>();
        allGenres = new HashSet<>();
        genre_id_map = new HashMap<>();
    }

    public void runParsing(){
        parseDocument();
        print();
        addNewGenres();
        addNewMovies(movies);
    }

    private void print(){
        System.out.println("Number of Movies '" + movies.size() + "'.");
//        Iterator<Movie> it = movies.iterator();
//        while (it.hasNext()) {
//            Movie movie = it.next();
//            System.out.println(movie.toString());
//
//        }
    }

    private void parseDocument() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("mains243.xml",this);
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
        if(qName.equalsIgnoreCase("film")){
            tempMovie = new Movie();
            tempMovie.setDirector(currentDirectorName);
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }


    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(qName.equalsIgnoreCase("film")){
            movies.add(tempMovie);
        }else if(qName.equalsIgnoreCase("dirname")){
            //update the new director
            currentDirectorName = tempVal;
        }else if(qName.equalsIgnoreCase("fid")){
            tempMovie.setId(tempVal);
        }else if(qName.equalsIgnoreCase("t")){
            tempMovie.setTitle(tempVal);
        }else if(qName.equalsIgnoreCase("year")){
            tempMovie.setYear(tempVal);
        }else if(qName.equalsIgnoreCase("cat")){
            tempMovie.getGenres().add(tempVal);
            allGenres.add(tempVal.toLowerCase());
        }
    }

    private void addNewGenres(){
        try {
            //get present number of genres id
            Connection connection = DatabaseConnector.getConnection();
            String genresNumSql = "select count(id) as id_num from genres";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(genresNumSql);
            //get the present number in genres table
            int presentNumber = 0;
            while (resultSet.next()) {
                presentNumber = resultSet.getInt("id_num");
            }
            statement.close();
            //test if the new genre is already in the database if no add it to database
            Iterator<String> it = allGenres.iterator();
            while (it.hasNext()){
                String genre = it.next();
                if(!genre.equals("")) {
                    //test if there is already such genre
                    PreparedStatement preparedStatement = connection.prepareStatement("select id from genres where name = ?");
                    preparedStatement.setString(1, genre);
                    ResultSet resultSet1 = preparedStatement.executeQuery();
                    int genreID = -1;
                    while (resultSet1.next()) {
                        genreID = resultSet1.getInt("id");
                    }
                    if (genreID == -1) {
                        //if genreID is still -1 there is no such genre we need to create new one
                        presentNumber++; // first add the index
                        genreID = presentNumber;
                        System.out.println("There is no genre: " +genre+"  create new and id = "+genreID);
                    }else {
                        System.out.println("Genre: " + genre + " is already in database id = " + genreID);
                    }
                    //add to the hashmap
                    genre_id_map.put(genre,genreID);
                }
            }
            connection.close();
            //add these new genres into databases
            Connection connection1 = DatabaseConnector.getConnection();
            connection1.setAutoCommit(false);
            PreparedStatement preparedStatement = connection1.prepareStatement("insert ignore into genres (id,name) values(?,?)");
            Iterator it1 = genre_id_map.entrySet().iterator();
            while (it1.hasNext()){
                Map.Entry pair = (Map.Entry)it1.next();
                preparedStatement.setInt(1,(Integer)pair.getValue());
                preparedStatement.setString(2,(String)pair.getKey());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            connection1.commit();
            System.out.println("Add all new genres!");
            preparedStatement.close();
            connection1.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addNewMovies(List<Movie> movies){
        String searchSql = "select * from movies where title = ? and year = ?";

        String insertMovieSql = "insert ignore into movies (id,title,year,director) values (?,?,?,?)";

        String insertMovieGenreSql = "insert ignore into genres_in_movies (genreId,movieId) values (?,?)";

        try {
            //check movie if exists
            Connection connection1 = DatabaseConnector.getConnection();
            PreparedStatement statementForSearch = connection1.prepareStatement(searchSql);
            //create insert movie
            Connection connection2 = DatabaseConnector.getConnection();
            connection2.setAutoCommit(false);
            PreparedStatement statementForInsertMovie = connection2.prepareStatement(insertMovieSql);
            //create insert movie-genre
            Connection connection3 = DatabaseConnector.getConnection();
            connection3.setAutoCommit(false);
            PreparedStatement statementForInsertMovieGenre = connection3.prepareStatement(insertMovieGenreSql);
            //
            Iterator<Movie> it = movies.iterator();
            while (it.hasNext()) {
                Movie movie = it.next();
                //first we need to check if there is such movie in database
                //I use the rule that the movie have the same title and same year is the same movie
                //I do not use the director's name because there maybe little different between two data sources
                //use movie tile and year
                statementForSearch.setString(1,movie.getTitle());
                statementForSearch.setString(2,movie.getYear());
                ResultSet searchResult = statementForSearch.executeQuery();
                //if there is no such movie
                if(!searchResult.isBeforeFirst()){
                    //add movie
                    statementForInsertMovie.setString(1,movie.getId());
                    statementForInsertMovie.setString(2,movie.getTitle());
                    statementForInsertMovie.setString(3,movie.getYear());
                    statementForInsertMovie.setString(4,movie.getDirector());
                    statementForInsertMovie.addBatch();
                    //add movie-genre
                    Iterator<String> genres = movie.getGenres().iterator();
                    while (genres.hasNext()){
                        String genre = genres.next();
                        //System.out.println(genre);
                        if(!genre.equals("")){
                            //System.out.println(genre_id_map.get(genre.toLowerCase())+"  " + movie.getId());
                            statementForInsertMovieGenre.setInt(1,genre_id_map.get(genre.toLowerCase()));
                            statementForInsertMovieGenre.setString(2,movie.getId());
                            statementForInsertMovieGenre.addBatch();
//                            if(genre.equalsIgnoreCase("porn")){
//                                System.out.println(movie.getId());
//                                System.out.println(genre_id_map.get(genre.toLowerCase()));
//                            }
                        }
                    }
                }else {
                    System.out.println("Movie " + movie.getTitle() + " is already in database!");
                }
            }
            //commit changes
            statementForInsertMovie.executeBatch();
            connection2.commit();
            statementForInsertMovieGenre.executeBatch();
            connection3.commit();

            statementForSearch.close();
            statementForInsertMovie.close();
            statementForInsertMovieGenre.close();
            connection1.close();
            connection2.close();
            connection3.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
