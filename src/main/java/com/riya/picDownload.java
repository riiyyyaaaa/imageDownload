package com.riya;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.GeoData;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.SearchParameters;
import com.flickr4java.flickr.photos.geo.GeoInterface;

/*
 *移行前のもの
 *現在はSearchDataに移行
 *
 * flickrから写真を探す
 *
 */

public class picDownload {

	public void download(ArrayList<String> list, ArrayList<String> title) throws IOException {
		String strurl = new String();

		System.out.print("Now Downloading ");
		for (int j = 0; j < list.size(); j++) {
			strurl = list.get(j);
			URL url = new URL(strurl);

			URLConnection conn = url.openConnection();
			InputStream in = conn.getInputStream();

			File file = new File("c:\\photo\\" + title.get(j) + ".jpg");
			FileOutputStream out = new FileOutputStream(file, false);
			System.out.print(".");

			int i;
			while ((i = in.read()) != -1) {
				out.write(i);
			}

			out.close();
			in.close();
			if (j == (list.size() - 1)) {
				System.out.println(".");
				System.out.println("finish!");
			}
		}
	}

	// longitude,latitude,accuracyにわけてsplitを作る
	public String splitLon(String geostr) {
		String[] geo = new String[geostr.length()];
		String[] str = new String[geostr.length()];
		String str2 = new String();
		StringBuilder longi = new StringBuilder();

		for (int i = 0; i < geostr.length(); i++) {
			str2 = String.valueOf(geostr.charAt(i));
			str[i] = str2;
		}
		int k = 7;
		while (k != str.length) {
			if (str[k - 7].equals("g") && str[k - 2].equals("e")) {
				int m = k;
				String s = new String();
				s = "l";
				while (str.length != m) {
					if (str[m].equals(s)) {
						break;
					}
					geo[m - k] = str[m];
					m++;
				}

				// System.out.print("longitude!!!: ");
				for (int n = 0; n < m - k - 1; n++) {
					// System.out.print( geo[n]);
					longi.append(geo[n]);
				}
				// System.out.println("");
			}

			k++;
		}
		return longi.toString();
	}

	public String splitLat(String geostr) {
		String[] geo = new String[geostr.length()];
		String[] str = new String[geostr.length()];
		String str2 = new String();
		StringBuilder lati = new StringBuilder();

		for (int i = 0; i < geostr.length(); i++) {
			str2 = String.valueOf(geostr.charAt(i));
			str[i] = str2;
		}
		int k = 10;
		while (k != str.length) {
			if (str[k - 7].equals("t") && str[k - 2].equals("e")) {
				int m = k;
				String s = new String();
				s = "a";
				while (str.length != m) {
					if (str[m].equals(s)) {
						break;
					}
					geo[m - k] = str[m];
					m++;
				}

				// System.out.print("latitude!!!: ");
				for (int n = 0; n < m - k - 1; n++) {
					// System.out.print( geo[n]);
					lati.append(geo[n]);
				}
				// System.out.println("");
			}

			k++;
		}
		return lati.toString();
	}

	public String splitAc(String geostr) {
		String[] geo = new String[geostr.length()];
		String[] str = new String[geostr.length()];
		String str2 = new String();
		StringBuilder ac = new StringBuilder();

		for (int i = 0; i < geostr.length(); i++) {
			str2 = String.valueOf(geostr.charAt(i));
			str[i] = str2;
		}
		int k = 15;
		while (k != str.length) {
			if (str[k - 7].equals("c") && str[k - 2].equals("y")) {
				int m = k;
				String s = new String();
				s = "a";
				while (str.length != m) {
					if (str[m].equals(s)) {
						break;
					}
					geo[m - k] = str[m];
					m++;
				}

				// System.out.print("accuracy!!!: ");
				for (int n = 0; n < m - k - 1; n++) {
					// System.out.print( geo[n]);
					ac.append(geo[n]);
				}
				// System.out.println("");
			}

			k++;
		}
		return ac.toString();
	}

	// flickrから写真をダウンロード
	public void search(ArrayList<String> list, ArrayList<String> geolist, ArrayList<String> title) throws Exception {
		String key = "YOUR_APIKEY";
		String svr = "www.flickr.com";
		REST rest = new REST();
		rest.setHost(svr);

		Flickr flickr = new Flickr(key, svr, rest);
		Flickr.debugStream = false;

		SearchParameters searchParams = new SearchParameters();

		// ソートを人気順
		// searchParams.setSort(SearchParameters.INTERESTINGNESS_DESC);
		// geoが存在するものだけ
		searchParams.setHasGeo(true);

		PhotosInterface photosInterface = flickr.getPhotosInterface();
		PhotoList photoList = photosInterface.search(searchParams, 5, 1);
		GeoInterface geoInterface = flickr.getGeoInterface();

		ArrayList<String> idlist = new ArrayList<String>();

		if (photoList != null) {
			int i = 0;
			// System.out.println("Get!");
			for (i = 0; i < photoList.size(); i++) {
				// get(i) リストでi番目にある要素を取り出す
				Photo photo = (Photo) photoList.get(i);
				// System.out.println(photo);
				if (photo != null) {
					// getIdでphotoIdの取得
					list.add("https://farm" + photo.getFarm() + ".staticflickr.com/" + photo.getServer() + "/"
							+ photo.getId() + "_" + photo.getSecret() + ".jpg");
					idlist.add(photo.getId());

					// list.add("https://farm" + photo.getFarm() + "staticflickr.com/" +
					// photo.getServer() + "/" + photo.getId() +"_" + photo.getSecret() + ".jpg");
					System.out.println("https://farm" + photo.getFarm() + ".staticflickr.com/" + photo.getServer() + "/"
							+ photo.getId() + "_" + photo.getSecret() + ".jpg");
					// System.out.println(geoInterface.getLocation(photo.getId()));
					GeoData geo = geoInterface.getLocation(photo.getId());
					geolist.add(geo.toString());
					System.out.println(geolist.get(i));

					// list.add(photo.getFarm() + "_" + photo.getServer() + "_" + photo.getId() +
					// "_" + photo.getSecret() + "_" + geolist.get(i));
					title.add(photo.getId());
				}
			}
		}

	}

	public static void main(String[] args) throws Exception {

		picDownload pi = new picDownload();
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> geolist = new ArrayList<String>();
		ArrayList<String> title = new ArrayList<String>();
		// pi.download();
		pi.search(list, geolist, title);
		// pi.download(list, title);
		// System.out.println("Ok");
		// pi.split(geolist);
	}
}
