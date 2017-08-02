/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unibrasil.sca.cobol;

import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author humbhenri
 */
@WebService(serviceName = "ProfessorWS")
public class ProfessorWS {

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getProfessors")
    public List<Professor> getProfessors() {
        ProfessorJpaController jpa = new ProfessorJpaController(EMF.createEntityManager());
        return jpa.findProfessorEntities();
    }
}
