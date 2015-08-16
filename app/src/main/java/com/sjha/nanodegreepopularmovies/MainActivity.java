package com.sjha.nanodegreepopularmovies;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends Activity {

    private Context mContext = this;
    private SwipeRefreshLayout mSwipeContainer;
    private GridView mMoviesList;
    private String mSortBy;

    private SwipeRefreshLayout.OnRefreshListener mOnPullToRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh () {
            mSwipeContainer.setRefreshing(false);
            if(isNetworkConnected()){
                mSwipeContainer.setVisibility(View.VISIBLE);
                GetMoviesFromURL getMoviesFromURL = new GetMoviesFromURL();
                getMoviesFromURL.execute(mSortBy);
            }else{
                Toast.makeText(mContext, getString(R.string.noConnection), Toast.LENGTH_SHORT).show();
            }
        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        if(isNetworkConnected()){
            mSwipeContainer.setVisibility(View.VISIBLE);
            GetMoviesFromURL getMoviesFromURL = new GetMoviesFromURL();
            getMoviesFromURL.execute(mSortBy);
        }else{
            Toast.makeText(mContext, getString(R.string.noConnection), Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mMoviesList.setNumColumns(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ?4  : 2);
        super.onConfigurationChanged(newConfig);
    }




    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    private void initialize(){

        mSortBy = Constants.SORT_OPTION_POP;
        ImageButton mSettingBtn = (ImageButton)findViewById(R.id.btnSetting);
        mSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                View view = mInflater.inflate(R.layout.dialog_list_view, null);

                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(view);

                Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        dialog.dismiss();
                    }

                });

                String[] sortLabel = {getString(R.string.mostPopularLbl),
                        getString(R.string.highestRatedLbl),
                        getString(R.string.revenueLbl),
                        getString(R.string.releaseDateLbl),
                        getString(R.string.originalTitleLbl),
                        getString(R.string.primaryReleaseDateLbl)};
                final ListView listConference = (ListView) view.findViewById(R.id.sortLabelList);
                listConference.setAdapter(new DialogAdapter(mContext, R.layout.list_item_sort, R.id.sortText, sortLabel));
                listConference.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        mSortBy = listConference.getItemAtPosition(position).toString();
                        GetMoviesFromURL getMoviesFromURL = new GetMoviesFromURL();
                        if (mSortBy.equals(getString(R.string.mostPopularLbl))) {
                            mSortBy = Constants.SORT_OPTION_POP;
                            getMoviesFromURL.execute(mSortBy);
                        } else if (mSortBy.equals(getString(R.string.highestRatedLbl))) {
                            mSortBy = Constants.SORT_OPTION_RATING;
                            getMoviesFromURL.execute(mSortBy);
                        } else if (mSortBy.equals(getString(R.string.revenueLbl))) {
                            mSortBy = Constants.SORT_OPTION_REVENUE;
                            getMoviesFromURL.execute(mSortBy);
                        } else if (mSortBy.equals(getString(R.string.releaseDateLbl))) {
                            mSortBy = Constants.SORT_OPTION_RELEASE_DATE;
                            getMoviesFromURL.execute(mSortBy);
                        }else if (mSortBy.equals(getString(R.string.originalTitleLbl))) {
                            mSortBy = Constants.SORT_OPTION_ORIGINAL_TITLE;
                            getMoviesFromURL.execute(mSortBy);
                        }else if (mSortBy.equals(getString(R.string.primaryReleaseDateLbl))) {
                            mSortBy = Constants.SORT_OPTION_PRIMARY_RELEASE_DATE;
                            getMoviesFromURL.execute(mSortBy);
                        }
                            dialog.dismiss();
                        }

                    }

                    );

                    dialog.show();

                }
            }

            );
            mMoviesList=(GridView)findViewById(R.id.moviesList);
            mSwipeContainer=(SwipeRefreshLayout)findViewById(R.id.swipeContainer);
            mSwipeContainer.setColorSchemeResources(R.color.blue_dark);
            mSwipeContainer.setOnRefreshListener(mOnPullToRefreshListener);

        }


        public class GetMoviesFromURL extends AsyncTask<String, Void, ArrayList<Movie>> {

        ProgressDialog pd = new ProgressDialog(MainActivity.this);



        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieData = null;

            try{
                Uri buildUri = Uri.parse(Constants.URL_PATH + params[0] + Constants.API_Param + Constants.API_KEY).buildUpon().build();
                URL url = new URL(buildUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieData = buffer.toString();

                Log.v(Constants.LOG_TAG, "Movie Data: " + movieData);

            }catch (IOException e){
                Log.e(Constants.LOG_TAG,"Error", e);
            }finally{
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if(reader != null){
                    try{
                        reader.close();
                    }catch (final IOException e){
                        Log.e(Constants.LOG_TAG, "Error Closing stream", e);
                    }
                }
            }



            try{
                return getMovieData(movieData);
            }catch (JSONException e){
                Log.e(Constants.LOG_TAG, e.getMessage(), e);
                e.printStackTrace();

            }


            return null;

            
        }


        private ArrayList<Movie> getMovieData(String movieData) throws JSONException {



            JSONObject movieObject  = new JSONObject(movieData);
            JSONArray movieArray = movieObject.getJSONArray(Constants.RESULTS);

            ArrayList<Movie> pictureCollection = new ArrayList<>();
            for(int i = 0; i <movieArray.length(); i++){
                Movie movie = new Movie();

                JSONObject movieResults = movieArray.getJSONObject(i);
                movie.setId(movieResults.getString(Constants.ID));
                movie.setBackdropPath(movieResults.getString(Constants.BACKDROP_PATH));
                movie.setTitle(movieResults.getString(Constants.TITLE));
                movie.setOverview(movieResults.getString(Constants.OVERVIEW));
                movie.setPopularity(movieResults.getString(Constants.POPULARITY));
                movie.setReleaseDate(movieResults.getString(Constants.RELEASE_DATE));
                movie.setVoteAverage(movieResults.getDouble(Constants.VOTE_AVERAGE));
                movie.setVoteCount(movieResults.getString(Constants.VOTE_COUNT));
                movie.setPosterPath(movieResults.getString(Constants.POSTER_PATH));


                pictureCollection.add(movie);

            }


            return pictureCollection;

        }

        public GetMoviesFromURL() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage(getString(R.string.txtloading));
            pd.show();

        }

        @Override
        protected void onPostExecute(final ArrayList<Movie> movies) {
            String[] urlArray = new String[movies.size()];
            if(movies != null) {

                for (int i = 0; i < movies.size(); i++) {
                    urlArray[i] = (Constants.IMAGE_URL + movies.get(i).getPosterPath());
                }
                mMoviesList.setAdapter(new ImageAdapter(MainActivity.this, urlArray));
                mMoviesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent detail = new Intent(mContext, OverviewActivity.class);
                        Movie movieObject =  movies.get(position);
                        detail.putExtra(Constants.DETAIL_INTENT, movieObject);
                        mContext.startActivity(detail);
                    }
                });
            }
            pd.dismiss();

        }
    }

}


