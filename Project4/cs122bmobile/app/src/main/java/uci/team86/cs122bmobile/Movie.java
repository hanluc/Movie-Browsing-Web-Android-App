package uci.team86.cs122bmobile;

import java.util.ArrayList;

public class Movie {
    private String name;
    private Integer birthYear;

    private String movieid;
    private String title;
    private String year;             //Integer & int diff
    private String director;
    private ArrayList<String> genres = new ArrayList<>();
    private ArrayList<String> stars = new ArrayList<>();

    public Movie(String name, int birthYear) {
        this.name = name;
        this.birthYear = birthYear;
    }
    public Movie( String movieid,String title,String year,String director,ArrayList<String> genres, ArrayList<String> stars){
        this.movieid = movieid;
        this.title = title;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
    }
    public Movie( String title,String year,String director,ArrayList<String> genres, ArrayList<String> stars){
        this.title = title;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
    }

    public String getName() {
        return name;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public String getTitle(){
        return this.title;
    }

    public String getYear() {
        return year;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public String getDirector() {
        return director;
    }

    public ArrayList<String> getStars() {
        return stars;
    }

    public void setMovieid(String movieid) {
        this.movieid = movieid;
    }

    public static void main(String[] args){
        ArrayList<String> a = new ArrayList<>();
        a.add("a");
        a.add("b");
        a.add("c");
        System.out.println(a.toString());  // [a, b, c]
    }
}
