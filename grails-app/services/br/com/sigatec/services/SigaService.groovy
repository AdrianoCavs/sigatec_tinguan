package br.com.sigatec.services

import br.com.sigatec.business.Aluno
import br.com.sigatec.business.Disciplina
import br.com.sigatec.connector.SigaWebConnector
import br.com.sigatec.parser.SigaParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component


@Component
@Scope("request")
class SigaService {

    @Autowired
    private SigaWebConnector connector

    @Autowired
    private SigaParser parser

    private Map<String, String> cookies

    @Autowired
    public SigaService (){
        this.cookies = new HashMap<String, String>()
    }

    def auth(String login, String password) {
        connector.get("https://www.sigacentropaulasouza.com.br/aluno/login.aspx")
        def mapLogin = parser.parseMapLogin(login,password)

        connector.post("https://www.sigacentropaulasouza.com.br/aluno/login.aspx",mapLogin)
        def gradesPage = connector.get("https://www.sigacentropaulasouza.com.br/aluno/notasparciais.aspx")

        def studentHistoryJson = parser.extractJsonStudentHistory(gradesPage)

        Aluno aluno = new Aluno()
        parser.extractStudentBasicInformations(aluno, studentHistoryJson)

        List<Disciplina> disciplinas = new ArrayList<Disciplina>()



    }
}
