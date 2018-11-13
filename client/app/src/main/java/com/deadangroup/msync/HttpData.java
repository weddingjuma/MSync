package com.deadangroup.msync;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

import java.io.IOException;

public class HttpData {

    public HttpResponse response;
    public String body;

    public HttpData(HttpResponse httpResponse) throws IOException {
        if (httpResponse != null) {
            this.populate(httpResponse);
        }
    }

	public void populate(HttpResponse httpResponse) throws IOException, UnsupportedOperationException {
        response = httpResponse;
        body = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
	}

}
