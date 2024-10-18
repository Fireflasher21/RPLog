package fireflasher.rplog.logging;

import fireflasher.rplog.RPLog;
import net.minecraft.network.chat.Component;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static fireflasher.rplog.RPLog.LOGGER;
import static fireflasher.rplog.ChatLogManager.*;


public class LoggerRunner implements Runnable {

    private File logFile;
    private boolean error = false;
    private String lastdequeueMessage = "";

    private static final DateTimeFormatter TIME  = DateTimeFormatter.ofPattern("HH:mm:ss");

    private void setup(){
        LocalDateTime date = LocalDateTime.now();
        setUp_logFile(date.toLocalDate());
    }

    private void setUp_logFile(LocalDate date){
        //create logfile with selected date
        //if error happened, write in errorFile
        if(error)logFile = new File(RPLog.getFolder() + "/errorLogs/", date.format(DATE) + "-error.txt");
        else logFile = new File(RPLog.getFolder() + serverName + "/" + date.format(DATE) + ".txt");
    }

    @Override
    public void run() {
        //bring everything intoOrder
        setup();

        //while my File exists, run code
        while(true) {
            LocalDate date = LocalDate.now();

            String messageToPrint;
            try {
                //check on day switch: if yes, change logFile
                if(!logFile.getName().contains(date.format(DATE))){
                    setUp_logFile(date);
                    //write first message into file
                    Component firstLogMessage = RPLog.translateAbleStrings.get("rplog.logger.loggerrunner.first_log_message");
                    messageToPrint = "[RPLog] " +  firstLogMessage + date.format(DATE);
                }
                //else getQueuedMessage
                else messageToPrint = getMessageString();

                if(messageToPrint.isEmpty())continue;
                //try to print message
                printMessage(messageToPrint);

            } catch (IOException e) {
                Component logger_writewarning = RPLog.translateAbleStrings.get("rplog.logger.loggerrunner.write_warning");
                LOGGER.error("{}{}", logger_writewarning, logFile.toString(),"");
                //When an exception was thrown, try one time, then switch to errorlogFile
                handleErrorCase();
            } catch (InterruptedException e) {
                Component messageQueueError = RPLog.translateAbleStrings.get("rplog.logger.loggerrunner.message_queue_dequeue_warning");
                LOGGER.error(messageQueueError);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                return;
            }
        }
    }

    private String getMessageString() throws InterruptedException {
        if(messageQueue.size() <= 1) {
            //take the oldest message from queue
            //if queue is empty, thread is paused
            String dequeueMessage = messageQueue.take();
            //if dequeue is empty, skip
            //if dequeue is the same as the message before, skip
            if (dequeueMessage.isEmpty() || dequeueMessage.equals(lastdequeueMessage)) return "";
            //set on new last dequeueMessage
            lastdequeueMessage = dequeueMessage;
            String time = "[" + LocalDateTime.now().format(TIME) + "] ";
            //merge in messageString with newline
            return time + dequeueMessage + "\n";
        }
        StringBuilder returnMessage = new StringBuilder();

        //if there are already more than one message in it
        //iterate over all messages
        while(true){
            //when messageQueue is Empty, return string
            if(messageQueue.isEmpty())return returnMessage.toString();
            //take the oldest message from queue
            String dequeueMessage = messageQueue.takeLast();
            //if dequeue is empty, skip
            //if dequeue is the same as the message before, skip
            if(dequeueMessage.isEmpty() || dequeueMessage.equals(lastdequeueMessage))continue;
            String time = "[" + LocalDateTime.now().format(TIME) + "] ";
            //append to StringBuilder
            returnMessage.append(time).append(dequeueMessage).append("\n");
            lastdequeueMessage = dequeueMessage;
        }
    }

    private void printMessage(String messageToPrint) throws IOException {
        //creates file and parentfolders if not existing
        FileOutputStream fileOutputStream = FileUtils.openOutputStream(logFile);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        BufferedWriter bw = new BufferedWriter(outputStreamWriter);

        bw.append(messageToPrint);
        bw.close();
    }

    private void handleErrorCase() {
        //Move through every step
        try{
            File serverFolder = logFile.getParentFile();
            if(!serverFolder.exists())serverFolder.mkdir();
            if(logFile.exists()){
                if(!logFile.canWrite())logFile.renameTo(new File(logFile.getParent()+logFile.getName()+".bak"));
                setUp_logFile(LocalDate.now());
                logFile.delete();
            }
            logFile.createNewFile();
            printMessage(lastdequeueMessage);
            Component logger_error_success = RPLog.translateAbleStrings.get("rplog.logger.loggerrunner.error_success");
            LOGGER.info("{}{}", logger_error_success, logFile.toString(),"");
        } catch (IOException ex) {
            error = true;
            setUp_logFile(LocalDate.now());
            Component logger_error_file = RPLog.translateAbleStrings.get("rplog.logger.loggerrunner.error_file");
            LOGGER.error("{}{}", logger_error_file, logFile.toString(),"");
        }
    }

    public void onServerInteraction(){
        error = false;
        setUp_logFile(LocalDate.now());
    }


}
