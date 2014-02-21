
package com.jckbt.github.octodex;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.REST;

import javax.xml.parsers.ParserConfigurationException;

public class FlickrHelper {

    private static FlickrHelper instance = null;
    private static final String API_KEY = "3e4aaa8af34f860ea3279b8a49b647bc";
    public static final String API_SEC = "9d5206181adea891";

    public static FlickrHelper getInstance() {
        if (instance == null) {
            instance = new FlickrHelper();
        }

        return instance;
    }

    public Flickr getFlickr() {
        try {
            Flickr f = new Flickr(API_KEY, API_SEC, new REST());
            return f;
        } catch (ParserConfigurationException e) {
            return null;
        }
    }

}
