package com.hagilap.talipaapp;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import android.content.Context;
import android.util.Log;
import java.util.Map;
import java.util.Dictionary;
import android.util.ArrayMap;
import java.io.File;
import android.os.FileUtils;
import android.webkit.MimeTypeMap;

public class MyServer implements Runnable {

	HttpServer mHttpServer;

	ArrayMap<String, String> files;

	private Context ctx;


	public MyServer(Context ctx) {
		this.ctx = ctx;
		files = new ArrayMap<String, String>();
		loadFiles("");
//		files.put("index.html", "");
//		files.put("vite.svg", "");
//		files.put("assets/index.4d45b907.css", "");
//		files.put("assets/index.7a700f0d.js", "text/javascript");
		try {
			mHttpServer = HttpServer.create(new InetSocketAddress(8000), 0);
			mHttpServer.setExecutor(Executors.newCachedThreadPool());

			for (String file : files.keySet()) {
				String type = files.get(file);
				mHttpServer.createContext("/"+file, new MyHandler(file, ctx, type));
			}
		} catch (Exception e) {
			Log.e("Read me", e.getMessage());
			e.printStackTrace();
		}
	}

	boolean loadFiles(String dir) {
		String[] list;

		try {
			list = ctx.getAssets().list(dir);
			if (list.length > 0){
				for(String file : list){
					String separator = dir.equals("") ? "" : "/";
					if (!loadFiles(dir + separator + file)){
						return false;
					} else {
						files.put(dir + separator + file, getType(file));
						Log.i("test",dir + separator + file+":"+ getType(file));
					}
				}
			}
		} catch (IOException e) {
			return false;
		}

		return true;
	}

	String getType(String filename){
		String type = "";
		String extension = MimeTypeMap.getFileExtensionFromUrl(filename);
		if (extension != null){
			type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		}
		if ("js".equals(extension)) {
			type = "text/javascript";
		}
		return type == null ? "": type;
	}
	
	@Override
	public void run() {
		if (mHttpServer != null) {
			mHttpServer.start();
		} else {
			Log.e("error", "Failed to start server");
		}
	}

}
