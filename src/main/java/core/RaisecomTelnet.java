package core;

public class RaisecomTelnet extends BaseTelnet {

    public RaisecomTelnet(String host) {
        super(host);

        LOGIN_PROMPT          = "Login:";
        PASSWORD_PROMPT       = "Password:";
        CONFIG_CONFIRM_PROMPT = "...";
    }
}
