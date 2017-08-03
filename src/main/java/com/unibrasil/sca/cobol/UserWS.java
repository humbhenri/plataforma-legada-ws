/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unibrasil.sca.cobol;

import javax.jws.Oneway;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author humbhenri
 */
@WebService(serviceName = "UserWS")
public class UserWS {

    /**
     * Web service operation
     */
    @WebMethod(operationName = "createUser")
    public void createUser(User user) throws Exception {
        UserJpaController jpa = new UserJpaController(EMF.createEntityManager());
        jpa.create(user);
    }

    
}
