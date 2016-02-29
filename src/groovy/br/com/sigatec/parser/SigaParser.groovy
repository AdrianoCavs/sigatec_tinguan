package br.com.sigatec.parser

import br.com.sigatec.business.Aluno
import br.com.sigatec.business.Disciplina
import br.com.sigatec.business.Nota
import groovy.json.JsonSlurper
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.springframework.context.annotation.Scope

/**
 * Created by tinguan on 08/02/16.

 */
@Scope("singleton")
class SigaParser {

    def parseMapLogin(login,password){
        def mapa = ["vSIS_USUARIOID": login]
        mapa = mapa + ["vSIS_USUARIOSENHA": password]
        mapa = mapa + ["BTCONFIRMA":"Confirmar"]
        mapa = mapa + ["GXState":"{\"_EventName\":\"EENTER.\",\"_EventGridId\":\"\",\"_EventRowId\":\"\",\"MPW0005_CMPPGM\":\"login_top.aspx\",\"MPW0005GX_FocusControl\":\"\",\"vREC_SIS_USUARIOID\":\"\",\"GX_FocusControl\":\"vSIS_USUARIOID\",\"GX_AJAX_KEY\":\"B18D3F01D882EBFB9CEB8BF6F8DD4186\",\"AJAX_SECURITY_TOKEN\":\"2F112E79D6E40FAF0878E1A4F5581A2C0AC07220B80EF97E624D3458E9CAB367\",\"GX_CMP_OBJS\":{\"MPW0005\":\"login_top\"},\"sCallerURL\":\"\",\"GX_RES_PROVIDER\":\"GXResourceProvider.aspx\",\"GX_THEME\":\"GeneXusX\",\"_MODE\":\"\",\"Mode\":\"\",\"IsModified\":\"1\"}"]
        return mapa
    }


    def extractJsonStudentHistory(html){
        Document document = Jsoup.parse(html)

        try{
            Element element = document.select("div input").last()
            String json = element.attributes()["value"]
            json = json.replace("\\>", "\\\\>")
            def jsonSlurper = new JsonSlurper()
            return jsonSlurper.parseText(json)

        } catch (Exception e){
            throw new Exception(e)
        }
    }

    def extractStudentBasicInformations(Aluno aluno, json){
        aluno.setNome(json.MPW0039vPRO_PESSOALNOME)
        aluno.setRa(json.MPW0039vACD_ALUNOCURSOREGISTROACADEMICOCURSO)
        aluno.setCurso(json.vACD_CURSONOME_MPAGE)
        aluno.setFaculdade(json.vUNI_UNIDADENOME_MPAGE)
        return aluno
    }

    def extractDisciplines(json, studentAbsencesJson, gradesClassesHtml){
        List<Disciplina> disciplinas = new ArrayList<Disciplina>()
        json.Acd_alunonotasparciais_sdt.each{ j ->
            Disciplina disciplina = new Disciplina()
            disciplina.setNome(j.ACD_DisciplinaNome.toString())
            disciplina.setFaltas(j.ACD_AlunoHistoricoItemQtdFaltas.toString())
            disciplina.setMedia(j.ACD_AlunoHistoricoItemMediaFinal.toString())
            disciplina.setNotas(this.extractDisciplinesResults(j))
            def absences = extractAbsences(studentAbsencesJson, disciplina, gradesClassesHtml)
            disciplina.setPorcentagemAusencia(absences)
            disciplinas.add(disciplina)
        }
        return disciplinas
    }

    String extractAbsences(studentAbsencesJson, disciplina, gradesClassesHtml){
        def calculatedValue
        studentAbsencesJson.vFALTAS.each{ faltas ->
            def disciplineName = faltas.ACD_DisciplinaNome.trim()
            if (disciplineName.equals(disciplina.getNome())){
                def abscenceNumber = faltas.TotalAusencias
                def numberOfClasses = this.extractNumberOfClasses(gradesClassesHtml, disciplina)
                calculatedValue = String.valueOf(calculateAbscence(abscenceNumber, numberOfClasses))
            }
        }
        return calculatedValue

    }

    def extractNumberOfClasses(gradesClassesHtml, disciplina){
        Document document = Jsoup.parse(gradesClassesHtml)
        try{
            Elements elements = document.select("table#TABLE100_MPAGE tbody tr td table tbody tr td div table tbody tr td div table tbody")
            for(Element element : elements){

                if(element.text().contains("AS:")){
                    def className = element.select("tr td p").get(2).text()
                    if(className.trim().equals(disciplina.getNome())){
                        String numberOfClasses = element.select("td p").get(1).text().replace("AS:","")
                        return numberOfClasses.trim()
                    }
                }

            }
        } catch(Exception e){
            throw e
        }
    }

    Double calculateAbscence(abscenceNumber, numberOfClasses){
        //int abscenceValue = Integer.parseInt(abscenceNumber)
        double value
        if(numberOfClasses.equals("4")){
            value = (100 * abscenceNumber) / 80
            return value
        }

        value = (100 * abscenceNumber) / 40
        return value

    }

    def extractDisciplinesResults(json){
        List<Nota> notas = new ArrayList<Nota>()
        json.Avaliacoes.each{j ->
            Nota nota = new Nota()
            nota.setNome(j.ACD_PlanoEnsinoAvaliacaoTitulo.toString())
            //TODO VER PADRÃO DO ARRAY DE NOTAS QUANDO ALGUM PROFESSOR LANCAR ALGUMA
            if(!j.Notas.toString().equals("[]")){
                nota.setNota(j.Notas.toString())
            } else {
                nota.setNota(null)
            }
            notas.add(nota)
        }
        return notas
    }

    def isInvalidPassword(String html){
        return html.contains("confere Login e Senha")
    }


}
