package core;

import java.io.IOException;

public class PlanetTelnet extends BaseTelnet {

    public PlanetTelnet(String host) {
        super(host);

        LOGIN_PROMPT    = "login:";
        PASSWORD_PROMPT = "password:";
        ENABLE_PROMPT   = ">";
        NEXT_PROMPT     = "----------MORE------------";
        END_COMMAND     = "disable";
    }

    @Override
    public String execute(String command) throws IOException {
        LOGGER.info("Execute: " + command);
        write(command);
        return read();
    }

    @Override
    protected boolean isFinalLineToRead(String line, String prompt){
        return line.endsWith(prompt);
    }
}
