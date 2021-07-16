package com.lglab.goutam.simple_cms.web_scraping.data;

public abstract class InfoScraping {

    private int type;

    public InfoScraping(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
