package com.example.miky.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.miky.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivityFragment extends Fragment {

    private MovieAdapter mMovieAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mMovieAdapter = new MovieAdapter(new ArrayList<Movie>());

        GridView grid = (GridView)view.findViewById(R.id.grid_view_movies);
        grid.setAdapter(mMovieAdapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie movie = mMovieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                    .putExtra(DetailActivity.EXTRA_MOVIE, movie);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String type = prefs.getString(getString(R.string.pref_key_type), getString(R.string.pref_type_key_popular));

        FetchMovieTask task = new FetchMovieTask();
        task.execute(type);
    }

    private class MovieAdapter extends ArrayAdapter<Movie> {

        public MovieAdapter(ArrayList<Movie> movies) {
            super(getActivity(), 0, movies);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_movie, null);
            }

            Movie movie = getItem(position);

            if (movie != null) {
                ImageView imageView = (ImageView) convertView.findViewById(R.id.movie_photo);
                Picasso.with(getActivity()).load(movie.getPhotoUrl()).into(imageView);
            }

            return convertView;
        }
    }

    public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private Movie[] getMoviesFromJson(String moviesJsonStr) throws JSONException {

            final String RESULTS = "results";
            final String POSTER = "poster_path";
            final String PLOT = "overview";
            final String RATING = "vote_average";
            final String DATE = "release_date";
            final String TITLE = "original_title";


            JSONObject json = new JSONObject(moviesJsonStr);
            JSONArray resultsJson = json.getJSONArray(RESULTS);
            JSONObject movieJson;
            ArrayList<Movie> movies = new ArrayList<Movie>();

            for (int i = 0; i < resultsJson.length(); i++) {
                movieJson = resultsJson.getJSONObject(i);
                Movie movie =  new Movie();
                movie.setPhotoUrl("http://image.tmdb.org/t/p/w185" + movieJson.getString(POSTER));
                movie.setTitle(movieJson.getString(TITLE));
                movie.setPlot(movieJson.getString(PLOT));
                movie.setDate(movieJson.getString(DATE));
                movie.setRating((float)movieJson.getDouble(RATING));
                movies.add(movie);
            }

            return movies.toArray(new Movie[0]);

        }

        @Override
        protected Movie[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }
            String type = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;

            try {

                final String FORECAST_BASE_URL =
                        "http://api.themoviedb.org/3/movie";

                final String APP_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendPath(type)
                        .appendQueryParameter(APP_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                mMovieAdapter.clear();
                for(Movie movie : result) {
                    mMovieAdapter.add(movie);
                }
            }
        }
    }


}
