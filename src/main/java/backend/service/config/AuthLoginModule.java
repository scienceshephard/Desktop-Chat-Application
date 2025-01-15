package backend.service.config;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public class AuthLoginModule implements LoginModule{

    public final String username = "user";
    public final String password = "pass";
    private CallbackHandler callbackHandler = null;
    private boolean authSuccessFlag = false;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
            Map<String, ?> options) {
                this.callbackHandler = callbackHandler;
    }

    @Override
    public boolean login() throws LoginException {
        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("User Name");
        callbacks[1] = new PasswordCallback("Password", false);
        try {
            callbackHandler.handle(callbacks);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedCallbackException e) {
            e.printStackTrace();
        }
        String name = ((NameCallback) callbacks[0]).getName();
        String pass= new String(((PasswordCallback) callbacks[1]).getPassword());
        if(username.equals(name) && password.equals(pass)){
            System.out.println("AUthentication successful....");
            authSuccessFlag = true;
        }else{
            authSuccessFlag = false;
            throw new FailedLoginException("authentication failure...");
        }
        return authSuccessFlag;
    }

    @Override
    public boolean commit() throws LoginException {
        return authSuccessFlag;
    }

    @Override
    public boolean abort() throws LoginException {
        
        return false;
    }

    @Override
    public boolean logout() throws LoginException {
        return false;
    }

}
