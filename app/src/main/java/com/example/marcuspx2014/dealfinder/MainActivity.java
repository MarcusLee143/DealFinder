package com.example.marcuspx2014.dealfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Deal;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView hello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hello = (TextView) findViewById(R.id.hello);
//        try {
        ArrayList<ArrayList<String>> dealsData = findDeal();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    protected ArrayList<ArrayList<String>> findDeal() {
        String consumerKey = "y1AXtI0XEPfMONEEMEk89A";
        String consumerSecret = "IEuXe3BuRu67bsobbxDIxyU9Yng";
        String token = "4iu-vAPiEIyss273n8ZkHIROKZ8LypTU";
        String tokenSecret = "Z_k2JHwqKYczItzNpgkMDZi35Zc";

        YelpAPIFactory apiFactory = new YelpAPIFactory(consumerKey, consumerSecret, token, tokenSecret);
        YelpAPI yelpAPI = apiFactory.createAPI();

        CoordinateOptions coordinate = CoordinateOptions.builder()
                .latitude(37.7577)
                .longitude(-122.4376).build();

        Map<String, String> params = new HashMap<>();
        // general params
//        params.put("radius_filter", "100");
        params.put("deals_filter", "true");
        final ArrayList<ArrayList<String>> emits = new ArrayList<>();
        Callback<SearchResponse> callback = new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                SearchResponse searchResponse = response.body();
//                int totalNumberOfResult = searchResponse.total();
//                ArrayList<String> dealTitles = new ArrayList<String>(totalNumberOfResult);
//                ArrayList<String> businessNames = new ArrayList<String>(totalNumberOfResult);
//                ArrayList<Double> latitudes = new ArrayList<Double>(totalNumberOfResult);
//                ArrayList<Double> longitudes = new ArrayList<Double>(totalNumberOfResult);

                ArrayList<Business> businesses = searchResponse.businesses();
//                CharArrayWriter writer = new CharArrayWriter();
                for (Business business : businesses) {
                    ArrayList<Deal> deals = business.deals();
                    for (Deal deal : deals) {
//                        dealTitles.add(deal.title());
//                        businessNames.add(business.name());
//                        latitudes.add(business.location().coordinate().latitude());
//                        longitudes.add(business.location().coordinate().longitude());
//                        try {
//                            writer.write(deal.title());
//                            writer.write('\n');
//                            writer.write(business.name());
//                            writer.write('\n');
//                            writer.write(business.location().coordinate().latitude().toString());
//                            writer.write('\n');
//                            writer.write(business.location().coordinate().longitude().toString());
//                            writer.write('\n');
//                            writer.write('\n');
                        ArrayList<String> singleEmit = new ArrayList<>();
                        singleEmit.add(deal.title());
                        singleEmit.add(business.name());
                        singleEmit.add(business.location().coordinate().latitude().toString());
                        singleEmit.add(business.location().coordinate().longitude().toString());
                        emits.add(singleEmit);
//                        } catch (IOException e) {
//                            System.out.println("Something went wrong");
//                        }
                    }
                }
//                hello.setText(writer.toString());
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                System.out.println("Something went wrong");
            }
        };
        Call<SearchResponse> call = yelpAPI.search(coordinate, params);
        call.enqueue(callback);
        return emits;
    }
}