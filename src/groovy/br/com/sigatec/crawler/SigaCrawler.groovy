package br.com.sigatec.crawler

import br.com.sigatec.business.Aluno
import br.com.sigatec.connector.SigaWebConnector
import br.com.sigatec.exception.InvalidPasswordException
import br.com.sigatec.parser.SigaParser
import org.codehaus.groovy.grails.web.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by tinguan on 21/02/16.
 */
class SigaCrawler {
    public JSONObject studentHistoryJson

    @Autowired
    private SigaWebConnector connector


    @Autowired
    private SigaParser parser


    def login(String login, String password){
        connector.get("https://www.sigacentropaulasouza.com.br/aluno/login.aspx")
        def mapLogin = parser.parseMapLogin(login,password)
        def response = connector.post("https://www.sigacentropaulasouza.com.br/aluno/login.aspx",mapLogin)
        if(parser.isInvalidPassword(response)){
            throw new InvalidPasswordException("Não Autorizado")
        }
    }

    def setAluno(Aluno aluno){
        def gradesPage = connector.get("https://www.sigacentropaulasouza.com.br/aluno/notasparciais.aspx")
        this.studentHistoryJson = parser.extractJsonStudentHistory(gradesPage)
        parser.extractStudentBasicInformations(aluno, this.studentHistoryJson)

        def disciplinas = parser.extractDisciplines(this.studentHistoryJson)
        aluno.setDisciplinas(disciplinas)
    }

}
