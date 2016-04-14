package br.com.sigatec.parser

import br.com.sigatec.business.Aluno
import br.com.sigatec.business.Disciplina
import br.com.sigatec.business.Nota
import br.com.sigatec.exception.InternalErrorException
import groovy.json.JsonSlurper
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
 * Created by tinguan on 08/02/16.

 */
@Component
@Scope("request")
class SigaParser {

    def parseMapLogin(login,password, html){
        Document document = Jsoup.parse(html)
        String element = document.select("div input[value]").attr("value")
        def json = new JsonSlurper().parseText(element)

        def mapa = ["vSIS_USUARIOID": login]
        mapa = mapa + ["vSIS_USUARIOSENHA": password]
        mapa = mapa + ["BTCONFIRMA":"Confirmar"]
        mapa = mapa + ["GXState":"{\"_EventName\":\"EENTER.\",\"_EventGridId\":\"\",\"_EventRowId\":\"\",\"MPW0005_CMPPGM\":\"login_top.aspx\",\"MPW0005GX_FocusControl\":\"\",\"vREC_SIS_USUARIOID\":\"\",\"GX_FocusControl\":\"vSIS_USUARIOID\",\"GX_AJAX_KEY\": \"" + json.GX_AJAX_KEY + "\",\"AJAX_SECURITY_TOKEN\":\""+ json.AJAX_SECURITY_TOKEN +"\",\"GX_CMP_OBJS\":{\"MPW0005\":\"login_top\"},\"sCallerURL\":\"\",\"GX_RES_PROVIDER\":\"GXResourceProvider.aspx\",\"GX_THEME\":\"GeneXusX\",\"_MODE\":\"\",\"Mode\":\"\",\"IsModified\":\"1\"}"]
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
            throw new InternalErrorException("Erro Interno do Servidor")
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
            throw new InternalErrorException("Erro Interno do Servidor")
        }
    }

    Double calculateAbscence(abscenceNumber, numberOfClasses){
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
            //TODO VER POR QUE ATRIBUI [] no valor da nota
            if(!j.Notas.toString().equals("[]")){
                nota.setNota(j.Notas.ACD_PlanoEnsinoAvaliacaoParcialNota.toString().replace("[","").replace("]",""))
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

    def isBlockedAccountWithAttempts(String html){
        return html.contains("Sua conta de acesso ao sistema encontra-se bloqueada por tentativas de acesso")
    }

    def isBlockedAccountWithExpiredDate(String html){
        return html.contains("vencida. Entrar em contato")
    }

    def isSuccess(String html){
        return html.contains("vACD_CURSONOME_MPAGE")
    }


}
