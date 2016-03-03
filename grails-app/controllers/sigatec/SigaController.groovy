package sigatec

import br.com.sigatec.exception.InvalidPasswordException
import br.com.sigatec.exception.SigaException
import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONObject

class SigaController {

    def sigaService
    def sigaException

    def auth() {
        log.info("POST Siga User Credential")

        try {
            JSONObject param = request.JSON as JSONObject
            String login = param.login
            String password = param.password
            def resposta = sigaService.auth(login, password)
            render resposta as JSON

        } catch (InvalidPasswordException i){
            def resposta = sigaException.createResponse(i)
            response.status = 401
            render resposta as JSON

        } catch(SigaException e){
            def resposta = sigaException.createResponse(e)
            response.status = 500
            render resposta as JSON
        }
    }
}
