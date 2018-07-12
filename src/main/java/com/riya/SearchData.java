package com.riya;

import java.io.IOException;
import java.util.ArrayList;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.photos.GeoData;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.SearchParameters;
import com.flickr4java.flickr.photos.geo.GeoInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;



/*
 *
 * 現行バージョン
 * GeoDataを検索、Longitude, Latitude, Accuracyを抽出
 * SendDataで使用
 *
 */

public class SearchData extends Thread{

	//位置情報を含む写真のURLと位置情報を検索、取得
	public static PhotoList searchPhoto(Flickr flickr) throws Exception{

		Flickr.debugStream = false;

		SearchParameters searchParams = new SearchParameters();

		//geoが存在するものだけ
		searchParams.setHasGeo(true);

		PhotosInterface photosInterface = flickr.getPhotosInterface();

		//取得するphotoの数の変更
		PhotoList photoList = photosInterface.search(searchParams, 25, 1);		
		

		return photoList;
	}

	public ArrayList<String> getURI(PhotoList photoList){
		ArrayList<String> Urilist = new ArrayList<String>();
		Photo photo = new Photo();
		if(photoList != null){
			int i=0;
			//System.out.println("PhotoList Get");
			for(i = 0; i < photoList.size(); i++){
				//get(i) リストでi番目にある要素を取り出す
				photo = (Photo)photoList.get(i);
				if(photo != null){
					//System.out.println("https://farm" + photo.getFarm() + ".staticflickr.com/" + photo.getServer() + "/" + photo.getId() +"_" + photo.getSecret() + ".jpg");
					Urilist.add("https://farm" + photo.getFarm() + ".staticflickr.com/" + photo.getServer() + "/" + photo.getId() +"_" + photo.getSecret() + ".jpg");
					
					//System.out.println("url(user): "+photo.getUrl());
				}
			}
		}
		return Urilist;
	}

	public ArrayList<String> getId(PhotoList photoList){
		ArrayList<String> idlist = new ArrayList<String>();
		Photo photo = new Photo();
		if(photoList != null){
			for(int i=0; i<photoList.size(); i++){
				photo = (Photo)photoList.get(i);
				if(photo != null){
					idlist.add(photo.getId());
				}
			}
		}
		return idlist;
	}

	public float[][] getGeo(PhotoList photoList, Flickr flickr) throws FlickrException{
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

	public ArrayList<String> getPhoto(ArrayList<String> urilist, ArrayList<String> idlist) throws IOException{
		String strurl = new String();
		ArrayList<String> photopath = new ArrayList<String>();

		System.out.print("Now Downloading ");

		for(int i = 0; i < urilist.size(); i++){
			strurl = urilist.get(i);
			URL url = new URL(strurl);

			URLConnection conn = url.openConnection();
			InputStream in = conn.getInputStream();

			String dir = "c:\\photo\\"+ idlist.get(i) +".jpg";
			File file = new File(dir);
			FileOutputStream out = new FileOutputStream(file, false);
			System.out.print(".");

			int j;
			while((j = in.read()) != -1){
				out.write(j);
			}

			out.close();
			in.close();
			if(i == (urilist.size()-1)){
				System.out.println("finish!");
			}
			photopath.add(dir);
		}
		return photopath;
	}

	public static void main(String[] args) throws Exception {

	}

}
