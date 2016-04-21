
package com.yeleman.fondasms.task;

import com.yeleman.fondasms.App;
import com.yeleman.fondasms.HttpData;

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
