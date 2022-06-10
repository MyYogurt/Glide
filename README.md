# Glide

A pure Java, lightweight, HTTP library.

Alternative to the HttpServer class.

Requires at least Java 8.

## Current Limitations

HTTPS, chunk transfer encoding, and compression (gzip, deflate) are NOT supported at this time.

## Example
```java
import org.moisiadis.glide.util.network.HTTPExchange;
import org.moisiadis.glide.util.network.HTTPRequest;

import java.io.File;
import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		Glide glide = new Glide(80, 4);
		glide.setContext("/", new Handler());
		try {
			glide.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class Handler implements HTTPExchange {
	@Override
	public void handle(HTTPRequest request) {
		File file;
		if (request.getRequestPath().equals("/")) {
			file = new File("html/index.html");
		} else {
			file = new File("html" + request.getRequestPath());
		}

		if (!file.exists()) {
			try {
				request.sendResponse(404);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}

		try {
			final String mimeType = "text/html";
			request.sendResponse(200, mimeType, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
```
