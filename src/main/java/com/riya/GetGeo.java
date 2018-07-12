package com.riya;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.photos.GeoData;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.geo.GeoInterface;
import com.flickr4java.flickr.FlickrException;

public class GetGeo extends Thread{
    public void main(String[] args){

    }

	public float[][] run(PhotoList photoList, Flickr flickr) throws FlickrException{
		float[][] geodata = new float[photoList.size()][3];
		Photo photo = new Photo();
		GeoInterface geoInterface = flickr.getGeoInterface();

		if(photoList != null){
			
			int i=0;
			System.out.println("GeoData Get");

			for(i = 0; i < photoList.size(); i++){

				//get(i) リストでi番目にある要素を取り出す
				photo = (Photo)photoList.get(i);

				if(photo != null){

					GeoData geo = geoInterface.getLocation(photo.getId());
					//Longitude,Latitudeの順で入れる
					geodata[i][0] = geo.getLongitude();
					geodata[i][1] = geo.getLatitude();
					geodata[i][2] = geo.getAccuracy();

					System.out.println(geo.toString());
					/*
					System.out.println("Longitude: " + geodata[i][0]);
					System.out.println("Latitude: " + geodata[i][1]);
					System.out.println("Accuracy: " + geodata[i][2]);
					*/
				}
			}
		}
		return geodata;
	}

}