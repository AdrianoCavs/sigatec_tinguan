package br.com.sigatec.crawler

import br.com.sigatec.business.Aluno
import br.com.sigatec.business.Disciplina
import br.com.sigatec.connector.SigaWebConnector
import br.com.sigatec.exception.BlockedAccountException
import br.com.sigatec.exception.InternalErrorException
import br.com.sigatec.exception.InvalidPasswordException
import br.com.sigatec.parser.SigaParser
import org.codehaus.groovy.grails.web.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
 * Created by tinguan on 21/02/16.
 */
@Component
@Scope("request")
class SigaCrawler {
    public JSONObject studentHistoryJson

    @Autowired
    private SigaWebConnector connector


    @Autowired
    private SigaParser parser

    public Map<String, String> cookies

    public SigaCrawler() {
        this.cookies = new HashMap<String, String>()
    }

    def login(String login, String password){
        if(!login || !password){
            throw new InvalidPasswordException("Não Autorizado")
        }
        def html = connector.get("https://www.sigacentropaulasouza.com.br/aluno/login.aspx", cookies)
        def mapLogin = parser.parseMapLogin(login, password, html)
        def response = connector.post("https://www.sigacentropaulasouza.com.br/aluno/login.aspx",mapLogin, cookies)
        if(parser.isInvalidPassword(response)){
            throw new InvalidPasswordException("Não Autorizado")
        }
        if(parser.isBlockedAccountWithExpiredDate(response)){
            throw new BlockedAccountException("Sua conta de acesso ao sistema encontra-se com data de expiração vencida. Entrar em contato a Diretoria Acadêmica de sua Unidade.")
        }

        if(parser.isBlockedAccountWithAttempts(response)){
            throw new BlockedAccountException("Sua conta de acesso ao sistema encontra-se bloqueada por tentativas de acesso. Entrar em contato a Diretoria Acadêmica de sua Unidade.")
        }

        if(!parser.isSuccess(response)){
            throw new InternalErrorException("Erro Interno do Servidor")
        }
    }

    def setAluno(Aluno aluno){
        def gradesPage = connector.get("https://www.sigacentropaulasouza.com.br/aluno/notasparciais.aspx", cookies)
        this.studentHistoryJson = parser.extractJsonStudentHistory(gradesPage)
        parser.extractStudentBasicInformations(aluno, this.studentHistoryJson)


    }

    def setDisciplina(){
        def abscencePage = connector.get("https://www.sigacentropaulasouza.com.br/aluno/faltasparciais.aspx", cookies)
        def gradesClasses = connector.get("https://www.sigacentropaulasouza.com.br/aluno/historicograde.aspx", cookies)
        def studentAbsencesJson = parser.extractJsonStudentHistory(abscencePage)
        List<Disciplina> disciplina = parser.extractDisciplines(this.studentHistoryJson, studentAbsencesJson, gradesClasses)
        return disciplina
    }

}
