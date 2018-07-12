package com.riya;

import java.util.ArrayList;

import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;

public class GetId extends Thread{
    public void main(String[] args){

    }

	public ArrayList<String> run(PhotoList photoList){
		ArrayList<String> idlist = new ArrayList<String>();
		Photo photo = new Photo();
		if(photoList != null){
			for(int i=0; i<photoList.size(); i++){
				photo = (Photo)photoList.get(i);
				if(photo != null){
					idlist.add(photo.getId()+photo.getSecret()+photo.getFarm());
				}
			}
		}
		return idlist;
	}
}