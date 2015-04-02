package yanasmirnova.com.stormy.ui;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;

import yanasmirnova.com.stormy.R;
import yanasmirnova.com.stormy.adapters.HourAdapter;
import yanasmirnova.com.stormy.weather.Hour;

public class HourlyForecastActivity extends ListActivity {

    private Hour[] mHours;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.HOURLY_FORECAST);
        mHours = Arrays.copyOf(parcelables, parcelables.length, Hour[].class);

        HourAdapter adapter = new HourAdapter(mHours);
        mRecyclerView.setAdapter(adapter);
    }
}
