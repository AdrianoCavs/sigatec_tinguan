import br.com.sigatec.connector.SigaWebConnector
import br.com.sigatec.parser.LoginParser

// Place your Spring DSL code here
beans = {
    sigaConnector(SigaWebConnector) { bean ->
        bean.scope = 'request'
        bean.singleton = false
    }

    loginParser(LoginParser) { bean ->
        bean.scope = 'request'
        bean.singleton = false
    }
}
