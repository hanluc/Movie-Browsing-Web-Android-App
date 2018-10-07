package uci.team86.cs122bmobile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MovieListViewAdapter extends ArrayAdapter<Movie> {
    private ArrayList<Movie> movies;

    public MovieListViewAdapter(ArrayList<Movie> movies, Context context) {
        super(context, R.layout.movie_list_row, movies);
        this.movies = movies;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //initialize inflater
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.movie_list_row, parent, false);  //parent:the root viewgroup

        Movie movie = movies.get(position);

        TextView movieTitleView = (TextView)view.findViewById(R.id.movie_title);
        TextView yearView = (TextView)view.findViewById(R.id.year);
        TextView directorView = (TextView)view.findViewById(R.id.director);
        TextView genresView = (TextView)view.findViewById(R.id.genres);
        TextView starsView = (TextView)view.findViewById(R.id.stars);

        movieTitleView.setText(movie.getTitle());
        yearView.setText(movie.getYear());
        directorView.setText(movie.getDirector());
        genresView.setText(movie.getGenres().toString());
        starsView.setText(movie.getStars().toString());

        return view;
    }
}
