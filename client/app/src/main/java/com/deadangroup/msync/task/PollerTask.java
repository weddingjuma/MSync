
package com.deadangroup.msync.task;

import com.deadangroup.msync.App;
import com.deadangroup.msync.HttpData;

import org.apache.http.message.BasicNameValuePair;

public class PollerTask extends HttpTask {

    public PollerTask(App app) {
        super(app, new BasicNameValuePair("action", App.ACTION_OUTGOING));
    }

    @Override
    protected void onPostExecute(HttpData data) {
        super.onPostExecute(data);
        app.markPollComplete();
    }

    @Override
    protected void handleUnknownContentType(String contentType)
            throws Exception
    {
        throw new Exception("Invalid response type " + contentType);
    }
}
