package com.lglab.goutam.simple_cms.export_esp;

import android.util.Log;

import com.lglab.goutam.simple_cms.create.CreateStoryBoardActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
// this class is in charge of storing coordinates point in hashmap

public class record extends CreateStoryBoardActivity {

// this function update values in hashmap
    public static void Update(Map<String, List<String>> position_data,String key,Double longitude,Double latitude,Double altitude,String esp_mode,String durationText,String name,String position){
        List<String> values = new ArrayList<String>();
        values.add(String.valueOf(longitude));
        values.add(String.valueOf(latitude));
        values.add(String.valueOf(altitude));
        values.add(esp_mode);
        values.add(name);
        values.add(durationText);
        values.add(position);
        position_data.put(key, values);
    }
//    this function delete particular value from hashmap
    public static void Delete(Map<String, List<String>> position_data,String key){
        try { position_data.remove(key); }
        catch (Exception e) { e.printStackTrace(); }
    }

    public static void findanddelete(String position,Map<String, List<String>> people) {
        Collection getter = people.values();
        Iterator i = getter.iterator();
        String key = null;
        while (i.hasNext()) {
            List items = (List) i.next();
            if(position.equals(items.get(6))){
                key = String.valueOf(items.get(4));
                Log.d("chal hauia aagaya aagaya maal aagaya",String.valueOf(items.get(4)));
            }
        }
        Delete(people, key);
    }
}
