package com.deadangroup.msync.task;

import com.deadangroup.msync.HttpData;
import com.deadangroup.msync.IncomingMessage;

import org.apache.http.message.BasicNameValuePair;

public class ForwarderTask extends HttpTask {

    private IncomingMessage message;

    public ForwarderTask(IncomingMessage message, BasicNameValuePair... paramsArr) {
        super(message.app, paramsArr);
        this.message = message;
    }

    @Override
    protected String getDefaultToAddress() {
        return message.getFrom();
    }

    @Override
    protected void handleResponse(HttpData data) throws Exception {
        app.inbox.messageForwarded(message);
        super.handleResponse(data);
    }

    @Override
    protected void handleFailure() {
        app.inbox.messageFailed(message);
    }
}
