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

    def 'extract disciplines informations'(){
        when:
        File file = new ClassPathResource("resources/aluno.json").getFile()
        JSONObject json = jsonSlurper.parseText(FileUtils.readFileToString(file))
        def disciplinesInformations = service.extractDisciplines(json)

        then:
        //TODO TESTAR DIREITO
        assertEquals(7,disciplinesInformations.size)

    }

    def 'extract disciplines results'(){
        when:
        File file = new ClassPathResource("resources/disciplina.json").getFile()
        JSONObject json = jsonSlurper.parseText(FileUtils.readFileToString(file))
        def disciplineResults = service.extractDisciplinesResults(json)

        then:
        //TODO TESTAR DIREITO
        assertEquals(3,disciplineResults.size)

    }


    def setup(){
        service = new SigaParser()
    }
}