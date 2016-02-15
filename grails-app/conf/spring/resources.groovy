import br.com.sigatec.connector.SigaWebConnector
import br.com.sigatec.parser.SigaParser

// Place your Spring DSL code here
beans = {
    sigaConnector(SigaWebConnector) { bean ->
        bean.scope = 'request'
        bean.singleton = false
    }

    loginParser(SigaParser) { bean ->
        bean.scope = 'request'
        bean.singleton = false
    }
}
