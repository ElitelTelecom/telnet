package core;

import java.io.IOException;

public class AlliedTelesisTelnet extends BaseTelnet {

    protected String COMMIT_CONFIRM = "Overwrite file [startup-config] ?[Yes/press any key for no]....";

    public AlliedTelesisTelnet(String host) {
        super(host);

        LOGIN_PROMPT          = "User Name:";
        PASSWORD_PROMPT       = "Password:";
        ENABLE_PROMPT         = "#";
        NEXT_PROMPT           = "More: <space>,  Quit: q, One line: <return> ";
        COMMIT_COMMAND        = "copy running-config startup-config";
    }

    @Override
    public void doConfig() throws IOException {
        LOGGER.info("Configure mode");
        write(CONFIG_COMMAND);
        readUntil(ENABLE_PROMPT);
        isConfigMode = true;
        LOGGER.info("Configure mode");
    }

    @Override
    public void commit() throws IOException {
        LOGGER.info("Commit");
        if (isConfigMode){
            end();
        }
        write(COMMIT_COMMAND);
        readUntil(COMMIT_CONFIRM);
        write("\n");
        readUntil(ENABLE_PROMPT);
        LOGGER.info("Commit success");
    }
}
