package com.yeleman.fondasms.task;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.yeleman.fondasms.App;
import com.yeleman.fondasms.HttpData;
import com.yeleman.fondasms.JsonUtils;
import com.yeleman.fondasms.R;
import com.yeleman.fondasms.XmlUtils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseHttpTask extends AsyncTask<String, Void, HttpData> {

    protected App app;
    protected String url;
    protected List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

    private List<FormBodyPart> formParts;
    protected boolean useMultipartPost = false;
    protected HttpPost post;
    protected Throwable requestException;

    public static final String LOG_NAME = "fondaSMS";

    public BaseHttpTask(App app, String url, BasicNameValuePair... paramsArr)
    {
        this.url = url;
        this.app = app;
        params = new ArrayList<BasicNameValuePair>(Arrays.asList(paramsArr));

        params.add(new BasicNameValuePair("version", "" + app.getPackageInfo().versionCode));
    }

    public void addParam(String name, String value)
    {
        params.add(new BasicNameValuePair(name, value));
    }

    public void setFormParts(List<FormBodyPart> formParts)
    {
        useMultipartPost = true;
        this.formParts = formParts;
    }

    protected HttpPost makeHttpPost() throws Exception
    {
        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("User-Agent", app.getText(R.string.app_name) + "/" + app.getPackageInfo().versionName + " (Android; SDK "+Build.VERSION.SDK_INT + "; " + Build.MANUFACTURER + "; " + Build.MODEL+")");

        if (useMultipartPost)
        {
            MultipartEntity entity = new MultipartEntity();//HttpMultipartMode.BROWSER_COMPATIBLE);

            Charset charset = Charset.forName("UTF-8");

            for (BasicNameValuePair param : params)
            {
                entity.addPart(param.getName(), new StringBody(param.getValue(), charset));
            }

            for (FormBodyPart formPart : formParts)
            {
                entity.addPart(formPart);
            }
            httpPost.setEntity(entity);
        }
        else
        {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        }

        return httpPost;
    }

    protected HttpData doInBackground(String... ignored)
    {
        try
        {
            post = makeHttpPost();

            HttpClient client = app.getHttpClient();
            HttpResponse hr = client.execute(post);
            return new HttpData(hr);
        }
        catch (Throwable ex)
        {
            requestException = ex;

            try
            {
                String message = ex.getMessage();
                // workaround for https://issues.apache.org/jira/browse/HTTPCLIENT-881
                if ((ex instanceof IOException)
                        && message != null && message.equals("Connection already shutdown"))
                {
                    // app.log("Retrying request");
                    post = makeHttpPost();
                    HttpClient client = app.getHttpClient();

                    HttpResponse hr = client.execute(post);
                    return new HttpData(hr);
                }
            }
            catch (Throwable ex2)
            {
                requestException = ex2;
            }
        }

        return null;
    }

    protected String getErrorText(HttpData data)
            throws Exception
    {
        String contentType = getContentType(data);
        String error = null;

        if (contentType.startsWith("application/json"))
        {
            JSONObject json = JsonUtils.parseResponse(data.body);
            error = JsonUtils.getErrorText(json);
        }
        else if (contentType.startsWith("text/xml"))
        {
            Document xml = XmlUtils.parseResponse(data.body);
            error = XmlUtils.getErrorText(xml);
        }

        if (error == null)
        {
            error = "HTTP " + data.response.getStatusLine().getStatusCode();
        }
        return error;
    }

    protected String getContentType(HttpData data)
    {
        Header contentTypeHeader = data.response.getFirstHeader("Content-Type");
        return (contentTypeHeader != null) ? contentTypeHeader.getValue() : "";
    }

    @Override
    protected void onPostExecute(HttpData data) {
        if (data == null){
            app.log("HTTP DATA is null");
           // handleRequestException(requestException);
           // handleFailure();
        }
        else if (data.response != null)
        {
            try
            {
                int statusCode = data.response.getStatusLine().getStatusCode();

                if (statusCode == 200)
                {
                    handleResponse(data);
                }
                else if (statusCode >= 400 && statusCode <= 499)
                {
                    handleErrorResponse(data);
                    handleFailure();
                }
                else
                {
                    throw new Exception("HTTP " + statusCode);
                }
            }
            catch (Throwable ex)
            {
                post.abort();
                handleResponseException(ex);
                handleFailure();
            }

         /*
           try {
                response.getEntity().consumeContent();
             } catch (IOException ex){
             }
            */
        }
        else
        {
            handleRequestException(requestException);
            handleFailure();
        }
    }

    protected void handleResponse(HttpData data) throws Exception
    {
    }

    protected void handleErrorResponse(HttpData data) throws Exception
    {
    }

    protected void handleFailure()
    {
    }

    protected void handleRequestException(Throwable ex)
    {
    }

    protected void handleResponseException(Throwable ex)
    {
    }

}
