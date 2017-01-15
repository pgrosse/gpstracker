package de.pgr.android.gpstest;

import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

/**
 * Created by PeterG on 29.12.2016.
 */

public class TextViewHandler extends Handler {

    private TextView view;
    private TextView sizeView;


    public TextViewHandler( TextView view, TextView sizeView ) {
        super();
        this.view     = view;
        this.sizeView = sizeView;
    }


    @Override
    public void handleMessage(Message msg) {

        if( GPSService.weg.isEmpty() ) {
            return;
        }

        Location wegpunkt = GPSService.weg.get( GPSService.weg.size() - 1 );

        view.setText( Double.toString( wegpunkt.getLatitude() ) + "," + Double.toString( wegpunkt.getLongitude() ) );
        sizeView.setText( "" + msg.what );
    }

}
