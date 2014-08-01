package com.directions.route;

/**
 * Async Task to access the Google Direction API and return the routing data
 * which is then parsed and converting to a route overlay using some classes created by Hesham Saeed.
 * @author Joel Dean
 * Requires an instance of the map activity and the application's current context for the progress dialog.
 *
 */

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;


public class Routing extends AsyncTask<List<LatLng>, Void, Route> {
    protected ArrayList<RoutingListener> _aListeners;
    protected TravelMode _mTravelMode;

    public enum TravelMode {
        BIKING("biking"),
        DRIVING("driving"),
        WALKING("walking"),
        TRANSIT("transit");

        protected String _sValue;

        private TravelMode(String sValue) {
            this._sValue = sValue;
        }

        protected String getValue() {
            return _sValue;
        }
    }


    public Routing(TravelMode mTravelMode) {
        this._aListeners = new ArrayList<RoutingListener>();
        this._mTravelMode = mTravelMode;
    }

    public void registerListener(RoutingListener mListener) {
        _aListeners.add(mListener);
    }

    protected void dispatchOnStart() {
        for (RoutingListener mListener : _aListeners) {
            mListener.onRoutingStart();
        }
    }

    protected void dispatchOnFailure() {
        for (RoutingListener mListener : _aListeners) {
            mListener.onRoutingFailure();
        }
    }

    protected void dispatchOnSuccess(PolylineOptions mOptions) {
        for (RoutingListener mListener : _aListeners) {
            mListener.onRoutingSuccess(mOptions);
        }
    }

    /**
     * Performs the call to the google maps API to acquire routing data and
     * deserializes it to a format the map can display.
     *
     * @param aPoints
     * @return
     */
    @Override
    protected Route doInBackground(List<LatLng>... aPoints) {
        for (LatLng mPoint : aPoints[0]) {
            if (mPoint == null) return null;
        }

        return new GoogleParser(constructURL(aPoints[0])).parse();
    }

    protected String constructURL(List<LatLng> points) {
        LatLng start = points.get(0);
        LatLng dest = points.get(points.size() - 1);
        String sJsonURL = "http://maps.googleapis.com/maps/api/directions/json?";

        final StringBuffer mBuf = new StringBuffer(sJsonURL);
        mBuf.append("origin=");
        mBuf.append(start.latitude);
        mBuf.append(',');
        mBuf.append(start.longitude);
        mBuf.append("&destination=");
        mBuf.append(dest.latitude);
        mBuf.append(',');
        mBuf.append(dest.longitude);
        if (points.size() > 2) {
        	mBuf.append("&waypoints=");
        	for (int i = 1; i < points.size() - 1; i++) {
        		LatLng point = points.get(i);
        		mBuf.append(point.latitude);
        		mBuf.append(",");
        		mBuf.append(point.longitude);
        		if (i < points.size() - 2) {
        			mBuf.append("|");
        		}
        	}
        }
        
        mBuf.append("&sensor=false&mode=");
        mBuf.append(_mTravelMode.getValue());

        return mBuf.toString();
    }

    @Override
    protected void onPreExecute() {
        dispatchOnStart();
    }

    @Override
    protected void onPostExecute(Route result) {
        if (result == null) {
            dispatchOnFailure();
        } else {
            PolylineOptions mOptions = new PolylineOptions();

            for (LatLng point : result.getPoints()) {
                mOptions.add(point);
            }

            dispatchOnSuccess(mOptions);
        }
    }//end onPostExecute method
}
