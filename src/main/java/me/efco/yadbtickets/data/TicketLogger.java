package me.efco.yadbtickets.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TicketLogger {
    private static final TicketLogger INSTANCE = new TicketLogger();

    private TicketLogger() {}

    public void saveLogLocal(String id, String log) {
        File folder = new File("./yadbtickets/tickets");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        try (FileWriter fileWriter = new FileWriter(new File("./yadbtickets/tickets/" + id + ".txt"))){
            fileWriter.append(log);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static TicketLogger getInstance() {
        return INSTANCE;
    }
}
