package com.lglab.goutam.simple_cms.web_scraping.data;

import com.lglab.goutam.simple_cms.create.utility.IJsonPacker;

import org.json.JSONException;
import org.json.JSONObject;


public class GDG extends InfoScraping implements IJsonPacker {

    private long id;
    private String urlName;
    private String status;
    private String city;
    private String country;
    private String name;
    private double longitude;
    private double latitude;
    private double position;

    public GDG(){
        super(Constant.GDG.getId());
    }

    public GDG(long id, String urlName, String status, String city, String country, String name, double longitude, double latitude,double position) {
        super(Constant.GDG.getId());
        this.id = id;
        this.urlName = urlName;
        this.status = status;
        this.city = city;
        this.country = country;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.position = position;
    }

    public long getId() {
        return id;
    }

    public String getUrlName() {
        return urlName;
    }

    public String getStatus() {
        return status;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
    public double getPosition() { return position;}

    @Override
    public JSONObject pack() throws JSONException {
        JSONObject obj = new JSONObject();


        obj.put("type", Constant.GDG.getId());
        obj.put("id", id);
        obj.put("urlname", urlName);
        obj.put("status", status);
        obj.put("city", city);
        obj.put("country", country);
        obj.put("name", name);
        obj.put("lon", longitude);
        obj.put("lat", latitude);

        return obj;
    }

    @Override
    public GDG unpack(JSONObject obj) throws JSONException {

        this.setType(Constant.GDG.getId());
        id = obj.getLong("id");
        urlName = obj.getString("urlname");
        status = obj.getString("status");
        city = obj.getString("city");
        country = obj.getString("country");
        name = obj.getString("name");
        longitude = obj.getDouble("lon");
        latitude = obj.getDouble("lat");

        return this;
    }

}
