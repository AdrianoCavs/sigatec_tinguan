package br.com.sigatec.parser

import br.com.sigatec.business.Aluno
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
            def jsonSlurper = new JsonSlurper()
            return jsonSlurper.parseText(element.attributes()["value"].toString())

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


}
