package uci.team86.cs122bmobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class SingleMovieActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);

        Bundle bundle = getIntent().getExtras();

        TextView movieTitleView = (TextView)findViewById(R.id.textViewTitle);
        TextView yearView = (TextView)findViewById(R.id.textViewYear);
        TextView directorView = (TextView)findViewById(R.id.textViewDirector);
        TextView genresView = (TextView)findViewById(R.id.textViewGenres);
        TextView starsView = (TextView)findViewById(R.id.textViewStars);

        movieTitleView.setText(bundle.getString("title"));
        yearView.setText(yearView.getText()+":"+bundle.getString("year"));
        directorView.setText(directorView.getText()+":"+bundle.getString("director"));
        ArrayList g = bundle.getParcelableArrayList("genres");  //hasbeen serialized by intent.putExtra(
        if(g.size()!= 0) {
            genresView.setText(genresView.getText()+": "+g.toString());      //get ArrayList
        }
        ArrayList s = bundle.getParcelableArrayList("stars");
        if(s.size()!= 0) {
            starsView.setText(starsView.getText()+": "+s.toString());      //get ArrayList
        }

    }



}
