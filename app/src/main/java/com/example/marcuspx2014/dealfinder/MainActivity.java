package com.example.marcuspx2014.dealfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Deal;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private TextView hello;
    private double lat;
    private double lng;
    CharArrayWriter writer;
    ArrayList<Double> coordinates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        writer = new CharArrayWriter();

        hello = (TextView) findViewById(R.id.hello);
        /* As detailed in https://guides.codepath.com/android/Using-Android-Async-Http-Client */
        String url = "https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyBMm9LFo02WPePFOvdEmRaKzJeTzgyU7Y0";
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Root JSON in response is an dictionary i.e { "data : [ ... ] }
                // Handle resulting parsed JSON response here
                try {
                    JSONObject location = response.getJSONObject("location");
                    lat = location.getDouble("lat");
                    lng = location.getDouble("lng");

                    ArrayList<ArrayList<String>> dealsData = findDeal();
                } catch (JSONException e) {
                    try {
                        writer.write("Something went wrong with processing the JSONObject");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                System.out.println("Something went wrong with the location request");
            }
        });
    }

    protected ArrayList<ArrayList<String>> findDeal() {
        String consumerKey = "y1AXtI0XEPfMONEEMEk89A";
        String consumerSecret = "IEuXe3BuRu67bsobbxDIxyU9Yng";
        String token = "4iu-vAPiEIyss273n8ZkHIROKZ8LypTU";
        String tokenSecret = "Z_k2JHwqKYczItzNpgkMDZi35Zc";

        YelpAPIFactory apiFactory = new YelpAPIFactory(consumerKey, consumerSecret, token, tokenSecret);
        YelpAPI yelpAPI = apiFactory.createAPI();


        CoordinateOptions coordinate = CoordinateOptions.builder()
                .latitude(lat)
                .longitude(lng).build();

        Map<String, String> params = new HashMap<>();
        // general params
//        params.put("radius_filter", "100");
        params.put("deals_filter", "true");
        final ArrayList<ArrayList<String>> emits = new ArrayList<>();
        Callback<SearchResponse> callback = new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                SearchResponse searchResponse = response.body();

                ArrayList<Business> businesses = searchResponse.businesses();
                for (Business business : businesses) {
                    ArrayList<Deal> deals = business.deals();
                    for (Deal deal : deals) {
                        try {
                            writer.write(deal.title());
                            writer.write('\n');
                            writer.write(business.name());
                            writer.write('\n');
                            writer.write(business.location().coordinate().latitude().toString());
                            writer.write('\n');
                            writer.write(business.location().coordinate().longitude().toString());
                            writer.write('\n');
                            writer.write('\n');
//                            ArrayList<String> singleEmit = new ArrayList<>();
//                            singleEmit.add(deal.title());
//                            singleEmit.add(business.name());
//                            singleEmit.add(business.location().coordinate().latitude().toString());
//                            singleEmit.add(business.location().coordinate().longitude().toString());
//                            emits.add(singleEmit);
                        } catch (IOException e) {
                            System.out.println("Something went wrong");
                        }
                    }
                }
                hello.setText(writer.toString());
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