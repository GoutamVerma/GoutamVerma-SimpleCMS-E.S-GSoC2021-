package com.lglab.goutam.simple_cms.export_esp;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.lglab.goutam.simple_cms.create.action.CreateStoryBoardActionLocationActivity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This class is in charge of create esp file
 */
public class export_esp extends AppCompatActivity {
    CreateStoryBoardActionLocationActivity a = new CreateStoryBoardActionLocationActivity();
    HashMap<String, List<String>> people = a.getPeopleMap();
    double initial_value = 0;
    StringBuilder longitudes = new StringBuilder();
    StringBuilder latitudes = new StringBuilder();
    StringBuilder altitudes = new StringBuilder();

    StringBuilder POIlongitudes = new StringBuilder();
    StringBuilder POIlatitudes = new StringBuilder();
    StringBuilder POIaltitudes = new StringBuilder();


    public double time_calculate(){
        Collection getter = people.values();
        Iterator i = getter.iterator();
        int y = 0;
        int total_points = 0;
        int a = getter.size();
        while (y<a) {
            y++;
            List items = (List) i.next();
            if (items.contains("Orbit")) {
                 total_points += 5;
            } else if (items.contains("Spiral")) {
                 total_points += 5;
            } else if (items.contains("Zoom-To")) {
                total_points += 3;
            }
        }
        Log.d("totla points",String.valueOf(total_points));
        double key_diffrence = (double)1/(double) total_points;
        Log.d("diffrence is ",String.valueOf(key_diffrence));
         return key_diffrence;
    }


    @SuppressLint("LongLogTag")
    public String espinone(){

        Collection getter = people.values();
        Iterator i = getter.iterator();
        int y = 0;
        int total_points = 0;
        int a = getter.size();
        double time = 0;
        while (y<a) {
            y++;
            List items = (List) i.next();
            time += Integer.parseInt(String.valueOf(items.get(5)));
            if (items.contains("Orbit")) {
                longitudes.append(orbit_longitude(Double.parseDouble(String.valueOf(items.get(0))),Double.parseDouble(String.valueOf(items.get(1))),Double.parseDouble(String.valueOf(items.get(2)))));

            }
            else if (items.contains("Spiral")) {
                longitudes.append(orbit_longitude(Double.parseDouble(String.valueOf(items.get(0))),Double.parseDouble(String.valueOf(items.get(1))),Double.parseDouble(String.valueOf(items.get(2)))));
            }
             else if (items.contains("Zoom-To")) {
                Zoom_To(Double.parseDouble(String.valueOf(items.get(0))),Double.parseDouble(String.valueOf(items.get(1))),Double.parseDouble(String.valueOf(items.get(2))));
            }
        }
        time = time * 26.162790597;
        StringBuilder esp = new StringBuilder();
        esp.append("{\"type\":\"quickstart\",\"modelVersion\":16,\"settings\":{\"name\":\"ESPFile\",\"frameRate\":60,\"dimensions\":{\"width\":1920,\"height\":1080},\"duration\":"+time+",\"timeFormat\":\"frames\"},\"scenes\":[{\"animationModel\":{\"roving\":false,\"logarithmic\":false,\"groupedPosition\":true},\"duration\":"+time+",\"attributes\":[{\"type\":\"cameraGroup\",\"attributes\":[{\"type\":\"cameraPositionGroup\",\"attributes\":[{\"type\":\"position\",\"value\":{\"maxValueRange\":71488366.22893658,\"minValueRange\":0,\"relative\":0},\"visible\":true,\"attributesLocked\":true,\"attributes\":[\n" +
                "{\"type\":\"longitude\",\"value\":{\"maxValueRange\":180,\"minValueRange\":-180,\"relative\":0.7145264194516641},\"keyframes\":[");
                esp.append(longitudes);
        Log.d("longitude file is",String.valueOf(longitudes));
        esp.append("],\"visible\":true},\n" +
                "\n" +
                "{\"type\":\"latitude\",\"value\":{\"maxValueRange\":89.9999,\"minValueRange\":-89.9999,\"relative\":0.6589919712779995},\"keyframes\":[");
        esp.append(latitudes);
        Log.d("latitude is ", String.valueOf(latitudes));
        esp.append("],\"visible\":true},\n" +
                "\n" +
                "{\"type\":\"altitude\",\"value\":{\"maxValueRange\":65117481,\"minValueRange\":1,\"relative\":0.000008461629657658742},\"keyframes\":[");
        esp.append(altitudes);
        Log.d("altitudes is ", String.valueOf(altitudes));

        esp.append("\n" +
                "],\"visible\":true}]}]},{\"type\":\"cameraTargetEffect\",\"attributes\":[\n" +
                "\n" +
                "{\"type\":\"poi\",\"value\":{\"maxValueRange\":71488366.22893658,\"minValueRange\":-6371022.11950216,\"relative\":0},\"visible\":true,\"attributesLocked\":true,\"attributes\":[\n" +
                "\n" +
                "{\"type\":\"longitudePOI\",\"value\":{\"maxValueRange\":180,\"minValueRange\":-180},\"keyframes\":[");
        esp.append(POIlongitudes);
        esp.append("],\"visible\":true},\n" +
                "{\"type\":\"latitudePOI\",\"value\":{\"maxValueRange\":89.9999,\"minValueRange\":-89.9999},\"keyframes\":[");
        esp.append(POIlatitudes);
        esp.append("],\"visible\":true},\n" +
                "{\"type\":\"altitudePOI\",\"value\":{\"maxValueRange\":65117481,\"minValueRange\":1},\"keyframes\":[");
        esp.append(POIaltitudes);
        esp.append("],\"visible\":true}]\n" +
                "\n" +
                "},{\"type\":\"influence\",\"value\":{\"maxValueRange\":1,\"minValueRange\":0},\"visible\":true}],\"visible\":true}]},{\"type\":\"environmentGroup\",\"attributes\":[{\"type\":\"planet\",\"value\":{\"world\":\"earth\"},\"visible\":true},{\"type\":\"clouddate\",\"value\":{\"maxValueRange\":1629891000000,\"minValueRange\":1629759240000,\"relative\":0.030054644808743168},\"visible\":true}]}]}],\"has_started\":true,\"has_finished\":true,\"playbackManager\":{\"range\":{\"start\":0,\"end\":"+time+"}}}");
        Log.d("complete esp file of orbit is", String.valueOf(esp));
        return String.valueOf(esp);
    }


    public String orbit_longitude(double longitudee,double latitudess,double altitude){
        double keyframe1;
        if(initial_value == 0){
             keyframe1 = initial_value;
             Log.d("keyframe valye is ",String.valueOf(keyframe1));
        }else {
            keyframe1 = initial_value + time_calculate();
            longitudes.append(",");
            altitudes.append(",");
            latitudes.append(",");
            POIlatitudes.append(",");
            POIaltitudes.append(",");
            POIlongitudes.append(",");
        }
        double keyframe2 = keyframe1 + time_calculate();
        double keyframe3 = keyframe2 + time_calculate();
        double keyframe4 = keyframe3 + time_calculate();
        double keyframe5 = keyframe4 + time_calculate();
        initial_value = keyframe5;
        double longitude =cal_longitude(longitudee);
        double longitude1 = longitude - 0.0000521498534771 ;
        double longitude2 = longitude + 0.0000521498534771;
        orbit_latitude(keyframe1, keyframe2, keyframe3, keyframe4, keyframe5,latitudess,altitude);
        String longitude_esp = "{\"time\":"+keyframe1+",\"value\":"+longitude+",\"transitionIn\":{\"x\":0,\"y\":0,\"type\":\"linear\"},\"transitionOut\":{\"x\":0,\"y\":0,\"type\":\"linear\"}},{\"time\":"+keyframe2+",\"value\":"+longitude1+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},{\"time\":"+keyframe3+",\"value\":"+longitude+",\"transitionIn\":{\"x\":0,\"y\":0,\"type\":\"linear\"},\"transitionOut\":{\"x\":0,\"y\":0,\"type\":\"linear\"}},{\"time\":"+keyframe4+",\"value\":"+longitude2+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},{\"time\":"+keyframe5+",\"value\":"+longitude+",\"transitionIn\":{\"x\":0,\"y\":0,\"type\":\"linear\"},\"transitionOut\":{\"x\":0,\"y\":0,\"type\":\"linear\"}}";
        String longitude_poi_orbit = "{\"time\":"+keyframe2+",\"value\":"+longitude+"},{\"time\":"+keyframe3+",\"value\":"+longitude+"},{\"time\":"+keyframe4+",\"value\":"+longitude+"},{\"time\":"+keyframe5+",\"value\":"+longitude+"}";
        POIlongitudes.append(longitude_poi_orbit);
        return longitude_esp;
    }
    public void orbit_latitude(double keyframe1, double keyframe2, double keyframe3, double keyframe4, double keyframe5,double latitudee,double altitude ){
        String latitude_esp = "";
        double latitude =cal_latitude(latitudee);
        double latitude1= latitude - 0.0000686298492109;
        double latitude2 = latitude1 - 0.0000686129161325;
        orbit_altitude(keyframe1, keyframe2, keyframe3, keyframe4, keyframe5, altitude);
        latitude_esp = "{\"time\":"+keyframe1+",\"value\":"+latitude+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},{\"time\":"+keyframe2+",\"value\":"+latitude1+",\"transitionIn\":{\"x\":0,\"y\":0,\"type\":\"linear\"},\"transitionOut\":{\"x\":0,\"y\":0,\"type\":\"linear\"}},{\"time\":"+keyframe3+",\"value\":"+latitude2+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},{\"time\":"+keyframe4+",\"value\":"+latitude1+",\"transitionIn\":{\"x\":0,\"y\":0,\"type\":\"linear\"},\"transitionOut\":{\"x\":0,\"y\":0,\"type\":\"linear\"}},{\"time\":"+keyframe5+",\"value\":"+latitude+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}}";
        String latitude_poi_orbit = "{\"time\":"+keyframe2+",\"value\":"+latitude1+"},{\"time\":"+keyframe3+",\"value\":"+latitude1+"},{\"time\":"+keyframe4+",\"value\":"+latitude1+"},{\"time\":"+keyframe5+",\"value\":"+latitude1+"}";
        POIlatitudes.append(latitude_poi_orbit);
        latitudes.append(latitude_esp);
    }

    public void orbit_altitude(double keyframe1, double keyframe2, double keyframe3, double keyframe4, double keyframe5,double altitude){
        String altitude_esp = "";
        double Altitude = cal_altitude(altitude);
        altitude_esp = "{\"time\":"+keyframe1+",\"value\":0.5},{\"time\":"+keyframe2+",\"value\":"+Altitude+"},{\"time\":"+keyframe3+",\"value\":"+Altitude+"},{\"time\":"+keyframe4+",\"value\":"+Altitude+"},{\"time\":"+keyframe5+",\"value\":"+Altitude+"}";
        altitudes.append(altitude_esp);
        String altitude_poi_orbit = "{\"time\":"+keyframe2+",\"value\":9.677648139072125e-7},{\"time\":"+keyframe3+",\"value\":9.677648139072125e-7},{\"time\":"+keyframe4+",\"value\":9.677648139072125e-7},{\"time\":"+keyframe5+",\"value\":9.677648139072125e-7}";
        POIaltitudes.append(altitude_poi_orbit);
    }
    public void Spiral(double Longitude, double Latitude,double Altitude){
        double keyframe1;
        if(initial_value == 0){
            keyframe1 = initial_value;
            Log.d("keyframe valye is ",String.valueOf(keyframe1));
        }else {
            keyframe1 = initial_value + time_calculate();
            longitudes.append(",");
            altitudes.append(",");
            latitudes.append(",");
            POIlatitudes.append(",");
            POIaltitudes.append(",");
            POIlongitudes.append(",");
        }
        double keyframe2 = keyframe1 + time_calculate();
        double keyframe3 = keyframe2 + time_calculate();
        double keyframe4 = keyframe3 + time_calculate();
        double keyframe5 = keyframe4 + time_calculate();
        double keyframe6 = keyframe5 + time_calculate();
        initial_value = keyframe6;
        double latitude =cal_latitude(Latitude);
        double longitude =cal_longitude(Longitude);
        double altitude=  cal_altitude(Altitude);

        double longitude1 = longitude ;
        double longitude2 = longitude +  0.0000298509100691;
        double longitude3 = longitude ;
        double longitude4 = longitude - 0.0000141278051995;
        double longitude5 = longitude;

        double latitude1 =latitude + 0.0000963693745013;
        double latitude2 = latitude + 0.0000000019924541;
        double latitude3 = latitude - 0.0000338802481173 ;
        double latitude4 = latitude - 0.0000000004462956;
        double latitude5 = latitude + 0.0000249532300624;

        double altitude1 ;
        double altitude2 ;
        double altitude3 ;
        double altitude4 ;
        double altitude5 ;

        String esp_longitude = "\n" +
                "{\"time\":"+keyframe1+",\"value\":"+longitude1+",\"transitionIn\":{\"x\":0,\"y\":0,\"type\":\"linear\"},\"transitionOut\":{\"x\":0,\"y\":0,\"type\":\"linear\"}},\n" +
                "{\"time\":"+keyframe2+",\"value\":"+longitude+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},\n" +
                "{\"time\":"+keyframe3+",\"value\":"+longitude3+",\"transitionIn\":{\"x\":-0.04074666666666667,\"y\":0.000008133209991771178,\"influence\":0.16000000318735022,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.026880000000000008,\"y\":-0.000005365363659493029,\"influence\":0.1600000031873502,\"type\":\"auto\"},\"transitionLinked\":false},\n" +
                "{\"time\":"+keyframe4+",\"value\":"+longitude4+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},\n" +
                "{\"time\":"+keyframe5+",\"value\":"+longitude5+",\"transitionIn\":{\"x\":0,\"y\":0,\"type\":\"linear\"},\"transitionOut\":{\"x\":0,\"y\":0,\"type\":\"linear\"}}";

      longitudes.append(esp_longitude);

       String esp_latitude = "{\"time\":"+keyframe1+",\"value\":"+latitude1+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},\n" +
               "{\"time\":"+keyframe2+",\"value\":"+latitude2+",\"transitionIn\":{\"x\":-0.06976,\"y\":0.00002453729961953144,\"influence\":0.16000000989761512,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.04074666666666667,\"y\":-0.000014332184181438856,\"influence\":0.16000000989761515,\"type\":\"auto\"},\"transitionLinked\":false},\n" +
               "{\"time\":"+keyframe3+",\"value\":"+latitude3+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},\n" +
               "{\"time\":"+keyframe4+",\"value\":"+latitude4+",\"transitionIn\":{\"x\":-0.026880000000000008,\"y\":-0.000010065692086769217,\"influence\":0.160000011218085,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.02261333333333333,\"y\":0.000008467963184107437,\"influence\":0.16000001121808496,\"type\":\"auto\"},\"transitionLinked\":false},\n" +
               "{\"time\":"+keyframe5+",\"value\":"+latitude5+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}}\n";

        latitudes.append(esp_latitude);

       String esp_altitude = "\n" +
               "{\"time\":"+keyframe1+",\"value\":0.000024002771606026525,\"transitionOut\":{\"x\":0.07061333333333333,\"y\":-9.669468975712343e-7,\"influence\":0.16000000001500106,\"type\":\"custom\"},\"transitionLinked\":false},\n" +
               "{\"time\":"+keyframe2+",\"value\":0.00001795935349620487,\"transitionIn\":{\"x\":-0.06976,\"y\":0.0000010755386515253388,\"influence\":0.16000000001901643,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.04074666666666667,\"y\":-6.28219823979632e-7,\"influence\":0.16000000001901646,\"type\":\"auto\"},\"transitionLinked\":false},\n" +
               "{\"time\":"+keyframe3+",\"value\":0.00001364262627490369,\"transitionIn\":{\"x\":-0.04074666666666667,\"y\":6.824467681893548e-7,\"influence\":0.16000000002244108,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.026880000000000008,\"y\":-4.5020048582124985e-7,\"influence\":0.16000000002244105,\"type\":\"auto\"},\"transitionLinked\":false},\n" +
               "{\"time\":"+keyframe4+",\"value\":0.000011052589942122983,\"transitionIn\":{\"x\":-0.026880000000000008,\"y\":2.8562530989567517e-7,\"influence\":0.16000000000903286,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.02261333333333333,\"y\":-2.4028795911858387e-7,\"influence\":0.16000000000903286,\"type\":\"auto\"},\"transitionLinked\":false},\n" +
               "{\"time\":"+keyframe5+",\"value\":0.000010189244497862747,\"transitionIn\":{\"x\":-0.022186666666666674,\"y\":1.381352710816813e-7,\"influence\":0.16000000000310108,\"type\":\"custom\"},\"transitionLinked\":false}\n" +
               "\n";
       altitudes.append(esp_altitude);

        String esp_POIlongitude = "{\"time\":"+keyframe5+",\"value\":"+longitude+"}";
        POIlongitudes.append(esp_POIlongitude);
        String esp_POIlatitude = "{\"time\":"+keyframe5+",\"value\":"+latitude+"}";
        POIlatitudes.append(esp_POIlatitude);
        String esp_POIaltitude = "{\"time\":"+keyframe5+",\"value\":"+altitude+"}";
        POIaltitudes.append(esp_POIaltitude);
        }

    public void Zoom_To(double Longitude, double Latitudes,double Altitude){
        double altitude1 = 0.45884035117674;
        double latitude =cal_latitude(Latitudes);
        double longitude =cal_longitude(Longitude);
        double end = 0.95411228992732346 / 32193;
        double final_altitude = cal_altitude(Altitude);
        double keyframe1;
        if(initial_value == 0){
            keyframe1 = initial_value;
        }else {
            keyframe1 = initial_value + time_calculate();
            longitudes.append(",");
            altitudes.append(",");
            latitudes.append(",");
            POIlatitudes.append(",");
            POIaltitudes.append(",");
            POIlongitudes.append(",");
        }
        double keyframe2 = keyframe1 + time_calculate();
        double keyframe3 = keyframe2 + time_calculate();
        double keyframe4 = keyframe1 + time_calculate() + 0.0000142857142857;
        initial_value = keyframe3;
                longitudes.append("{\"time\":"+keyframe1+",\"value\":"+longitude+",\"transitionIn\":{\"x\":-0.2,\"y\":0,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"}},{\"time\":"+keyframe2+",\"value\":"+longitude+",\"transitionIn\":{\"x\":-0.32000000000000006,\"y\":0,\"influence\":0.4000000000000001,\"type\":\"custom\"},\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"},\"transitionLinked\":false},{\"time\":"+keyframe3+",\"value\":"+longitude+",\"transitionIn\":{\"x\":-0.32000000000000006,\"y\":0,\"influence\":0.4000000000000001,\"type\":\"custom\"},\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"},\"transitionLinked\":false}");
                latitudes.append("{\"time\":"+keyframe1+",\"value\":"+ latitude +",\"transitionIn\":{\"x\":-0.2,\"y\":0,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"}},{\"time\":"+keyframe2+",\"value\":"+ latitude +",\"transitionIn\":{\"x\":-0.32000000000000006,\"y\":0,\"influence\":0.4000000000000001,\"type\":\"custom\"},\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"},\"transitionLinked\":false},{\"time\":"+keyframe3+",\"value\":"+ latitude +",\"transitionIn\":{\"x\":-0.32000000000000006,\"y\":0,\"influence\":0.4000000000000001,\"type\":\"custom\"},\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"},\"transitionLinked\":false}");
                altitudes.append("{\"time\":"+keyframe1+",\"value\":0.5,\"transitionIn\":{\"x\":-0.2,\"y\":0,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"}},{\"time\":"+keyframe2+",\"value\":"+final_altitude+",\"transitionIn\":{\"x\":-0.32000000000000006,\"y\":0,\"influence\":0.4000000000000001,\"type\":\"custom\"},\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"},\"transitionLinked\":false},{\"time\":"+keyframe3+",\"value\":"+final_altitude+",\"transitionIn\":{\"x\":-0.32000000000000006,\"y\":0,\"influence\":0.4000000000000001,\"type\":\"custom\"},\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"},\"transitionLinked\":false}");
                POIlongitudes.append("{\"time\":"+keyframe1+",\"value\":"+longitude+"},{\"time\":"+keyframe3+",\"value\":"+longitude+"},");
                POIlatitudes.append("{\"time\":"+keyframe1+",\"value\":"+latitude+"},{\"time\":"+keyframe3+",\"value\":"+ latitude +"},");
                POIaltitudes.append("{\"time\":"+keyframe1+",\"value\":"+final_altitude+"},{\"time\":"+keyframe3+",\"value\":"+final_altitude+"},");

                POIlongitudes.append("{\"time\":"+keyframe1+",\"value\":"+longitude+"},{\"time\":"+keyframe3+",\"value\":"+longitude+"}");
                POIlatitudes.append("{\"time\":"+keyframe1+",\"value\":"+latitude+"},{\"time\":"+keyframe3+",\"value\":"+ latitude +"}");
                POIaltitudes.append("{\"time\":"+keyframe1+",\"value\":"+final_altitude+"},{\"time\":"+keyframe3+",\"value\":"+final_altitude+"}");



    }
    /**
     * @param longitude this function calculate the esp values of longitude
     * @return
     */
    public static double cal_longitude(double longitude){
        double final_longitude;
        if(longitude>0){
            final_longitude=((0.5/180)*longitude)+0.5;
        }
        else if(longitude<0){
            final_longitude=0.5 - ((0.5/180)*(longitude* -1)) ;
        }
        else
            final_longitude=0.5;

        return final_longitude;
    }

    /**
     * @param latitude this function calculate the esp values of latitude
     * @return
     */
    public static double cal_latitude(double latitude){
        double final_latitude;
        if(latitude>0){
            final_latitude = ((0.5/90)*latitude)+0.5;
        }
        else if(latitude<0){
            final_latitude = 0.5 - (((0.5/90)*(latitude * -1))) ;
        }
        else
            final_latitude = 0.5;

        return final_latitude;
    }

    /**
     * @param altitude this function calculate the altitude value for esp files
     * @return
     */
    public static double cal_altitude(double altitude){
        double devi =1.535697283351506e-8*altitude;
        Log.d("devi value",String.valueOf(devi));
        return devi;
    }

    /**
     * @param second this function calculate the duration for animation
     * @return
     */
    public static int cal_duration(int second){
        int duration_frame;
        duration_frame = second * 30;
        return duration_frame;
    }

    /**
     * @param Longitude
     * @param Latitude
     * @param Altitude
     * @param Duration
     * @param Name
     * @return this function return the esp animation for zoom-to feature
     */
    public static String ZoomTo(Double Longitude,Double Latitude,Double Altitude,int Duration,String Name){
        double altitude1 = 0.45884035117674;
        double latitude =cal_latitude(Latitude);
        double longitude =cal_longitude(Longitude);
        int duration = cal_duration(Duration);
        double end = 0.95411228992732346 / 32193;
        double final_altitude = 0;
        if(Altitude<= 10000 && Altitude>4500){
            final_altitude = 0.558636450372466;
        }
        else if(Altitude<=4500 && Altitude>2500) {
            final_altitude = 0.525636450372466;
        }
        else if(Altitude<=2500 && Altitude>1400){
            final_altitude =0.508636450372466;
        }
        else if(Altitude<=1400 && Altitude>600 ){
            final_altitude = 0.488636450372466;
        }
        else if(Altitude<=600){
            final_altitude = 0.46527569781874;
        }
        String esp ="{\"type\":\"quickstart\",\"modelVersion\":16,\"settings\":{\"name\":\""+Name+"\",\"frameRate\":30,\"dimensions\":{\"width\":1920,\"height\":1080},\"duration\":"+duration+",\"timeFormat\":\"frames\"},\"scenes\":[{\"animationModel\":{\"roving\":false,\"logarithmic\":true,\"groupedPosition\":true},\"duration\":"+duration+",\"attributes\":[{\"type\":\"cameraGroup\",\"attributes\":[{\"type\":\"cameraPositionGroup\",\"attributes\":[{\"type\":\"position\",\"value\":{\"maxValueRange\":71488366.22893658,\"minValueRange\":0,\"relative\":0},\"visible\":true,\"attributesLocked\":true,\"attributes\":[{\"type\":\"longitude\",\"value\":{\"maxValueRange\":180,\"minValueRange\":-180,\"relative\":"+longitude+"},\"keyframes\":[{\"time\":0,\"value\":"+longitude+",\"transitionIn\":{\"x\":-0.2,\"y\":0,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"}},{\"time\":0.8,\"value\":"+longitude+",\"transitionIn\":{\"x\":-0.32000000000000006,\"y\":0,\"influence\":0.4000000000000001,\"type\":\"custom\"},\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"},\"transitionLinked\":false}],\"visible\":true},{\"type\":\"latitude\",\"value\":{\"maxValueRange\":89.9999,\"minValueRange\":-89.9999,\"relative\":"+latitude+"},\"keyframes\":[{\"time\":0,\"value\":"+latitude+",\"transitionIn\":{\"x\":-0.2,\"y\":0,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"}},{\"time\":0.8,\"value\":"+latitude+",\"transitionIn\":{\"x\":-0.32000000000000006,\"y\":0,\"influence\":0.4000000000000001,\"type\":\"custom\"},\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"},\"transitionLinked\":false}],\"visible\":true},{\"type\":\"altitude\",\"value\":{\"maxValueRange\":65117481,\"minValueRange\":1,\"relative\":0.982542908896292},\"keyframes\":[{\"time\":0,\"value\":0.982542908896292,\"transitionIn\":{\"x\":-0.2,\"y\":0,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"}},{\"time\":0.8,\"value\":"+final_altitude+",\"transitionIn\":{\"x\":-0.32000000000000006,\"y\":0,\"influence\":0.4000000000000001,\"type\":\"custom\"},\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"},\"transitionLinked\":false}],\"visible\":true}]}]},{\"type\":\"cameraRotationGroup\",\"attributes\":[{\"type\":\"rotationX\",\"value\":{\"maxValueRange\":360,\"minValueRange\":0},\"keyframes\":[{\"time\":0,\"value\":0,\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"}},{\"time\":0.8,\"value\":0,\"transitionIn\":{\"x\":-0.2,\"y\":0,\"type\":\"auto\"}}],\"visible\":true},{\"type\":\"rotationY\",\"value\":{\"maxValueRange\":180,\"minValueRange\":0},\"keyframes\":[{\"time\":0,\"value\":0,\"transitionOut\":{\"x\":0.68,\"y\":0,\"influence\":0.85,\"type\":\"custom\"},\"transitionLinked\":false},{\"time\":0.8,\"value\":0,\"transitionIn\":{\"x\":-0.24,\"y\":0,\"influence\":0.3,\"type\":\"custom\"},\"transitionLinked\":false}],\"visible\":true}]},{\"type\":\"cameraTargetEffect\",\"attributes\":[{\"type\":\"poi\",\"value\":{\"maxValueRange\":71488366.22893658,\"minValueRange\":-6371022.11950216,\"relative\":0},\"visible\":true,\"attributesLocked\":true,\"attributes\":[{\"type\":\"longitudePOI\",\"value\":{\"maxValueRange\":180,\"minValueRange\":-180,\"relative\":0.18016713992972266},\"keyframes\":[{\"time\":0,\"value\":"+longitude+"}],\"visible\":true},{\"type\":\"latitudePOI\",\"value\":{\"maxValueRange\":89.9999,\"minValueRange\":-89.9999,\"relative\":"+latitude+"},\"keyframes\":[{\"time\":0,\"value\":"+latitude+"}],\"visible\":true},{\"type\":\"altitudePOI\",\"value\":{\"maxValueRange\":65117481,\"minValueRange\":1,\"relative\":"+altitude1+"},\"keyframes\":[{\"time\":0,\"value\":"+altitude1+"}],\"visible\":true}]},{\"type\":\"influence\",\"value\":{\"maxValueRange\":1,\"minValueRange\":0,\"relative\":0},\"keyframes\":[{\"time\":0,\"value\":0,\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"}},{\"time\":0.5333328,\"value\":0,\"transitionIn\":{\"x\":-0.2,\"y\":0,\"type\":\"auto\"}}],\"visible\":true}],\"visible\":true}]},{\"type\":\"environmentGroup\",\"attributes\":[{\"type\":\"planet\",\"value\":{\"world\":\"earth\"},\"visible\":true},{\"type\":\"clouddate\",\"value\":{\"maxValueRange\":1624026600000,\"minValueRange\":1623947400000,\"relative\":0.9318181818181818},\"visible\":true}]}]}],\"has_started\":true,\"has_finished\":true,\"playbackManager\":{\"range\":{\"start\":0,\"end\":150}}}";
        return esp;
    }

    /**
     * @param latitudee this function have the latitude value of location
     * @param longitudee this function have the longitude value of location
     * @param altiude this function have the altitude value of location
     * @param duratioon
     * @param name
     * @return this function return the animation of orbit
     */
    public static String orbit(Double latitudee, Double longitudee,Double altiude ,int duratioon, String name){
        double latitude =cal_latitude(latitudee);
        double latitude1= latitude - 0.0000686298492109;
        double latitude2 = latitude1 - 0.0000686129161325;
        double longitude =cal_longitude(longitudee);
        double longitude1 = longitude - 0.0000521498534771 ;
        double longitude2 = longitude + 0.0000521498534771;
        double Altitude = cal_altitude(altiude);
        int duration = cal_duration(duratioon);
        String esp = "{\"type\":\"quickstart\",\"modelVersion\":16,\"settings\":{\"name\":\""+name+"\",\"frameRate\":30,\"dimensions\":{\"width\":1920,\"height\":1080},\"duration\":"+duration+",\"timeFormat\":\"frames\"},\"scenes\":[{\"animationModel\":{\"roving\":false,\"logarithmic\":false,\"groupedPosition\":true},\"duration\":"+duration+",\"attributes\":[{\"type\":\"cameraGroup\",\"attributes\":[{\"type\":\"cameraPositionGroup\",\"attributes\":[{\"type\":\"position\",\"value\":{\"maxValueRange\":71488366.22893658,\"minValueRange\":0,\"relative\":0},\"visible\":true,\"attributesLocked\":true,\"attributes\":[{\"type\":\"longitude\",\"value\":{\"maxValueRange\":180,\"minValueRange\":-180,\"relative\":"+longitude+"},\"keyframes\":[{\"time\":0,\"value\":"+longitude+",\"transitionIn\":{\"x\":0,\"y\":0,\"type\":\"linear\"},\"transitionOut\":{\"x\":0,\"y\":0,\"type\":\"linear\"}},{\"time\":0.25,\"value\":"+longitude1+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},{\"time\":0.5,\"value\":"+longitude+",\"transitionIn\":{\"x\":0,\"y\":0,\"type\":\"linear\"},\"transitionOut\":{\"x\":0,\"y\":0,\"type\":\"linear\"}},{\"time\":0.75,\"value\":"+longitude2+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},{\"time\":1,\"value\":"+longitude+",\"transitionIn\":{\"x\":0,\"y\":0,\"type\":\"linear\"},\"transitionOut\":{\"x\":0,\"y\":0,\"type\":\"linear\"}}],\"visible\":true},{\"type\":\"latitude\",\"value\":{\"maxValueRange\":89.9999,\"minValueRange\":-89.9999,\"relative\":"+latitude+"},\"keyframes\":[{\"time\":0,\"value\":"+latitude+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},{\"time\":0.25,\"value\":"+latitude1+",\"transitionIn\":{\"x\":0,\"y\":0,\"type\":\"linear\"},\"transitionOut\":{\"x\":0,\"y\":0,\"type\":\"linear\"}},{\"time\":0.5,\"value\":"+latitude2+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},{\"time\":0.75,\"value\":"+latitude1+",\"transitionIn\":{\"x\":0,\"y\":0,\"type\":\"linear\"},\"transitionOut\":{\"x\":0,\"y\":0,\"type\":\"linear\"}},{\"time\":1,\"value\":"+latitude+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}}],\"visible\":true},{\"type\":\"altitude\",\"value\":{\"maxValueRange\":65117481,\"minValueRange\":1,\"relative\":"+Altitude+"},\"keyframes\":[{\"time\":0,\"value\":"+Altitude+"},{\"time\":0.25,\"value\":"+Altitude+"},{\"time\":0.5,\"value\":"+Altitude+"},{\"time\":0.75,\"value\":"+Altitude+"},{\"time\":1,\"value\":"+Altitude+"}],\"visible\":true}]}]},{\"type\":\"cameraRotationGroup\",\"attributes\":[{\"type\":\"rotationX\",\"value\":{\"maxValueRange\":360,\"minValueRange\":0},\"visible\":true},{\"type\":\"rotationY\",\"value\":{\"maxValueRange\":180,\"minValueRange\":0},\"visible\":true}]},{\"type\":\"cameraTargetEffect\",\"attributes\":[{\"type\":\"poi\",\"value\":{\"maxValueRange\":71488366.22893658,\"minValueRange\":-6371022.11950216,\"relative\":0},\"visible\":true,\"attributesLocked\":true,\"attributes\":[{\"type\":\"longitudePOI\",\"value\":{\"maxValueRange\":180,\"minValueRange\":-180,\"relative\":"+longitude+"},\"keyframes\":[{\"time\":0,\"value\":"+longitude+"}],\"visible\":true},{\"type\":\"latitudePOI\",\"value\":{\"maxValueRange\":89.9999,\"minValueRange\":-89.9999,\"relative\":"+latitude1+"},\"keyframes\":[{\"time\":0,\"value\":"+latitude1+"}],\"visible\":true},{\"type\":\"altitudePOI\",\"value\":{\"maxValueRange\":65117481,\"minValueRange\":1,\"relative\":9.677648139072125e-7},\"keyframes\":[{\"time\":0,\"value\":9.677648139072125e-7}],\"visible\":true}]},{\"type\":\"influence\",\"value\":{\"maxValueRange\":1,\"minValueRange\":0},\"visible\":true}],\"visible\":true}]},{\"type\":\"environmentGroup\",\"attributes\":[{\"type\":\"planet\",\"value\":{\"world\":\"earth\"},\"visible\":true},{\"type\":\"clouddate\",\"value\":{\"maxValueRange\":1623810600000,\"minValueRange\":1623731400000,\"relative\":0.9318181818181818},\"visible\":true}]}]}],\"has_started\":true,\"has_finished\":true,\"playbackManager\":{\"range\":{\"start\":0,\"end\":600}}}";
        return esp;
    }


    /**
     * @param Longitude
     * @param Latitude
     * @param Altitude
     * @param Duration
     * @param Name this function return the animation of spiral
     * @return
     */
    public static String spiral(Double Longitude, Double Latitude,Double Altitude ,int Duration, String Name){
        String esp = "";
        double latitude =cal_latitude(Latitude);
        double latitude1  = latitude - 0.0000864903794252;
        double latitude2 = latitude1 - 0.0000395858154654;
        double latitude3 = latitude2 + 0.0000395873822476;
        double latitude4 = latitude3 + 0.0000328892744689;
        double longitude =cal_longitude(Longitude);
        double longitude1 = longitude + 0.0000311936257905;
        double longitude2 = longitude - 0.0000189552971268;
        double altitude5=  Altitude * 1.5355128202E-8;
        double altitude4 = altitude5 +  0.0000008637511816;
        double altitude3 = altitude4 +  0.0000025912535448;
        double altitude2 = altitude3 +  0.000004318755908;
        double altitude1 = altitude2 +  0.0000060462582712;
        double POIaltitude = altitude5 - 0.000001677754869;
        int duration = cal_duration(Duration);
        esp = "{\"type\":\"quickstart\",\"modelVersion\":16,\"settings\":{\"name\":\"" + Name + "\",\"frameRate\":30,\"dimensions\":{\"width\":1920,\"height\":1080},\"duration\":" + duration + ",\"timeFormat\":\"frames\"},\"scenes\":[{\"animationModel\":{\"roving\":false,\"logarithmic\":false,\"groupedPosition\":true},\"duration\":" + duration + ",\"attributes\":[{\"type\":\"cameraGroup\",\"attributes\":[{\"type\":\"cameraPositionGroup\",\"attributes\":[{\"type\":\"position\",\"value\":{\"maxValueRange\":71488366.22893658,\"minValueRange\":0,\"relative\":0},\"visible\":true,\"attributesLocked\":true,\"attributes\":[{\"type\":\"longitude\",\"value\":{\"maxValueRange\":180,\"minValueRange\":-180,\"relative\":" + longitude + "},\"keyframes\":[{\"time\":0,\"value\":" + longitude + ",\"transitionIn\":{\"x\":0,\"y\":0,\"type\":\"linear\"},\"transitionOut\":{\"x\":0,\"y\":0,\"type\":\"linear\"}},{\"time\":0.38,\"value\":" + longitude1 + ",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},{\"time\":0.6333333333333333,\"value\":" + longitude + ",\"transitionIn\":{\"x\":-0.04053333333333333,\"y\":0.000008918421723791425,\"influence\":0.16000000387294522,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.03072000000000001,\"y\":-0.000006759224885399817,\"influence\":0.1600000038729452,\"type\":\"auto\"},\"transitionLinked\":false},{\"time\":0.8253333333333334,\"value\":" + longitude2 + ",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},{\"time\":1,\"value\":" + longitude + ",\"transitionIn\":{\"x\":0,\"y\":0,\"type\":\"linear\"},\"transitionOut\":{\"x\":0,\"y\":0,\"type\":\"linear\"}}],\"visible\":true},{\"type\":\"latitude\",\"value\":{\"maxValueRange\":89.9999,\"minValueRange\":-89.9999,\"relative\":" + latitude + "},\"keyframes\":[{\"time\":0,\"value\":" + latitude + ",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},{\"time\":0.38,\"value\":" + latitude1 + ",\"transitionIn\":{\"x\":-0.0608,\"y\":0.000023178334612516952,\"influence\":0.16000001162645378,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.04053333333333333,\"y\":-0.0000154522230750113,\"influence\":0.16000001162645378,\"type\":\"auto\"},\"transitionLinked\":false},{\"time\":0.6333333333333333,\"value\":" + latitude2 + ",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},{\"time\":0.8253333333333334,\"value\":" + latitude3 + ",\"transitionIn\":{\"x\":-0.03072000000000001,\"y\":-0.000012008072351932242,\"influence\":0.16000001222345955,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.02794666666666666,\"y\":0.000010924010264605024,\"influence\":0.16000001222345953,\"type\":\"auto\"},\"transitionLinked\":false},{\"time\":1,\"value\":" + latitude4 + ",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}}],\"visible\":true},{\"type\":\"altitude\",\"value\":{\"maxValueRange\":65117481,\"minValueRange\":1,\"relative\":"+altitude1+"},\"keyframes\":[{\"time\":0,\"value\":"+altitude1+",\"transitionOut\":{\"x\":0.05141333333333334,\"y\":-9.665974830871797e-7,\"influence\":0.1600000000282768,\"type\":\"custom\"},\"transitionLinked\":false},{\"time\":0.38,\"value\":"+altitude2+",\"transitionIn\":{\"x\":-0.0608,\"y\":0.0000013126714683318004,\"influence\":0.16000000003729023,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.04053333333333333,\"y\":-8.751143122212002e-7,\"influence\":0.16000000003729023,\"type\":\"auto\"},\"transitionLinked\":false},{\"time\":0.6333333333333333,\"value\":"+altitude3+",\"transitionIn\":{\"x\":-0.04053333333333333,\"y\":8.437911229293384e-7,\"influence\":0.16000000003466852,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.03072000000000001,\"y\":-6.395048510622354e-7,\"influence\":0.16000000003466852,\"type\":\"auto\"},\"transitionLinked\":false},{\"time\":0.8253333333333334,\"value\":"+altitude4+",\"transitionIn\":{\"x\":-0.03072000000000001,\"y\":3.8798365497756674e-7,\"influence\":0.1600000000127607,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.02794666666666666,\"y\":-3.5295735279209194e-7,\"influence\":0.16000000001276068,\"type\":\"auto\"},\"transitionLinked\":false},{\"time\":1,\"value\":"+altitude5+",\"transitionIn\":{\"x\":-0.03349333333333334,\"y\":1.3808535472651564e-7,\"influence\":0.16000000000135978,\"type\":\"custom\"},\"transitionLinked\":false}],\"visible\":true}]}]},{\"type\":\"cameraRotationGroup\",\"attributes\":[{\"type\":\"rotationX\",\"value\":{\"maxValueRange\":360,\"minValueRange\":0},\"visible\":true},{\"type\":\"rotationY\",\"value\":{\"maxValueRange\":180,\"minValueRange\":0},\"visible\":true}]},{\"type\":\"cameraTargetEffect\",\"attributes\":[{\"type\":\"poi\",\"value\":{\"maxValueRange\":71488366.22893658,\"minValueRange\":-6371022.11950216,\"relative\":0},\"visible\":true,\"attributesLocked\":true,\"attributes\":[{\"type\":\"longitudePOI\",\"value\":{\"maxValueRange\":180,\"minValueRange\":-180,\"relative\":" + longitude + "},\"keyframes\":[{\"time\":0,\"value\":" + longitude + "}],\"visible\":true},{\"type\":\"latitudePOI\",\"value\":{\"maxValueRange\":89.9999,\"minValueRange\":-89.9999,\"relative\":" + latitude1 + "},\"keyframes\":[{\"time\":0,\"value\":" + latitude1 + "}],\"visible\":true},{\"type\":\"altitudePOI\",\"value\":{\"maxValueRange\":65117481,\"minValueRange\":1,\"relative\":"+POIaltitude+"},\"keyframes\":[{\"time\":0,\"value\":"+POIaltitude+"}],\"visible\":true}]},{\"type\":\"influence\",\"value\":{\"maxValueRange\":1,\"minValueRange\":0},\"visible\":true}],\"visible\":true}]},{\"type\":\"environmentGroup\",\"attributes\":[{\"type\":\"planet\",\"value\":{\"world\":\"earth\"},\"visible\":true},{\"type\":\"clouddate\",\"value\":{\"maxValueRange\":1624023000000,\"minValueRange\":1623943800000,\"relative\":0.9772727272727273},\"visible\":true}]}]}],\"has_started\":true,\"has_finished\":true,\"playbackManager\":{\"range\":{\"start\":0,\"end\":300}}}";
        return esp;

    }

}
