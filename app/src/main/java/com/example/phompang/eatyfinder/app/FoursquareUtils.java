package com.example.phompang.eatyfinder.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.ExecutionException;

import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;

/**
 * Created by phompang on 11/11/2016 AD.
 */

public class FoursquareUtils {
    public static CompactVenue[] venuesSearch(String ll) throws ExecutionException, InterruptedException {
        VenuesSearchResult mVenuesSearchResult = new VenuesSearch().execute(ll).get();
        return mVenuesSearchResult.getVenues();
    }

    private static class VenuesSearch extends AsyncTask<String, Intent, VenuesSearchResult> {
        @Override
        protected VenuesSearchResult doInBackground(String... ll) {
            String clientId = "L2IWJVW3MZJ2TVRUIKZXBWD03H12I5THJZ4BVJCIQLFGSPJW";
            String clientSec = "H5JL0RJV2TZ43YLQ30NKRM2JBDWN2D1TMRY4CD2OQN11LRZX";

            FoursquareApi foursquareApi = new FoursquareApi(clientId, clientSec, "");

            // After client has been initialized we can make queries.
            Result<VenuesSearchResult> result = null;
            try {
                result = foursquareApi.venuesSearch(ll[0], null, null, null, null, null, null, null, null, null, null, null, null);
            } catch (FoursquareApiException e) {
                e.printStackTrace();
            }

            if (result.getMeta().getCode() == 200) {
                // if query was ok we can finally we do something with the data
                for (CompactVenue venue : result.getResult().getVenues()) {
                    // TODO: Do something we the data
                    Log.d("venue", venue.getName());
                }
            } else {
                // TODO: Proper error handling
                Log.e("4sqErr", "Error occured: ");
                Log.e("4sqErr", "  code: " + result.getMeta().getCode());
                Log.e("4sqErr", "  type: " + result.getMeta().getErrorType());
                Log.e("4sqErr", "  detail: " + result.getMeta().getErrorDetail());
            }
            return result.getResult();
        }
    }
}
