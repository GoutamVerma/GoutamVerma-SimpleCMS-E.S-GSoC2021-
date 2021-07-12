package com.lglab.diego.simple_cms.export_esp;

import androidx.appcompat.app.AppCompatActivity;

import com.lglab.diego.simple_cms.create.utility.model.poi.POI;
import com.lglab.diego.simple_cms.create.utility.model.poi.POICamera;
import com.lglab.diego.simple_cms.create.utility.model.poi.POILocation;

/**
 * This class is in charge of create esp file
 */
public class export_esp extends AppCompatActivity {

    public static double cal_longitude(double longitude){
        double final_longitude;
        if(longitude>0){
            final_longitude=((0.5/180)*longitude)+0.5;
        }
        else if(longitude<0){
            final_longitude=(0.5/180)*longitude;
        }
        else
            final_longitude=0.5;

        return final_longitude;
    }

    public static double cal_latitude(double latitude){
        double final_latitude;
        if(latitude>0){
            final_latitude = ((0.5/90)*latitude)+0.5;
        }
        else if(latitude<0){
            final_latitude = (0.5/90)*latitude;
        }
        else
            final_latitude = 0.5;

        return final_latitude;
    }

    public static double cal_altitude(double altitude){
        double final_altitude;
        final_altitude = (0.982542908896292/50000) * altitude;
        return final_altitude;
    }

    public static int cal_duration(int second){
        int duration_frame;
        duration_frame = second * 30;
        return duration_frame;
    }
    /**
     * this function return an animation for "zoom to" function in esp format
     */
    public static String ZoomTo(POI poi){
        POILocation poiLocation = poi.getPoiLocation();
        POICamera poiCamera = poi.getPoiCamera();
        double altitude = cal_altitude(poiLocation.getAltitude());
        double altitude1 = altitude + 0.2536396684357228;
        double latitude =cal_latitude(poiLocation.getLatitude());
        double longitude =cal_longitude(poiLocation.getLongitude());
        int duration = cal_duration(poiCamera.getDuration());
        double end = 0.95411228992732346 / 32193;
        String esp ="{\"type\":\"quickstart\",\"modelVersion\":16,\"settings\":{\"name\":\""+poiLocation.getName()+"\",\"frameRate\":30,\"dimensions\":{\"width\":1920,\"height\":1080},\"duration\":"+duration+",\"timeFormat\":\"frames\"},\"scenes\":[{\"animationModel\":{\"roving\":false,\"logarithmic\":true,\"groupedPosition\":true},\"duration\":"+duration+",\"attributes\":[{\"type\":\"cameraGroup\",\"attributes\":[{\"type\":\"cameraPositionGroup\",\"attributes\":[{\"type\":\"position\",\"value\":{\"maxValueRange\":71488366.22893658,\"minValueRange\":0,\"relative\":0},\"visible\":true,\"attributesLocked\":true,\"attributes\":[{\"type\":\"longitude\",\"value\":{\"maxValueRange\":180,\"minValueRange\":-180,\"relative\":"+longitude+"},\"keyframes\":[{\"time\":0,\"value\":"+longitude+",\"transitionIn\":{\"x\":-0.2,\"y\":0,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"}},{\"time\":0.8,\"value\":"+longitude+",\"transitionIn\":{\"x\":-0.32000000000000006,\"y\":0,\"influence\":0.4000000000000001,\"type\":\"custom\"},\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"},\"transitionLinked\":false}],\"visible\":true},{\"type\":\"latitude\",\"value\":{\"maxValueRange\":89.9999,\"minValueRange\":-89.9999,\"relative\":"+latitude+"},\"keyframes\":[{\"time\":0,\"value\":"+latitude+",\"transitionIn\":{\"x\":-0.2,\"y\":0,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"}},{\"time\":0.8,\"value\":"+latitude+",\"transitionIn\":{\"x\":-0.32000000000000006,\"y\":0,\"influence\":0.4000000000000001,\"type\":\"custom\"},\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"},\"transitionLinked\":false}],\"visible\":true},{\"type\":\"altitude\",\"value\":{\"maxValueRange\":65117481,\"minValueRange\":1,\"relative\":0.982542908896292},\"keyframes\":[{\"time\":0,\"value\":0.982542908896292,\"transitionIn\":{\"x\":-0.2,\"y\":0,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"}},{\"time\":0.8,\"value\":"+altitude1+",\"transitionIn\":{\"x\":-0.32000000000000006,\"y\":0,\"influence\":0.4000000000000001,\"type\":\"custom\"},\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"},\"transitionLinked\":false}],\"visible\":true}]}]},{\"type\":\"cameraRotationGroup\",\"attributes\":[{\"type\":\"rotationX\",\"value\":{\"maxValueRange\":360,\"minValueRange\":0},\"keyframes\":[{\"time\":0,\"value\":0,\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"}},{\"time\":0.8,\"value\":0,\"transitionIn\":{\"x\":-0.2,\"y\":0,\"type\":\"auto\"}}],\"visible\":true},{\"type\":\"rotationY\",\"value\":{\"maxValueRange\":180,\"minValueRange\":0},\"keyframes\":[{\"time\":0,\"value\":0,\"transitionOut\":{\"x\":0.68,\"y\":0,\"influence\":0.85,\"type\":\"custom\"},\"transitionLinked\":false},{\"time\":0.8,\"value\":0,\"transitionIn\":{\"x\":-0.24,\"y\":0,\"influence\":0.3,\"type\":\"custom\"},\"transitionLinked\":false}],\"visible\":true}]},{\"type\":\"cameraTargetEffect\",\"attributes\":[{\"type\":\"poi\",\"value\":{\"maxValueRange\":71488366.22893658,\"minValueRange\":-6371022.11950216,\"relative\":0},\"visible\":true,\"attributesLocked\":true,\"attributes\":[{\"type\":\"longitudePOI\",\"value\":{\"maxValueRange\":180,\"minValueRange\":-180,\"relative\":0.18016713992972266},\"keyframes\":[{\"time\":0,\"value\":"+longitude+"}],\"visible\":true},{\"type\":\"latitudePOI\",\"value\":{\"maxValueRange\":89.9999,\"minValueRange\":-89.9999,\"relative\":"+latitude+"},\"keyframes\":[{\"time\":0,\"value\":"+latitude+"}],\"visible\":true},{\"type\":\"altitudePOI\",\"value\":{\"maxValueRange\":65117481,\"minValueRange\":1,\"relative\":"+altitude1+"},\"keyframes\":[{\"time\":0,\"value\":"+altitude1+"}],\"visible\":true}]},{\"type\":\"influence\",\"value\":{\"maxValueRange\":1,\"minValueRange\":0,\"relative\":0},\"keyframes\":[{\"time\":0,\"value\":0,\"transitionOut\":{\"x\":0.2,\"y\":0,\"type\":\"auto\"}},{\"time\":0.5333328,\"value\":0,\"transitionIn\":{\"x\":-0.2,\"y\":0,\"type\":\"auto\"}}],\"visible\":true}],\"visible\":true}]},{\"type\":\"environmentGroup\",\"attributes\":[{\"type\":\"planet\",\"value\":{\"world\":\"earth\"},\"visible\":true},{\"type\":\"clouddate\",\"value\":{\"maxValueRange\":1624026600000,\"minValueRange\":1623947400000,\"relative\":0.9318181818181818},\"visible\":true}]}]}],\"has_started\":true,\"has_finished\":true,\"playbackManager\":{\"range\":{\"start\":0,\"end\":150}}}";
        return esp;
    }
    /**
     * this function return an animation for "orbit" function in esp format
     */
    public static String orbit(POI poi){
        POILocation poiLocation = poi.getPoiLocation();
        POICamera poiCamera = poi.getPoiCamera();
        double altitude = cal_altitude(poiLocation.getAltitude());
        double latitude =cal_latitude(poiLocation.getLatitude());
        double latitude1= latitude - 0.0000686298492109;
        double latitude2 = latitude1 - 0.0000686129161325;
        double longitude =cal_longitude(poiLocation.getLongitude());
        double longitude1 = longitude - 0.0000521498534771 ;
        double longitude2 = longitude + 0.0000521498534771;
        int duration = cal_duration(poiCamera.getDuration());
        String esp = "{\"type\":\"quickstart\",\"modelVersion\":16,\"settings\":{\"name\":\""+poiLocation.getName()+"\",\"frameRate\":30,\"dimensions\":{\"width\":1920,\"height\":1080},\"duration\":"+duration+",\"timeFormat\":\"frames\"},\"scenes\":[{\"animationModel\":{\"roving\":false,\"logarithmic\":false,\"groupedPosition\":true},\"duration\":"+duration+",\"attributes\":[{\"type\":\"cameraGroup\",\"attributes\":[{\"type\":\"cameraPositionGroup\",\"attributes\":[{\"type\":\"position\",\"value\":{\"maxValueRange\":71488366.22893658,\"minValueRange\":0,\"relative\":0},\"visible\":true,\"attributesLocked\":true,\"attributes\":[{\"type\":\"longitude\",\"value\":{\"maxValueRange\":180,\"minValueRange\":-180,\"relative\":"+longitude+"},\"keyframes\":[{\"time\":0,\"value\":"+longitude+",\"transitionIn\":{\"x\":0,\"y\":0,\"type\":\"linear\"},\"transitionOut\":{\"x\":0,\"y\":0,\"type\":\"linear\"}},{\"time\":0.25,\"value\":"+longitude1+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},{\"time\":0.5,\"value\":"+longitude+",\"transitionIn\":{\"x\":0,\"y\":0,\"type\":\"linear\"},\"transitionOut\":{\"x\":0,\"y\":0,\"type\":\"linear\"}},{\"time\":0.75,\"value\":"+longitude2+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},{\"time\":1,\"value\":"+longitude+",\"transitionIn\":{\"x\":0,\"y\":0,\"type\":\"linear\"},\"transitionOut\":{\"x\":0,\"y\":0,\"type\":\"linear\"}}],\"visible\":true},{\"type\":\"latitude\",\"value\":{\"maxValueRange\":89.9999,\"minValueRange\":-89.9999,\"relative\":"+latitude+"},\"keyframes\":[{\"time\":0,\"value\":"+latitude+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},{\"time\":0.25,\"value\":"+latitude1+",\"transitionIn\":{\"x\":0,\"y\":0,\"type\":\"linear\"},\"transitionOut\":{\"x\":0,\"y\":0,\"type\":\"linear\"}},{\"time\":0.5,\"value\":"+latitude2+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},{\"time\":0.75,\"value\":"+latitude1+",\"transitionIn\":{\"x\":0,\"y\":0,\"type\":\"linear\"},\"transitionOut\":{\"x\":0,\"y\":0,\"type\":\"linear\"}},{\"time\":1,\"value\":"+latitude+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}}],\"visible\":true},{\"type\":\"altitude\",\"value\":{\"maxValueRange\":65117481,\"minValueRange\":1,\"relative\":0.000009214115779664691},\"keyframes\":[{\"time\":0,\"value\":0.000009214115779664691},{\"time\":0.25,\"value\":0.000009214115779664691},{\"time\":0.5,\"value\":0.000009214115779664691},{\"time\":0.75,\"value\":0.000009214115779664691},{\"time\":1,\"value\":0.000009214115779664691}],\"visible\":true}]}]},{\"type\":\"cameraRotationGroup\",\"attributes\":[{\"type\":\"rotationX\",\"value\":{\"maxValueRange\":360,\"minValueRange\":0},\"visible\":true},{\"type\":\"rotationY\",\"value\":{\"maxValueRange\":180,\"minValueRange\":0},\"visible\":true}]},{\"type\":\"cameraTargetEffect\",\"attributes\":[{\"type\":\"poi\",\"value\":{\"maxValueRange\":71488366.22893658,\"minValueRange\":-6371022.11950216,\"relative\":0},\"visible\":true,\"attributesLocked\":true,\"attributes\":[{\"type\":\"longitudePOI\",\"value\":{\"maxValueRange\":180,\"minValueRange\":-180,\"relative\":"+longitude+"},\"keyframes\":[{\"time\":0,\"value\":"+longitude+"}],\"visible\":true},{\"type\":\"latitudePOI\",\"value\":{\"maxValueRange\":89.9999,\"minValueRange\":-89.9999,\"relative\":"+latitude1+"},\"keyframes\":[{\"time\":0,\"value\":"+latitude1+"}],\"visible\":true},{\"type\":\"altitudePOI\",\"value\":{\"maxValueRange\":65117481,\"minValueRange\":1,\"relative\":9.677648139072125e-7},\"keyframes\":[{\"time\":0,\"value\":9.677648139072125e-7}],\"visible\":true}]},{\"type\":\"influence\",\"value\":{\"maxValueRange\":1,\"minValueRange\":0},\"visible\":true}],\"visible\":true}]},{\"type\":\"environmentGroup\",\"attributes\":[{\"type\":\"planet\",\"value\":{\"world\":\"earth\"},\"visible\":true},{\"type\":\"clouddate\",\"value\":{\"maxValueRange\":1623810600000,\"minValueRange\":1623731400000,\"relative\":0.9318181818181818},\"visible\":true}]}]}],\"has_started\":true,\"has_finished\":true,\"playbackManager\":{\"range\":{\"start\":0,\"end\":600}}}";
        return esp;
    }
    /**
     * this function return an animation for "Spiral" function in esp format
     */
    public static String spiral(POI poi){
        POILocation poiLocation = poi.getPoiLocation();
        POICamera poiCamera = poi.getPoiCamera();
        double altitude = cal_altitude(poiLocation.getAltitude());
        double latitude =cal_latitude(poiLocation.getLatitude());
        double latitude1  = latitude - 0.0000864903794252;
        double latitude2 = latitude1 - 0.0000395858154654;
        double latitude3 = latitude2 + 0.0000395873822476;
        double latitude4 = latitude3 + 0.0000328892744689;
        double longitude =cal_longitude(poiLocation.getLongitude());
        double longitude1 = longitude + 0.0000311936257905;
        double longitude2 = longitude - 0.0000189552971268;
        int duration = cal_duration(poiCamera.getDuration());
        String esp ="{\"type\":\"quickstart\",\"modelVersion\":16,\"settings\":{\"name\":\""+poiLocation.getName()+"\",\"frameRate\":30,\"dimensions\":{\"width\":1920,\"height\":1080},\"duration\":"+duration+",\"timeFormat\":\"frames\"},\"scenes\":[{\"animationModel\":{\"roving\":false,\"logarithmic\":false,\"groupedPosition\":true},\"duration\":"+duration+",\"attributes\":[{\"type\":\"cameraGroup\",\"attributes\":[{\"type\":\"cameraPositionGroup\",\"attributes\":[{\"type\":\"position\",\"value\":{\"maxValueRange\":71488366.22893658,\"minValueRange\":0,\"relative\":0},\"visible\":true,\"attributesLocked\":true,\"attributes\":[{\"type\":\"longitude\",\"value\":{\"maxValueRange\":180,\"minValueRange\":-180,\"relative\":"+longitude+"},\"keyframes\":[{\"time\":0,\"value\":"+longitude+",\"transitionIn\":{\"x\":0,\"y\":0,\"type\":\"linear\"},\"transitionOut\":{\"x\":0,\"y\":0,\"type\":\"linear\"}},{\"time\":0.38,\"value\":"+longitude1+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},{\"time\":0.6333333333333333,\"value\":"+longitude+",\"transitionIn\":{\"x\":-0.04053333333333333,\"y\":0.000008918421723791425,\"influence\":0.16000000387294522,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.03072000000000001,\"y\":-0.000006759224885399817,\"influence\":0.1600000038729452,\"type\":\"auto\"},\"transitionLinked\":false},{\"time\":0.8253333333333334,\"value\":"+longitude2+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},{\"time\":1,\"value\":"+longitude+",\"transitionIn\":{\"x\":0,\"y\":0,\"type\":\"linear\"},\"transitionOut\":{\"x\":0,\"y\":0,\"type\":\"linear\"}}],\"visible\":true},{\"type\":\"latitude\",\"value\":{\"maxValueRange\":89.9999,\"minValueRange\":-89.9999,\"relative\":"+latitude+"},\"keyframes\":[{\"time\":0,\"value\":"+latitude+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},{\"time\":0.38,\"value\":"+latitude1+",\"transitionIn\":{\"x\":-0.0608,\"y\":0.000023178334612516952,\"influence\":0.16000001162645378,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.04053333333333333,\"y\":-0.0000154522230750113,\"influence\":0.16000001162645378,\"type\":\"auto\"},\"transitionLinked\":false},{\"time\":0.6333333333333333,\"value\":"+latitude2+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}},{\"time\":0.8253333333333334,\"value\":"+latitude3+",\"transitionIn\":{\"x\":-0.03072000000000001,\"y\":-0.000012008072351932242,\"influence\":0.16000001222345955,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.02794666666666666,\"y\":0.000010924010264605024,\"influence\":0.16000001222345953,\"type\":\"auto\"},\"transitionLinked\":false},{\"time\":1,\"value\":"+latitude4+",\"transitionIn\":{\"x\":-0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.066,\"y\":0,\"influence\":0.5,\"type\":\"auto\"}}],\"visible\":true},{\"type\":\"altitude\",\"value\":{\"maxValueRange\":65117481,\"minValueRange\":1,\"relative\":0.000022912434572099535},\"keyframes\":[{\"time\":0,\"value\":0.000022912434572099535,\"transitionOut\":{\"x\":0.05141333333333334,\"y\":-9.665974830871797e-7,\"influence\":0.1600000000282768,\"type\":\"custom\"},\"transitionLinked\":false},{\"time\":0.38,\"value\":0.000014647335067236437,\"transitionIn\":{\"x\":-0.0608,\"y\":0.0000013126714683318004,\"influence\":0.16000000003729023,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.04053333333333333,\"y\":-8.751143122212002e-7,\"influence\":0.16000000003729023,\"type\":\"auto\"},\"transitionLinked\":false},{\"time\":0.6333333333333333,\"value\":0.000008743692563762797,\"transitionIn\":{\"x\":-0.04053333333333333,\"y\":8.437911229293384e-7,\"influence\":0.16000000003466852,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.03072000000000001,\"y\":-6.395048510622354e-7,\"influence\":0.16000000003466852,\"type\":\"auto\"},\"transitionLinked\":false},{\"time\":0.8253333333333334,\"value\":0.0000052015070616786125,\"transitionIn\":{\"x\":-0.03072000000000001,\"y\":3.8798365497756674e-7,\"influence\":0.1600000000127607,\"type\":\"auto\"},\"transitionOut\":{\"x\":0.02794666666666666,\"y\":-3.5295735279209194e-7,\"influence\":0.16000000001276068,\"type\":\"auto\"},\"transitionLinked\":false},{\"time\":1,\"value\":0.0000040207785609838845,\"transitionIn\":{\"x\":-0.03349333333333334,\"y\":1.3808535472651564e-7,\"influence\":0.16000000000135978,\"type\":\"custom\"},\"transitionLinked\":false}],\"visible\":true}]}]},{\"type\":\"cameraRotationGroup\",\"attributes\":[{\"type\":\"rotationX\",\"value\":{\"maxValueRange\":360,\"minValueRange\":0},\"visible\":true},{\"type\":\"rotationY\",\"value\":{\"maxValueRange\":180,\"minValueRange\":0},\"visible\":true}]},{\"type\":\"cameraTargetEffect\",\"attributes\":[{\"type\":\"poi\",\"value\":{\"maxValueRange\":71488366.22893658,\"minValueRange\":-6371022.11950216,\"relative\":0},\"visible\":true,\"attributesLocked\":true,\"attributes\":[{\"type\":\"longitudePOI\",\"value\":{\"maxValueRange\":180,\"minValueRange\":-180,\"relative\":"+longitude+"},\"keyframes\":[{\"time\":0,\"value\":"+longitude+"}],\"visible\":true},{\"type\":\"latitudePOI\",\"value\":{\"maxValueRange\":89.9999,\"minValueRange\":-89.9999,\"relative\":"+latitude1+"},\"keyframes\":[{\"time\":0,\"value\":"+latitude1+"}],\"visible\":true},{\"type\":\"altitudePOI\",\"value\":{\"maxValueRange\":65117481,\"minValueRange\":1,\"relative\":0.0000023430236911937523},\"keyframes\":[{\"time\":0,\"value\":0.0000023430236911937523}],\"visible\":true}]},{\"type\":\"influence\",\"value\":{\"maxValueRange\":1,\"minValueRange\":0},\"visible\":true}],\"visible\":true}]},{\"type\":\"environmentGroup\",\"attributes\":[{\"type\":\"planet\",\"value\":{\"world\":\"earth\"},\"visible\":true},{\"type\":\"clouddate\",\"value\":{\"maxValueRange\":1624023000000,\"minValueRange\":1623943800000,\"relative\":0.9772727272727273},\"visible\":true}]}]}],\"has_started\":true,\"has_finished\":true,\"playbackManager\":{\"range\":{\"start\":0,\"end\":300}}}";
        return esp;
    }

}
