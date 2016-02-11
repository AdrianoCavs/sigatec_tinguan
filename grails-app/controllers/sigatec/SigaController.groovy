package sigatec

import org.codehaus.groovy.grails.web.json.JSONObject

class SigaController {

    def sigaService

    def auth() {
        log.info("POST Siga User Credential")

        try {
            JSONObject param = request.JSON as JSONObject
            String login = param.login
            String password = param.password
            def response = sigaService.auth(login, password)
            log.info("")
        } catch (IllegalStateException e){
            log.info("Unexpected exception at auth siga")
            response.status = 401
            render e.getMessage()
        } catch (Exception e){
            log.info("Internal error at auth siga")
            response.status = 500
            render e.getMessage()
        }
    }
}
