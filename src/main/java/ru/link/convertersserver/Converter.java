package ru.link.convertersserver;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;


public class Converter {
    private final String[] CMD_LIST = {"TEMP 1?", "SYSTEM:ERROR?", "FREQ:REFSOURCE?"};
    private final String NO_CONNECTION_MSG = "No connection";
    
    private final int converterId;

    private Socket socket;

    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    private final Timer timer;
    private final TimerTask timeoutTask;

    private final long delay = 2000L;

    public Converter(int converterId) {
        this.converterId = converterId;

        this.timer = new Timer("Reading timeout");
        this.timeoutTask = new TimerTask() {
            @Override
            public void run() {
                log("Reading timeout");

                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    /*
     * @param address IP-адресс конвертера
     * @return В случае успешного подключения, возвращает ответ на команду INIT?.
     *         В противном случае, возвращает "No connection"
     */
    public String connect(String address) {
        String answer;

        try {
            this.socket = new Socket(address, 5025);
            this.printWriter = new PrintWriter(socket.getOutputStream(), true);
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            log("Connection error");
            
            return NO_CONNECTION_MSG;
        }

        log("Connected");

        write("INIT?");
        answer = read();

        return answer;
    }

    /**
     * @return Возвращает true в случае успешного закрытия соединения.
     *         В противном случае - false.
     */
    public boolean disconnect() {
        try {
            this.bufferedReader.close();
            this.printWriter.close();
            this.socket.close();
        } catch (IOException e) {
            log("Error while closing connection");
            return false;
        }

        return true;
    }

    /**
     * @param cmd Команда, отправляемая в конвертер
     * @return Возвращает ответ на команду. Если соединение с конвертером было
     *         потеряно, возвращает "No connection".
     */
    public String sendCommand(String cmd) {
        StringBuilder answer;

        log("CMD: " + cmd);

        if (!cmd.contains(";")) {
            write(cmd);
            answer = new StringBuilder(read());
        } else {
            String[] cache = cmd.split(";");
            
            if (cache[0].equals("console")) {
                write(cache[1]);
                sleep(50);
                
                if (!cmd.endsWith("?")) {
                    write(cmdToQuery(cache[1]));
                }
                
                answer = new StringBuilder(read());

                while (true) {
                    try {
                        if (!bufferedReader.ready()) break;
                        answer.append("@").append(read());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                String parsedCmd = CommandParser.parse(cmd);
                
                write(parsedCmd);
                sleep(50);
                
                write(cmdToQuery(parsedCmd));
                answer = new StringBuilder(read());                
            }
        }

        return cmd + "@" + answer;
    }

    /**
     * @return Возвращает значение температуры. Если соединение с конвертером было
     *         потеряно, возвращает "No connection".
     */
    public String getTemp() {
        StringBuilder answer = new StringBuilder();
        String cache;

        for (int i = 0; i < CMD_LIST.length; ++i) {
            write(CMD_LIST[i]);
            cache = read();

            if (cache == null || cache.equals("null") || cache.equals("")) {
                return NO_CONNECTION_MSG;
            }
            answer.append(cache).append(i == CMD_LIST.length - 1 ? "" : "@");

            switch (i) {
                case 0 -> log("Temperature received");
                case 1 -> log("Error received");
                case 2 -> log("Ref source received");
            }
        }

        return answer.toString();
    }


    /**
     * Отправляет команду в конвертер
     *
     * @param cmd Команда, отправляемая в конвертер
     */
    private void write(String cmd) {
        printWriter.println(cmd);
        
        log("WRITE: " + cmd);
    }

    /**
     * Ожидает строки от конвертера 2 секунды. Если строка не была получена,
     * то закрывает сокет, тем самым вызывая исключение при чтении буфера.
     *
     * @return Возвращает ответ от конвертера. В случае потери соединения,
     *         возвращает "".
     */
    private String read() {
        String answer = "";

        try {
            timer.schedule(timeoutTask, delay);
            answer = bufferedReader.readLine();
            timer.cancel();
        } catch (IOException e) {
            return "";
        }
        
        log("READ: " + answer);
        return answer;
    }
    
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    private String cmdToQuery(String cmd) {
        return cmd.split(" ")[0] + "?";
    }
    
    private void log(String message) {
        System.out.println(String.format("[%d] ", converterId) + message);
    }
}
