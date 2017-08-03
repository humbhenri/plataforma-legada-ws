/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unibrasil.sca.cobol;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.jws.WebMethod;

/**
 *
 * @author humbhenri
 */
@WebService(serviceName = "UserWS")
public class UserWS {
    
    private static final Logger LOGGER = Logger.getLogger(UserWS.class.getSimpleName());

    @WebMethod(operationName = "createUser")
    public void createUser(User user) throws Exception {
        LOGGER.log(Level.INFO, "User recebido: {0}", user);
        UserJpaController jpa = new UserJpaController(EMF.createEntityManager());
        jpa.create(user);
    }

    @WebMethod(operationName = "getUser")
    public User getUser(String username) throws Exception {
        UserJpaController jpa = new UserJpaController(EMF.createEntityManager());
        return jpa.findUser(username);
    }

}
