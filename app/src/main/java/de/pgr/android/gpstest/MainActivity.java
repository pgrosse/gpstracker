package de.pgr.android.gpstest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = (Button) findViewById( R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Geocoder geocoder = new Geocoder( getBaseContext(), Locale.getDefault() );
//
//                LocationProvider provider = lm.getProvider("gps");
//                lm.requestLocationUpdates("gps",
//                    60000, // 1min
//                    1,   // 10m
//                    locationListener);

                GPSService.handler = new TextViewHandler((TextView) findViewById(R.id.standortAnzeige), (TextView) findViewById( R.id.anzahlAnzeige ) );

                startService( new Intent( MainActivity.this, GPSService.class ) );
            }
        });


        Button stopButton = (Button) findViewById( R.id.stopButton );

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {

                List<Location> wegstrecke = new ArrayList<>( GPSService.weg.size() );

                for( Location location : GPSService.weg ) {
                    wegstrecke.add( location );
                }

                //Collections.copy( wegstrecke, GPSService.weg );
                stopService( new Intent( MainActivity.this, GPSService.class ) );

                ((TextView) findViewById( R.id.standortAnzeige )).setText( "..." );

                if( wegstrecke.size() > 0 ) {
                    speichereWeg( wegstrecke );
                }
            }
        });

        Button streckenButton = (Button) findViewById( R.id.streckenButton );

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {

                startActivity( new Intent( MainActivity.this, StreckenAnzeige.class ) );
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        GPSService.handler = new TextViewHandler( (TextView) findViewById( R.id.standortAnzeige ), (TextView) findViewById( R.id.anzahlAnzeige ) );
    }


    @Override
    protected void onDestroy() {
        GPSService.handler = null;
        super.onDestroy();
    }


    private void speichereWeg( List<Location> wegstrecke ) {

        String        timestamp = new SimpleDateFormat( "yyyyMMddhhmmssSSS" ).format( new Date() );
        StringBuilder xml       = new StringBuilder( 65536 );

        xml.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
        xml.append( "<document>\n");
        xml.append( "\t<wegstrecke>\n");

        for( Location wegpunkt : wegstrecke ) {

            xml.append( "\t\t<location>\n" );

            xml.append( "\t\t\t<time>").append( wegpunkt.getTime() ).append( "</time>\n" );
            xml.append( "\t\t\t<latitude>" ).append( wegpunkt.getLatitude() ).append( "</latitude>\n" );
            xml.append( "\t\t\t<longitude>" ).append( wegpunkt.getLongitude() ).append( "</longitude>\n" );
            xml.append( "\t\t\t<altitude>" ).append( wegpunkt.getAltitude() ).append( "</altitude>\n" );
            xml.append( "\t\t\t<bearing>").append( wegpunkt.getBearing() ).append( "</bearing>\n" );

            xml.append( "\t\t</location>\n" );
        }

        xml.append( "\t</wegstrecke>\n" );
        xml.append( "</document>\n" );

        File xmlfile = new File( getFilesDir(), "wegstrecke_" + timestamp + ".xml" );

        try {

            PrintWriter pw = new PrintWriter( new OutputStreamWriter( new FileOutputStream( xmlfile ), "UTF-8" ) );

            pw.print( xml.toString() );
            pw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
