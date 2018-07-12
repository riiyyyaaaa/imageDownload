/**
 *
 */
package com.riya;

import java.util.Timer;
import java.util.TimerTask;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;

/**
 * @author semin
 *
 */
public class StoreData {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//connect to flickr
		String key = "YOUR_APIKEY";
		String svr = "www.flickr.com";
		REST rest = new REST();
		rest.setHost(svr);
		SendData send = new SendData();
		final Flickr flickr = new Flickr(key, svr, rest);
		Flickr.debugStream = false;

		int test = 0;

		//Timer
		TimerTask task = new TimerTask() {
			public void run() {
				SendData sd = new SendData();
				try {
					sd.run(flickr);
					sd.join();
					System.out.println("All finish");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		Timer timer = new Timer();

		timer.schedule(task, 5000L, 1000L);

	}

}
