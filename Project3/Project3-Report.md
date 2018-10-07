# Project 3 Optimization Report

## 1. Use Batch Insert
In the mains243.xml, I will collect the movie information from XML file. When I get one record about movie, I do not insert it into database immediately but I use batch insert. After collecting all the movies data, I insert all of them together. It will be more fast than inserting them one by one.

## 2. Use Loading data file
As for actors63.xml and casts124.xml, I need to collect informtion for stars table and stars_in_movies table.

Because there are lots of information in the casts124.xml, I first wtite them to the csv file when I collect them. After collecting all data, I load the csv file into the database.

Load file is faster than inserting them one by one


## 3. Define More Effective Checking Processing
When we parse mains243.xml, we may meet the new genres. Not like the movie, we need to check every movies because they are unique, genres are shared by movies. Movies may have many genres and these genres are also shared by other movies. If we check every genres of every movies, it will cause more traffic between tomcat and mysql. 

So, I first use a **SET** to maintain all the genres in mains243.xml.
After collect all the genres, I only check the genres in the set and genreate a **hashmap(genrename, genreId)**. Then I use this hashmap to map the genres of movies to insert into genres_in_movies


### After all of the optimization, the parsing processing will be redeced to 1 minute.
