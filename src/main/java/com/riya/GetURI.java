package com.riya;

import java.util.ArrayList;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;

public class GetURI extends Thread{
    public static void main(String[] args){

}
    public ArrayList<String> run(PhotoList photoList){
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
}