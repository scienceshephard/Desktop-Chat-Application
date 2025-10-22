package backend.service.config;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Authorization {

    public void Start(){
        LoginContext loginContext = null;
        System.setProperty("java.security.auth.login.config", "authjaas.config");
        try {
            loginContext = new LoginContext("AuthLogin", new authHandler());
        } catch (LoginException e) {
            System.out.println(e.getMessage());
        }
        try{
            loginContext.login();
            System.out.println("Authentication succeeded!");
        }catch (LoginException e){
            System.out.println("Authentication failed: " + e.getMessage());
        }
    }

}
