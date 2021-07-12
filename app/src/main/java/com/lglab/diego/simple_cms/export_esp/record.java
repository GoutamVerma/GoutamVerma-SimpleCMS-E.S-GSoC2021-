package com.lglab.diego.simple_cms.export_esp;

import android.util.Log;

import com.lglab.diego.simple_cms.create.CreateStoryBoardActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
// this class is in charge of storing coordinates point in hashmap

public class record extends CreateStoryBoardActivity {
//    this function puts values in hashmap
    public static void HashMAP(String key, Double longitude, Double latitude, Double altitude, Map<String, List<Double>> position_data) {
        List<Double> values = new ArrayList<Double>();
        values.add(longitude);
        values.add(latitude);
        values.add(altitude);
        position_data.put(key, values);
    }
//     this function find location values in hashmap and return placemark for that location
    public static String FindLocation(Map<String, List<Double>> position_data, String key) {
        List<Double> data = position_data.get(key);
        Double longitude = data.get(0);
        Double latitude = data.get(1);
        Double altitude = data.get(2);
        String kml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" +
                "  <Placemark>\n" +
                "    <name>Simple placemark</name>\n" +
                "    <description>Attached to the ground. Intelligently places itself \n" +
                "       at the height of the underlying terrain.</description>\n" +
                "    <Point>\n" +
                "      <coordinates>" + longitude + "," + latitude + "," + altitude + "</coordinates>\n" +
                "    </Point>\n" +
                "  </Placemark>\n" +
                "</kml>";
        Log.d("kml", kml);
        return kml;
    }
// this function update values in hashmap
    public static void Update(Map<String, List<Double>> position_data,String key,Double longitude,Double latitude,Double altitude){
        List<Double> values = new ArrayList<Double>();
        values.add(longitude);
        values.add(latitude);
        values.add(altitude);
        position_data.replace(key, values);
    }
//    this function delete particular value from hashmap
    public static void Delete(Map<String, List<Double>> position_data,String key){
        position_data.remove(key);
    }
}
