/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unibrasil.sca.cobol;

import java.util.List;
import javax.jws.WebService;

import com.unibrasil.sca.cobol.exceptions.NonexistentEntityException;

import javax.jws.WebMethod;

/**
 *
 * @author humbhenri
 */
@WebService(serviceName = "ProfessorWS")
public class ProfessorWS {

    @WebMethod(operationName = "getProfessorById")
    public Professor getProfessorById(Integer id) {
    	ProfessorJpaController jpa = new ProfessorJpaController(EMF.createEntityManager());
    	return jpa.findProfessor(id);
    }

    @WebMethod(operationName = "getProfessors")
    public List<Professor> getProfessors() {
        ProfessorJpaController jpa = new ProfessorJpaController(EMF.createEntityManager());
        return jpa.findProfessorEntities();
    }
    
    @WebMethod(operationName = "createProfessor")
    public Professor createProfessor(Professor professor) {
        ProfessorJpaController jpa = new ProfessorJpaController(EMF.createEntityManager());
        jpa.create(professor);
        return professor;
    }
    
    @WebMethod(operationName = "editProfessor")
    public void editProfessor(Professor professor) throws NonexistentEntityException, Exception {
    	ProfessorJpaController jpa = new ProfessorJpaController(EMF.createEntityManager());
    	jpa.edit(professor);
    }
    
    @WebMethod(operationName = "deleteProfessor")
    public void deleteProfessor(Professor professor) throws NonexistentEntityException {
    	ProfessorJpaController jpa = new ProfessorJpaController(EMF.createEntityManager());
    	jpa.destroy(professor.getId());
    }
}

