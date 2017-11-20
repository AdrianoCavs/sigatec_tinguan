package br.com.sigatec.connector

import org.jsoup.Connection
import org.jsoup.Jsoup
import org.springframework.context.annotation.Scope

import static org.jsoup.Connection.Method.GET
import static org.jsoup.Connection.Method.POST


/**
 * Created by tinguan on 25/01/16.
 */
@Scope("request")
class SigaWebConnector {

    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.111 Safari/537.36";


    public String get(String url, Map<String, String> cookies){
        return this.processRequest(url, GET, null, cookies)
    }

    public String post(String url, Map data, Map<String, String> cookies)  {
        return this.processRequest(url, POST, data, cookies)
    }

    private String processRequest(String url, Connection.Method method, Map<String, String> data, Map<String, String> cookies) {
        try {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT).followRedirects(true).ignoreContentType(true).method(method).validateTLSCertificates(false)
                    .timeout(300000);
            if (data != null && !data.isEmpty()) {
                connection.data(data);
            }

            connection.header("Accept-Language", "en-US,en;q=0.8,pt;q=0.6,es;q=0.4");

            if (cookies != null && !cookies.isEmpty()) {
                connection.cookies(cookies);
            }
            Connection.Response response = connection.execute();
            if (response != null) {
                if (cookies == null) {
                    cookies = new HashMap<String, String>();
                }
                mergeCookies(response, cookies)
                if (response.statusCode() == 302) {
                    return processRequest(response.header('Location').toString(), GET, [:], cookies)
                }
                String body = response.body()
                return body;
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    private mergeCookies(Connection.Response response, Map currentCookies){
        response.cookies().each{ key, value ->
            if(key && value) {
                currentCookies.put(key, value)
            }
            if(!value) {
                currentCookies.remove(key)
            }
        }
    }
}