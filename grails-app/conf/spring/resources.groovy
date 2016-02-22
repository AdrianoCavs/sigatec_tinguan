import br.com.sigatec.connector.SigaWebConnector
import br.com.sigatec.crawler.SigaCrawler
import br.com.sigatec.exception.SigaException
import br.com.sigatec.parser.SigaParser

// Place your Spring DSL code here
beans = {
    sigaConnector(SigaWebConnector) { bean ->
        bean.scope = 'request'
        bean.singleton = false
    }

    sigaParser(SigaParser) { bean ->
        bean.scope = 'request'
        bean.singleton = false
    }

    sigaCrawler(SigaCrawler) {bean ->
        bean.scope = 'request'
        bean.singleton = false
    }

    sigaException(SigaException) { bean ->
        bean.scope = 'request'
        bean.singleton = false
    }
}
