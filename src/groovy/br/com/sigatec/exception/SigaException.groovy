package br.com.sigatec.exception

import org.codehaus.groovy.grails.web.json.JSONObject

/**
 * Created by tinguan on 22/02/16.
 */
class SigaException extends Exception {

    String code
    String errorMessage

    public SigaException() {
        super();
    }

    public SigaException(String message) {
        super(message);
    }

    public SigaException(String message, Throwable cause) {
        super(message, cause);
    }

    public SigaException(Throwable e) {
        super(e);
    }

    def createResponse(SigaException e){

        def now = Calendar.instance.time.time
        def response = new JSONObject()
        response.put("status", e.code)
        response.put("error", e.errorMessage)
        response.put("path", "/api/boletim")
        response.put("timestamp", now)
        response.put("message", e.message)
    }

}
