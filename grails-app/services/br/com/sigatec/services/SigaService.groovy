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
import sigatec.ClassSubject
import sigatec.Institution
import sigatec.Student
import sigatec.StudentGradeClassSubject


@Component
@Scope("request")
class SigaService {
    static scope = 'request'

    @Autowired
    SigaException sigaException

    @Autowired
    private SigaWebConnector connector

    @Autowired
    private SigaParser parser

    @Autowired
    private SigaCrawler crawler

    @Autowired
    public SigaService() {
    }

    def auth(String login, String password) {
        def response = new JSONObject()

        crawler.login(login, password)
        Aluno aluno = new Aluno()
        aluno.setRg(login)
        crawler.setAluno(aluno)
        response = ["aluno": aluno]
        List<Disciplina> disciplinas = crawler.setDisciplina()
        response += ["disciplinas": disciplinas]


        try {
            Institution institution = Institution.findByName(aluno.faculdade)
            if(!institution){
                institution = new Institution(name: aluno.faculdade).save(failOnError: true)
            }

            Student student = Student.findByName(aluno.nome)
            if(!student){
                new Student(name: aluno.nome, institution: institution).save(failOnError: true)
            }


            for (Disciplina disciplina : disciplinas) {
                ClassSubject classSubject = ClassSubject.findBySubject(disciplina.nome)
                if(!classSubject){
                    classSubject = new ClassSubject(subject: disciplina.nome).save(failOnError: true)
                }
                new StudentGradeClassSubject(student: student, p1: disciplina.notas.get(0).nota.toDouble(), p2: disciplina.notas.get(1).nota.toDouble(), p3: disciplina.notas.get(2).nota.toDouble(), abscencePercentage: disciplina.porcentagemAusencia.toDouble(), classSubject: classSubject).save(failOnError: true)
            }
        } catch (Exception e) {
            //shiu
        }
        return response
    }
}
