package com.sjha.nanodegreepopularmovies;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

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


public class ReviewActivity extends Activity{

    private String mId;
    private Movie mMovie;
    private Context mContext = this;
    private TextView mNoReview;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Intent getIntent = getIntent();
        mMovie = (Movie) getIntent.getSerializableExtra(Constants.REVIEW_INTENT);
        mId = mMovie.getId();
        GetReviews getReviews = new GetReviews();
        getReviews.execute(mId);

    }








    public class GetReviews extends AsyncTask<String, Void, ArrayList<Review>>{

        ProgressDialog pd = new ProgressDialog(ReviewActivity.this);


        @Override
        protected ArrayList<Review> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String reviewData = null;

            try{
                Uri buildUri = Uri.parse(Constants.URL_REVIEW_PATH + params[0] + Constants.REVIEW_PARAM +Constants.API_REVIEW_PARAM + Constants.API_KEY).buildUpon().build();
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
                reviewData = buffer.toString();

                Log.v(Constants.LOG_TAG, "Review Data " + reviewData);

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
                return getReviewData(reviewData);
            }catch (JSONException e){
                Log.e(Constants.LOG_TAG, e.getMessage(), e);
                e.printStackTrace();

            }


            return null;


        }


        private ArrayList<Review> getReviewData(String reviewData) throws JSONException {



            JSONObject reviewObject  = new JSONObject(reviewData);
            JSONArray reviewArray = reviewObject.getJSONArray(Constants.RESULTS);
            int totalResults = reviewObject.getInt(Constants.TOTAL_RESULTS);

            ArrayList<Review> reviewCollection = new ArrayList<>();
            for(int i = 0; i <reviewArray.length(); i++){
                Review review = new Review();

                JSONObject reviewResults = reviewArray.getJSONObject(i);
                review.setAuthor(reviewResults.getString(Constants.AUTHOR));
                review.setContent(reviewResults.getString(Constants.CONTENT));
                review.setUrl(reviewResults.getString(Constants.URL));
                review.setTotalResult(totalResults);
                reviewCollection.add(review);

            }


            return reviewCollection;

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage(getString(R.string.txtloading));
            pd.show();
        }

        @Override
        protected void onPostExecute(final ArrayList<Review> review) {
            Log.d(Constants.LOG_TAG, review.toString());
            ReviewAdapter reviewAdapter = new ReviewAdapter(mContext, review);
            listView = (ListView) findViewById(R.id.reviewList);
            listView.setAdapter(reviewAdapter);
            pd.dismiss();
        }
    }


}

