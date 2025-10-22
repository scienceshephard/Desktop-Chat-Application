package backend.service.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class authHandler implements CallbackHandler{

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        for(Callback cb : callbacks){
            if(cb instanceof NameCallback){
                NameCallback nc = (NameCallback) cb;
                System.out.println(nc.getPrompt());
                nc.setName(in.readLine());
            }else if(cb instanceof PasswordCallback){
                PasswordCallback pc = (PasswordCallback) cb;
                System.out.println(pc.getPrompt());
                pc.setPassword(in.readLine().toCharArray());
            }else{
                throw new UnsupportedCallbackException(cb, "Unrecognized Callback");
            }
        }
    }

}
