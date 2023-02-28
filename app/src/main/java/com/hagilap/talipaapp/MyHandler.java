package com.hagilap.talipaapp;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import android.content.Context;
import android.util.Log;
import com.sun.net.httpserver.Headers;

public class MyHandler implements HttpHandler {

	String path, type;
	Context ctx;

	public MyHandler(String path, Context ctx, String type) {
		this.path = path;
		this.type = type;
		this.ctx = ctx;
	}

	@Override
	public void handle(HttpExchange p1) {
		byte[] file = readFile();
		try {
			
			Headers header = p1.getResponseHeaders();

			if (!type.equals("")){
				header.set("Content-Type", type);
			}
			p1.sendResponseHeaders(200, file.length);
			OutputStream os = p1.getResponseBody();
			os.write(file);
			os.close();
		} catch (Exception e) {
			Log.e("Im here", "read error below");
			e.printStackTrace();
		}
	}

	byte[] readFile() {
		byte[] buffer = null;
		try {
			InputStream stream = ctx.getAssets().open(path);
			int size = stream.available();
			buffer = new  byte[size];
			stream.read(buffer);
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer;
	}
}
