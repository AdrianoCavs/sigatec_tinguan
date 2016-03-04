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

/**
 * Created by tinguan on 21/02/16.
 */
@Scope("request")
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
        if(parser.isBlockedAccount(response)){
            throw new BlockedAccountException("Sua conta de acesso ao sistema encontra-se com data de expiração vencida. Entrar em contato a Diretoria Acadêmica de sua Unidade.")
        }

        if(!parser.isSuccess(response)){
            throw new InternalErrorException("Erro Interno do Servidor")
        }
    }

    def setAluno(Aluno aluno){
        def gradesPage = connector.get("https://www.sigacentropaulasouza.com.br/aluno/notasparciais.aspx")
        this.studentHistoryJson = parser.extractJsonStudentHistory(gradesPage)
        parser.extractStudentBasicInformations(aluno, this.studentHistoryJson)


    }

    def setDisciplina(){
        def abscencePage = connector.get("https://www.sigacentropaulasouza.com.br/aluno/faltasparciais.aspx")
        def gradesClasses = connector.get("https://www.sigacentropaulasouza.com.br/aluno/historicograde.aspx")
        def studentAbsencesJson = parser.extractJsonStudentHistory(abscencePage)
        List<Disciplina> disciplina = parser.extractDisciplines(this.studentHistoryJson, studentAbsencesJson, gradesClasses)
        return disciplina
    }

}
