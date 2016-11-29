package com.example.miky.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miky.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(DetailActivity.EXTRA_MOVIE)) {
            Movie movie = (Movie)intent.getSerializableExtra(DetailActivity.EXTRA_MOVIE);
            TextView titleView = (TextView)view.findViewById(R.id.movie_title);
            titleView.setText(movie.getTitle());
            TextView plotView = (TextView)view.findViewById(R.id.movie_plot);
            plotView.setText(movie.getPlot());
            TextView dateView = (TextView)view.findViewById(R.id.movie_date);
            dateView.setText(movie.getDate());
            ImageView posterView = (ImageView)view.findViewById(R.id.movie_image);
            Picasso.with(getActivity()).load(movie.getPhotoUrl()).into(posterView);
        } else {
            getActivity().finish();
        }

        return view;
    }
}
