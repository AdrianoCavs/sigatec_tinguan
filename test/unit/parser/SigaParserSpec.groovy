package parser

import br.com.sigatec.business.Aluno
import br.com.sigatec.parser.SigaParser
import groovy.json.JsonSlurper
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.io.support.ClassPathResource
import org.codehaus.groovy.grails.web.json.JSONObject
import spock.lang.Specification

import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertNotNull
import static junit.framework.Assert.assertTrue

/**
 * Created by tinguan on 15/02/16.
 */

class SigaParserSpec extends Specification {

    def service
    def static jsonSlurper = new JsonSlurper()


    def 'extract json with history student informations'(){
        when:
        File file = new ClassPathResource("resources/notas.html").getFile()
        String html = FileUtils.readFileToString(file)
        def json = service.extractJsonStudentHistory(html)

        then:
        assertNotNull(json)
    }

    def 'extract student informations on json and set'(){
        when:
        File file = new ClassPathResource("resources/aluno.json").getFile()
        JSONObject jsonStudent = jsonSlurper.parseText(FileUtils.readFileToString(file))
        Aluno aluno = new Aluno()
        def student = service.extractStudentBasicInformations(aluno, jsonStudent)

        then:
        assertEquals("VINICIUS BARBOSA TINGUAN", student.nome)
        assertEquals("2040481423041", student.ra)
        assertEquals("Tecnologia em Análise e Desenvolvimento de Sistemas", student.curso)
        assertEquals("Faculdade de Tecnologia do Ipiranga", student.faculdade)
    }


    def 'is Invalid Password'(){
        when:
        File file = new ClassPathResource("resources/invalidPassword.html").getFile()
        String html = FileUtils.readFileToString(file)
        def isInvalidPassword = service.isInvalidPassword(html)

        then:
        assertTrue(isInvalidPassword)
    }

    //Quebrado
    def 'extract disciplines informations'(){
        when:
        File file = new ClassPathResource("resources/aluno.json").getFile()
        JSONObject json = jsonSlurper.parseText(FileUtils.readFileToString(file))
        def disciplinesInformations = service.extractDisciplines(json)

        then:
        assertEquals(7,disciplinesInformations.size)
        assertEquals("Laboratório de Banco de Dados", disciplinesInformations.get(0).nome)

    }

    def 'extract disciplines results'(){
        when:
        File file = new ClassPathResource("resources/disciplina.json").getFile()
        JSONObject json = jsonSlurper.parseText(FileUtils.readFileToString(file))
        def disciplineResults = service.extractDisciplinesResults(json)

        then:
        assertEquals(3,disciplineResults.size)
        assertEquals("P1", disciplineResults.get(0).nome)
        assertEquals(null, disciplineResults.get(0).nota)

    }


    def setup(){
        service = new SigaParser()
    }
}