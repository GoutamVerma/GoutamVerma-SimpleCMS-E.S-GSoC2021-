package com.lglab.goutam.simple_cms.web_scraping.data;

public enum Constant {
    INFO_WEB_SCRAPING(0), GDG(2), REFRESH_WEB_SCRAPING(3);

    private final int id;

    Constant(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
