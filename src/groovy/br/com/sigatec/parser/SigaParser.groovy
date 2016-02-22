package br.com.sigatec.parser

import br.com.sigatec.business.Aluno
import br.com.sigatec.business.Disciplina
import br.com.sigatec.business.Nota
import groovy.json.JsonSlurper
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
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

    def extractDisciplines(json){
        List<Disciplina> disciplinas = new ArrayList<Disciplina>()
        json.Acd_alunonotasparciais_sdt.each{ j ->
            Disciplina disciplina = new Disciplina()
            disciplina.setNome(j.ACD_DisciplinaNome.toString())
            disciplina.setFaltas(j.ACD_AlunoHistoricoItemQtdFaltas.toString())
            disciplina.setMedia(j.ACD_AlunoHistoricoItemMediaFinal.toString())
            disciplina.setNotas(this.extractDisciplinesResults(j))
            disciplinas.add(disciplina)
        }
        return disciplinas
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
