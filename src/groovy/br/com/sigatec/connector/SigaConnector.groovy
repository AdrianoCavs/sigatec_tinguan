package br.com.sigatec.connector

import org.apache.commons.io.IOUtils
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair
import org.apache.http.protocol.BasicHttpContext
import org.apache.http.protocol.HttpContext
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.client.CookieStore
import org.springframework.context.annotation.Scope


/**
 * Created by tinguan on 25/01/16.
 */
@Scope("request")
class SigaWebConnector {


    private CookieStore cookieStore = new BasicCookieStore()

    def get(String URL){
        HttpGet httpGet = new HttpGet(URL)
        return processRequest(httpGet)
    }

    def post(url, map){
        HttpPost post = new HttpPost(url);
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        map.each{k,v -> urlParameters.add(new BasicNameValuePair(k,v))}

        post.setEntity(new UrlEncodedFormEntity(urlParameters))
        return processRequest(post)

    }

    def processRequest(method){
        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore)
        HttpClient httpclient = HttpClientBuilder.create().build()

        try{
            HttpResponse response = httpclient.execute(method, localContext)
            String body = IOUtils.toString(response.getEntity().getContent())
            return body
        } catch (Exception e){
            throw(e)
        }
    }

}