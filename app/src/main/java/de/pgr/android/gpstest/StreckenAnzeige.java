package de.pgr.android.gpstest;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StreckenAnzeige extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_strecken_anzeige);

        File[] strecken = getFilesDir().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.exists() && pathname.getName().endsWith(".xml");
            }
        });

        List<List<Location>> wegstrecken = new ArrayList<>();

        for ( File strecke : strecken ) {

            try {

                Location       curLocation = new Location( "" );
                List<Location> wegstrecke  = new ArrayList<>();
                BufferedReader br          = new BufferedReader( new InputStreamReader( new FileInputStream( strecke ), "UTF-8" ) );
                String         zeile;

                while( ( zeile = br.readLine() ) != null ) {

                    zeile = zeile.trim();

                    if( zeile.startsWith( "<location>" ) ){
                        curLocation = new Location( "" );
                    } else if( zeile.startsWith( "<latitude>")) {
                        curLocation.setLatitude( getDoubleFromXml( zeile ) );
                    } else if( zeile.startsWith( "<longitude") ) {
                        curLocation.setLongitude( getDoubleFromXml( zeile ) );
                    } else if( zeile.startsWith( "<altitude") ) {
                        curLocation.setAltitude( getDoubleFromXml( zeile ) );
                    } else if( zeile.startsWith( "<time") ) {
                        curLocation.setTime( getLongFromXml( zeile ) );
                    } else if( zeile.startsWith( "<bearing") ) {
                        curLocation.setBearing( (float) getDoubleFromXml( zeile ) );
                    } else if( zeile.startsWith( "</location") ) {
                        wegstrecke.add( curLocation );
                    }
                }

                wegstrecken.add( wegstrecke );

            } catch (FileNotFoundException e) {
                continue;
            } catch (IOException e) {
                continue;
            }
        }

        //todo Anzeige im Listview
    }


    private double getDoubleFromXml(String xml ) {

        int start = xml.indexOf( '>' );
        int end   = xml.indexOf( "</" );

        String valueStr = xml.substring( start, end );

        try {
            return Double.parseDouble(valueStr);
        } catch( NumberFormatException e ) {
            return 0.0;
        }
    }

    private long getLongFromXml(String xml ) {

        int start = xml.indexOf( '>' );
        int end   = xml.indexOf( "</" );

        String valueStr = xml.substring( start, end );

        try {
            return Long.parseLong(valueStr);
        } catch( NumberFormatException e ) {
            return 0;
        }
    }

}
