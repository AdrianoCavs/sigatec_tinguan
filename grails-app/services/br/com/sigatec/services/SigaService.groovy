package br.com.sigatec.services

import br.com.sigatec.business.Aluno
import br.com.sigatec.connector.SigaWebConnector
import br.com.sigatec.parser.LoginParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component


@Component
@Scope("request")
class SigaService {

    @Autowired
    private SigaWebConnector connector

    @Autowired
    private LoginParser parser

    private Map<String, String> cookies

    @Autowired
    public SigaService (){
        this.cookies = new HashMap<String, String>()
    }

    def auth(String login, String password) {
        def home = connector.get("https://www.sigacentropaulasouza.com.br/aluno/login.aspx")
        def mapLogin = parser.parseMapLogin(login,password)

        def loginPlace = connector.post("https://www.sigacentropaulasouza.com.br/aluno/login.aspx",mapLogin)
        def notas = connector.get("https://www.sigacentropaulasouza.com.br/aluno/notasparciais.aspx")

        Aluno aluno = new Aluno()

    }
}
