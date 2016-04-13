package br.com.sigatec.services

import br.com.sigatec.business.Aluno
import br.com.sigatec.business.Disciplina

import br.com.sigatec.connector.SigaWebConnector
import br.com.sigatec.crawler.SigaCrawler
import br.com.sigatec.exception.InvalidPasswordException
import br.com.sigatec.exception.SigaException
import br.com.sigatec.parser.SigaParser
import org.codehaus.groovy.grails.web.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component


@Component
@Scope("request")
class SigaService {
    @Autowired
    SigaException sigaException

    @Autowired
    private SigaWebConnector connector

    @Autowired
    private SigaParser parser

    @Autowired
    private SigaCrawler crawler

    @Autowired
    public SigaService (){
    }

    def auth(String login, String password) {
        def response = new JSONObject()

        crawler.login(login, password)
        Aluno aluno = new Aluno()
        aluno.setRg(login)
        crawler.setAluno(aluno)
        response = ["aluno": aluno]
        response += ["disciplinas": crawler.setDisciplina()]

        return response

    }
}
