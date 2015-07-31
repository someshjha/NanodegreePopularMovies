package com.sjha.nanodegreepopularmovies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


public class OverviewActivity extends Activity {

    private TextView titleTxt;
    private TextView releaseDateTxt;
    private TextView durationTxt;
    private ImageView poster;
    private TextView  overviewTxt;
    private TextView voteTxt;
    private Button reviewBtn;
    private Activity mActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        Intent intent = getIntent();
        final Movie mMovie = (Movie) intent.getSerializableExtra(Constants.DETAIL_INTENT);

        initialize(mMovie);
        populateDate(mMovie);
        Toast.makeText(getBaseContext(), mMovie.getId(), Toast.LENGTH_SHORT).show();


    }

    private void initialize(final Movie  movieObject){
        titleTxt = (TextView)findViewById(R.id.titleText);
        releaseDateTxt = (TextView)findViewById(R.id.txtReleaseDate);
        durationTxt = (TextView)findViewById(R.id.txtDuration);
        poster = (ImageView) findViewById(R.id.posterImage);
        overviewTxt = (TextView)findViewById(R.id.txtOverview);
        voteTxt = (TextView)findViewById(R.id.txtVoteAverage);
        reviewBtn = (Button)findViewById(R.id.reviewBtn);


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


    }


}
