package com.riya;

import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
public class GetPhoto extends Thread{
    public static void main(String[] args){

    }

	public ArrayList<String> run(ArrayList<String> urilist, ArrayList<String> idlist) throws IOException{
		String strurl = new String();
		ArrayList<String> photopath = new ArrayList<String>();

		System.out.print("Now Downloading ");

		for(int i = 0; i < urilist.size(); i++){
			strurl = urilist.get(i);
			URL url = new URL(strurl);

			URLConnection conn = url.openConnection();
			InputStream in = conn.getInputStream();

			String dir = "c:\\photo02\\"+ idlist.get(i) +".jpg";
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
    
}