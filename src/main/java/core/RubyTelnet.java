package core;

import java.io.IOException;

public class RubyTelnet extends BaseTelnet {

    public RubyTelnet(String host) {
        super(host);
        LOGIN_PROMPT    = "Login: ";
        PASSWORD_PROMPT = "Password: ";
        USER_PROMPT     = "# ";
        ENABLE_PROMPT   = "# ";
    }

    @Override
    public void login(String login, String password, String enablePassword) throws IOException {
        connect();
        doLogin(login, password);
    }
}
