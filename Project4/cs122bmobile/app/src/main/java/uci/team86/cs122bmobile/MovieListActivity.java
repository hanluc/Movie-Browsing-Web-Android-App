package uci.team86.cs122bmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieListActivity extends AppCompatActivity {

    Button previous;
    Button next;
    TextView pageText;

    String searchContent;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        previous = (Button)findViewById(R.id.Previous);
        next = (Button)findViewById(R.id.Next);

        pageText = (TextView)findViewById(R.id.textViewPageNo);


        //from last intent
        //get info from search board activity
        Bundle bundle = getIntent().getExtras();
        String msg = bundle.getString("searchContent");   //need to define
        searchContent = msg;

        //pretend to have the query to ask: inflate the request contenet
        String url = "https://54.153.65.169:8443/Project4/Search"; //need change
        final Map<String,String> params = new HashMap<String,String>();

        if (msg != null && !"".equals(msg)) {
            //((TextView) findViewById(R.id.last_page_msg_container)).setText(msg);
            params.put("searchData",msg);
        }
        params.put("searchType","1");
        params.put("start","0");
        params.put("end","5");
        params.put("sortType","0");
        params.put("username","a@email.com");
        params.put("password","a2");


        //--get http connections
        //----1. get the queue
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        //---2.request
        //JSONArrayRequest: null parameter
//        final JsonArrayRequest searchRequestbyjson = new JsonArrayRequest(Request.Method.GET,
//                url,
//                new JSONObject(params),
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        Log.d("response",response.toString());
//                        try{
//                            JSONArray respList = response;
//                            addMovies(respList);
//                        }catch(Exception e){
//                            e.printStackTrace();
//                        }
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d("error in request",error.toString());
//                    }
//                }
//        );

        Log.d("search params: ",params.toString());

        final StringRequest searchRequest = new StringRequest(Request.Method.POST,   //cannnot use GET method.
                "https://54.153.65.169:8443/Project4/Search",
                //--2.2 success fucntion: create & add items into list
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("search response",response);
                        try{
                            JSONArray respList = new JSONArray(response.toString());
                            addMovies(respList);
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("security.error", error.toString());
                    }
                })
        {
            @Override
            //set params in the function
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.d("In request params: ",params.toString());
                return params;
            }
        };

        queue.add(searchRequest);
        //addMovies();

    }

    /**
     * @param int pageNoint, String searchContent, ? boolean if success ?
     *  return JSONArray
     */
    public void sendSearchRequest(int pageNoint, String searchContent ){

        //set params:
        final int pageNofinal=pageNoint;
        int start = (pageNoint-1)*5;
        int end = start+5;
        System.out.println("start "+start+"; end: "+end);

        String url = "https://54.153.65.169:8443/Project4/Search"; //need change
        final Map<String,String> params = new HashMap<String,String>();
        JSONArray movielist = new JSONArray();
        if (searchContent != null && !"".equals(searchContent)) {
            params.put("searchType","1");
            //set offset&limit
            params.put("start",Integer.toString(start));
            params.put("end",Integer.toString(end));
            params.put("sortType","0");
            params.put("searchData",searchContent);
        }

        //--get http connections
        //----1. get the queue
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        //---2.request
        final StringRequest searchRequest = new StringRequest(Request.Method.POST,
                url,
                //--2.2 success fucntion: create & add items into list
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response",response);
                        try{
                            JSONArray respList = new JSONArray(response.toString());
                            if(!response.equals("")&&response!=null&&respList.get(0)!=null){
                                addMovies(respList);
                                pageText.setText(Integer.toString(pageNofinal));
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"this is the last page!",Toast.LENGTH_SHORT).show();
                            }

                        }catch(Exception e){

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("security.error", error.toString());
                    }
                })
        {
            @Override
            //set params in the function
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        queue.add(searchRequest);

    }

    /** CALLBACK fuction for searchrequest
     * --@ param JSONArray movielist--(no param now)
     * do: inflate the activity's list
     */
    public void addMovies(JSONArray movieJArray){

        //1. Create Movielist----------------------- (not now：by jsonarray)
        final ArrayList<Movie> movies = new ArrayList<>();
        //for loop:
        //      create a Movie Class to store the movie info

        try{
            //add movie to arraylist
            for(int i=0;i<movieJArray.length();i++){
                JSONObject movie = movieJArray.getJSONObject(i);

                String title = movie.getString("title");
                String year = movie.getString("year");
                String director = movie.getString("director");
                JSONArray genresJA = movie.getJSONArray("genres");
                JSONArray starsJA = movie.getJSONArray("starlist");
                ArrayList<String> genres = new ArrayList<>();
                ArrayList<String> stars = new ArrayList<>();

                for(int j=0;j<genresJA.length();j++){
                    JSONObject genre = genresJA.getJSONObject(j);
                    String genrename = genre.getString("name");
                    genres.add(genrename);
                }

                for(int j=0;j<starsJA.length();j++){
                    JSONObject star = starsJA.getJSONObject(j);
                    String starname = star.getString("name");
                    stars.add(starname);
                }

                Movie new_movie = new Movie(title,year,director,genres,stars);
                movies.add(new_movie);
            }

        }catch(Exception e)
        {
            e.printStackTrace();
        }


        //2. Declare adapter to inflate the list
        MovieListViewAdapter adapter = new MovieListViewAdapter(movies, this);

        //3. Get the list
        ListView listView = (ListView)findViewById(R.id.list);
        //3.1 Set the list adapter; set itself as the parent
        listView.setAdapter(adapter);

        //3.2 Set OnClickListener to items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movies.get(position);
                String message = String.format("Clicked on position: %d, name: %s", position, movie.getTitle());
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                gotoSingleMovie(movie.getTitle(),movie.getYear(),movie.getDirector(),movie.getGenres(), movie.getStars());

            }
        });

        //pageText.setText(Integer.toString(pageNoint));

    }


    //for debug
    public void addMovies(){

        //1. Create Movielist----------------------- (not now：by jsonarray)
        final ArrayList<Movie> movies = new ArrayList<>();
        //1.1 Add Movie instances to list
        movies.add(new Movie("Peter Anteater", 1965));
        movies.add(new Movie("John Doe", 1975));
        //for loop:   (not now)
        //      create a Movie Class to store the movie info

        //2. Declare adapter to inflate the list
        MovieListViewAdapter adapter = new MovieListViewAdapter(movies, this);

        //3. Get the list
        ListView listView = (ListView)findViewById(R.id.list);
        //3.1 Set the list adapter; set itself as the parent
        listView.setAdapter(adapter);

        //3.2 Set OnClickListener to items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movies.get(position);
                String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getBirthYear());

                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                gotoSingleMovie(movie.getName(),Integer.toString(movie.getBirthYear()),movie.getDirector(),movie.getGenres(), movie.getStars());
            }
        });

    }

    //OnClick of single movie:
    public void gotoSingleMovie(String title,String year,String director,ArrayList<String> genres, ArrayList<String> stars){
        Intent intent = new Intent(this,SingleMovieActivity.class);

        intent.putExtra("title",title);
        intent.putExtra("year",year);
        intent.putExtra("director",director);
        intent.putExtra("genres",genres); //put ArrayList
        intent.putExtra("stars",stars);   //put ArrayList

        startActivity(intent);
    }


    //OnClick: at [Previous] Button
    public void doPrev(View view){

        //get the page&changeit
        String pageNo = pageText.getText().toString();
        int pageNoint = 1;
        if(pageNo!=null&&!pageNo.equals(""))
            pageNoint = Integer.parseInt(pageNo);
        pageNoint --;
        if(pageNoint<1){
            Toast.makeText(getApplicationContext(),"this is the first page!",Toast.LENGTH_SHORT).show();
            pageNoint = 1;
        }
        pageText.setText(Integer.toString(pageNoint));

        //reload the list
        Log.d("reload the list","get previous one");
        sendSearchRequest(pageNoint, searchContent );
        //addMovies();
        Log.d("reload the list","reload success! it will not get the list being appended.");
    }

    public void doNext(View view){

        //get the page&changeit
        String pageNo = pageText.getText().toString();
        int pageNoint = 1;
        if(pageNo!=null&&!pageNo.equals(""))
            pageNoint = Integer.parseInt(pageNo);
        pageNoint ++;

        /** to check the max page
        if(pageNoint<1){
            Toast.makeText(getApplicationContext(),"this is the last page!",Toast.LENGTH_SHORT).show();
            pageNoint = 1;
        }
         */

        //pageText.setText(Integer.toString(pageNoint));

        //reload the list
        Log.d("reload the list","get next one");
        //send request
        sendSearchRequest(pageNoint, searchContent );
        //addMovies();
        Log.d("reload the list","reload success! it will not get the list being appended.");
    }

    public void toSearch(View view){
        Intent intent = new Intent(this,SearchActivity.class);

        //intent.putExtra("searchContent",searchContent);

        startActivity(intent);

    }


}
