package com.lglab.diego.simple_cms.create.utility.model.balloon;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.lglab.diego.simple_cms.create.utility.IJsonPacker;
import com.lglab.diego.simple_cms.create.utility.model.Action;
import com.lglab.diego.simple_cms.create.utility.model.ActionIdentifier;
import com.lglab.diego.simple_cms.create.utility.model.poi.POI;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is in charge of creating a placemark in current location
 * The class has a poi with the location information
 * making the class use composition over inheritance
 */
public class Balloon extends Action implements IJsonPacker, Parcelable {


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Balloon createFromParcel(Parcel in) {
            return new Balloon(in);
        }

        public Balloon[] newArray(int size) {
            return new Balloon[size];
        }
    };

    private POI poi;
    private String description;
    private Uri imageUri;
    private String imagePath;
    private String videoPath;

    /**
     * Empty Constructor
     */
    public Balloon() {
        super(ActionIdentifier.BALLOON_ACTIVITY.getId());
    }

    public Balloon(long id, POI poi, String description, Uri imageUri, String imagePath, String videoPath) {
        super(id, ActionIdentifier.BALLOON_ACTIVITY.getId());
        this.poi = poi;
        this.description = description;
        this.imageUri = imageUri;
        this.imagePath = imagePath;
        this.videoPath = videoPath;
    }

    public Balloon(Parcel in) {
        super(in.readLong(), ActionIdentifier.BALLOON_ACTIVITY.getId());
        this.poi = in.readParcelable(POI.class.getClassLoader());
        this.description = in.readString();
        this.imageUri = in.readParcelable(Uri.class.getClassLoader());
        this.imagePath = in.readString();
        this.videoPath = in.readString();
    }

    public Balloon(Balloon balloon) {
        super(balloon.getId(), ActionIdentifier.BALLOON_ACTIVITY.getId());
        this.poi = balloon.poi;
        this.description = balloon.description;
        this.imageUri = balloon.imageUri;
        this.imagePath = balloon.imagePath;
        this.videoPath = balloon.videoPath;
    }

    public static Balloon getBalloon(com.lglab.diego.simple_cms.db.entity.Balloon actionDB) {
        POI poi = POI.getSimplePOI(actionDB.actionId, actionDB.simplePOI);
        Uri imageUri = actionDB.imageUriBalloon != null ? Uri.parse(actionDB.imageUriBalloon) : null;
        return  new Balloon(actionDB.actionId, poi, actionDB.descriptionBalloon, imageUri, actionDB.imagePathBalloon, actionDB.videoPathBalloon);
    }

    public POI getPoi() {
        return poi;
    }

    public Balloon setPoi(POI poi) {
        this.poi = poi;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Balloon setDescription(String description) {
        this.description = description;
        return this;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public Balloon setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
        return this;
    }


    public String getImagePath() {
        return imagePath;
    }

    public Balloon setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public Balloon setVideoPath(String videoPath) {
        this.videoPath = videoPath;
        return this;
    }

    @Override
    public JSONObject pack() throws JSONException {
        JSONObject obj = new JSONObject();

        obj.put("balloon_id", this.getId());
        obj.put("type", this.getType());
        obj.put("place_mark_poi", poi.pack());
        obj.put("description", description);
        obj.put("image_uri", imageUri != null ? imageUri.toString(): "");
        obj.put("image_path", imagePath != null ? imagePath: "");
        obj.put("video_path", videoPath != null ? videoPath: "");

        return obj;
    }

    @Override
    public Balloon unpack(JSONObject obj) throws JSONException {

        this.setId(obj.getLong("balloon_id"));
        this.setType(obj.getInt("type"));

        POI newPoi = new POI();
        poi =  newPoi.unpack(obj.getJSONObject("place_mark_poi"));

        description = obj.getString("description");
        String uri = obj.getString("image_uri");
        imageUri = !uri.equals("") ?  Uri.parse(obj.getString("image_uri")):null;
        imagePath = obj.getString("image_path");
        videoPath = obj.getString("video_path");

        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "Location Name: " + this.poi.getPoiLocation().getName() + " Image Uri: " + this.imageUri.toString()  + " Video URL: " +  this.videoPath;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(this.getId());
        parcel.writeParcelable(poi, flags);
        parcel.writeString(description);
        parcel.writeParcelable(imageUri, flags);
        parcel.writeString(imagePath);
        parcel.writeString(videoPath);
    }
}