package br.com.sigatec.utils

import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.codehaus.jackson.map.ObjectMapper

/**
 * Created by tinguan on 15/02/16.
 */
class JSONResponse {

    private static final Logger log = Logger.getLogger(JSONResponse.class);

    public static JSONObject objectAsJSON(Object object){
        try {
            ObjectMapper mapper = new ObjectMapper();
            String loginResponseJSON = mapper.writeValueAsString(object);
            return new JSONObject(loginResponseJSON);
        } catch (IOException e) {
            log.error("Error while serializing object as a JSON", e);
        }
        return new JSONObject();
    }

    //TODO NAO FUNCIONA
    public  static JSONObject arrayOfObjectAsJSON(ArrayList<Object> arrayList){
        def response =  new JSONArray(arrayList);
        return response
    }
}
