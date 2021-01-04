package core;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseTelnet {
    protected final Logger LOGGER = Logger.getLogger(BaseTelnet.class.getName());
    protected String LOGIN_PROMPT          = "Username: ";
    protected String PASSWORD_PROMPT       = "Password: ";
    protected String USER_PROMPT           = ">";
    protected String ENABLE_COMMAND        = "enable";
    protected String ENABLE_PROMPT         = "#";
    protected String CONFIG_COMMAND        = "config";
    protected String CONFIG_CONFIRM_PROMPT = "[terminal]?";
    protected String NEXT_PROMPT           = " --More-- ";
    protected String COMMIT_COMMAND        = "write";
    protected String END_COMMAND           = "end";

    protected final org.apache.commons.net.telnet.TelnetClient telnetClient = new org.apache.commons.net.telnet.TelnetClient();
    protected boolean isConfigMode = false;
    protected InputStream in;
    protected PrintStream out;
    protected final String host;

    public BaseTelnet(String host) {
        this.host = host;
        LOGGER.setLevel(Level.SEVERE);
    }

    public void setLogLevel(Level level){
        LOGGER.setLevel(level);
    }

    public void login(String login, String password, String enablePassword) throws IOException {
        connect();
        doLogin(login, password);
        doEnable(enablePassword);
    }

    public void doConfig() throws IOException {
        LOGGER.info("Configure mode");
        write(CONFIG_COMMAND);
        readUntil(CONFIG_CONFIRM_PROMPT);
        write("\n");
        readUntil(ENABLE_PROMPT);
        isConfigMode = true;
        LOGGER.info("Configure mode");
    }

    public void end() throws IOException {
        write(END_COMMAND);
        readUntil(ENABLE_PROMPT);
        isConfigMode = false;
        LOGGER.info("End success");
    }

    public String execute(String command) throws IOException {
        LOGGER.info("Execute: " + command);
        write(command);
        readUntil(command);
        return read();
    }

    public void commit() throws IOException {
        LOGGER.info("Commit");
        if (isConfigMode){
            end();
        }
        write(COMMIT_COMMAND);
        readUntil(ENABLE_PROMPT);
        LOGGER.info("Commit success");
    }

    public void close() throws IOException {
        telnetClient.disconnect();
    }

    protected void connect() throws IOException {
        telnetClient.connect(host, 23);
        in = telnetClient.getInputStream();
        out = new PrintStream(telnetClient.getOutputStream());
    }

    protected void doLogin(String login, String password) throws IOException {
        LOGGER.log(Level.INFO, "Login...");
        readUntil(LOGIN_PROMPT);
        write(login);
        readUntil(PASSWORD_PROMPT);
        write(password);
        readUntil(USER_PROMPT);
        LOGGER.log(Level.INFO, "Login success");
    }

    protected void doEnable(String enablePassword) throws IOException {
        LOGGER.log(Level.INFO, "Enable");
        write(ENABLE_COMMAND);
        readUntil(PASSWORD_PROMPT);
        write(enablePassword);
        readUntil(ENABLE_PROMPT);
        LOGGER.log(Level.INFO, "Enable success");
    }

    protected void write(String value) {
        out.println(value);
        out.flush();
    }

    protected String readUntil(String pattern) throws IOException {
        LOGGER.log(Level.INFO, "Read until: " + pattern);
        char lastChar = pattern.charAt(pattern.length() - 1);
        StringBuilder sb = new StringBuilder();
        char ch = (char) in.read();
        while(true)
        {
//            System.out.print(ch);
            sb.append(ch);
            if (ch == lastChar) {
                if (sb.toString().endsWith(pattern)) {
//                    System.out.print("\n");
                    return sb.toString();
                }
            }
            ch = (char) in.read();
        }
    }

    protected String read() throws IOException {
        LOGGER.info("Read");
        String curPrompt = ENABLE_PROMPT;
        char lastChar = curPrompt.charAt(curPrompt.length() - 1);
        StringBuilder sb = new StringBuilder();
        StringBuilder result = new StringBuilder();

        char ch = (char) in.read();
        while (true) {
//            System.out.print(ch);
            sb.append(ch);
            result.append(ch);
            String currentLine = sb.toString();
            if (currentLine.endsWith(NEXT_PROMPT)) {
                write(" ");
                sb = new StringBuilder();
            } else if (ch == '\n') {
                sb.setLength(sb.length() - 1);
                sb = new StringBuilder();
            }else if (ch == lastChar){
                if (isFinalLineToRead(currentLine, curPrompt)) {
                    int lastIndex = result.indexOf("\n" + currentLine);
                    return result.substring(0, lastIndex);
                }
            }
            ch = (char) in.read();
        }
    }

    protected boolean isFinalLineToRead(String line, String prompt){
        return line.contains(host) && line.endsWith(prompt);
    }

}
