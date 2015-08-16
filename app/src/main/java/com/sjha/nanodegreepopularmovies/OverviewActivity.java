package com.sjha.nanodegreepopularmovies;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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


public class OverviewActivity extends Activity {

    private TextView titleTxt;
    private TextView releaseDateTxt;
    private TextView durationTxt;
    private ImageView poster;
    private TextView  overviewTxt;
    private TextView voteTxt;
    private Button reviewBtn;
    private Activity mActivity = this;
    private Context mContext = this;
    private ListView trailerList;
    private ImageButton markButton;
    private TextView markFavTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        Intent intent = getIntent();
        final Movie mMovie = (Movie) intent.getSerializableExtra(Constants.DETAIL_INTENT);
        initialize(mMovie);
        populateDate(mMovie);
        GetTrailers getTrailers = new GetTrailers();
        getTrailers.execute(mMovie.getId());
    }
    private void initialize(final Movie  movieObject){
        titleTxt = (TextView)findViewById(R.id.titleText);
        releaseDateTxt = (TextView)findViewById(R.id.txtReleaseDate);
        durationTxt = (TextView)findViewById(R.id.txtDuration);
        poster = (ImageView) findViewById(R.id.posterImage);
        overviewTxt = (TextView)findViewById(R.id.txtOverview);
        voteTxt = (TextView)findViewById(R.id.txtVoteAverage);
        reviewBtn = (Button)findViewById(R.id.reviewBtn);
        markButton = (ImageButton) findViewById(R.id.markMovie);
        markFavTxt = (TextView)findViewById(R.id.markFavTxt);
    }

    private void populateDate(final Movie movieObject){
        titleTxt.setText(movieObject.getTitle());
        releaseDateTxt.setText(movieObject.getReleaseDate());
        durationTxt.setText("Popularity: " + movieObject.getPopularity());
        Picasso.with(getBaseContext()).load(Constants.IMAGE_URL+movieObject.getPosterPath()).fit().into(poster);
        overviewTxt.setText(movieObject.getOverview());
        voteTxt.setText("Rating: "+ movieObject.getVoteAverage().toString()+ "/10");
        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reviewActivity = new Intent(mActivity, ReviewActivity.class);
                reviewActivity.putExtra(Constants.REVIEW_INTENT, movieObject);
                mActivity.startActivity(reviewActivity);
            }
        });
        markButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                if(markFavTxt.getText() == getString(R.string.markedText)) {
                    markButton.setBackground(getDrawable(R.color.background_green));
                    markFavTxt.setText(getString(R.string.markText));
                }else{
                    markButton.setBackground(getDrawable(R.color.gold));
                    markFavTxt.setText(getString(R.string.markedText));
                }
            }
        });
    }

    public class GetTrailers extends AsyncTask<String, Void, ArrayList<Trailer>> {
        ProgressDialog pd = new ProgressDialog(OverviewActivity.this);
        @Override
        protected ArrayList<Trailer> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String trailerData = null;
            try {
                Uri buildUri = Uri.parse(Constants.URL_REVIEW_PATH + params[0] + Constants.VIDEO_PARAM + Constants.API_REVIEW_PARAM + Constants.API_KEY).buildUpon().build();
                URL url = new URL(buildUri.toString());
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
                trailerData = buffer.toString();
                Log.v(Constants.LOG_TAG, "Trailer Data " + trailerData);
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "Error", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(Constants.LOG_TAG, "Error Closing stream", e);
                    }
                }
            }
            try {
                return getTrailerData(trailerData);
            } catch (JSONException e) {
                Log.e(Constants.LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }


        private ArrayList<Trailer> getTrailerData(String trailerData) throws JSONException {
            JSONObject trailerObject = new JSONObject(trailerData);
            JSONArray trailerArray = trailerObject.getJSONArray(Constants.RESULTS);
            ArrayList<Trailer> trailerCollection = new ArrayList<>();
            for (int i = 0; i < trailerArray.length(); i++) {
                Trailer trailer = new Trailer();
                JSONObject trailerResults = trailerArray.getJSONObject(i);
                trailer.setKey(trailerResults.getString(Constants.KEY));
                trailer.setType(trailerResults.getString(Constants.TYPE));
                trailer.setName(trailerResults.getString(Constants.NAME));
                trailerCollection.add(trailer);
            }
            return trailerCollection;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage(getString(R.string.txtloading));
            pd.show();

        }
        @Override
        protected void onPostExecute(final ArrayList<Trailer> trailer) {
            Log.d(Constants.LOG_TAG, trailer.toString());
            trailerList = (ListView) findViewById(R.id.listViewTrailer);
            trailerList.setAdapter(new TrailerListAdapter(mContext ,trailer));
            trailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Trailer trailerPosObject = trailer.get(position);
                    String key = trailerPosObject.getKey();
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.API_TRAILERS + key));
                    startActivity(i);
                }
            });
            pd.dismiss();

        }
    }
}
