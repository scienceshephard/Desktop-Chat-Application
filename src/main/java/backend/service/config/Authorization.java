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
        while (true) {
            try {
                loginContext.login();
            } catch (LoginException e) {
                System.out.println(e.getMessage());
            }
            System.exit(0);
        }
    }

}
