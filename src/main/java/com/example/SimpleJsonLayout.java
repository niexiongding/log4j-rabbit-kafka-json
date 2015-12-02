package com.example;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author Nie Xiongding
 * @since 2015年12月2日 上午10:13:51
 */
public class SimpleJsonLayout extends Layout {

    private final Gson gson = new GsonBuilder().create();
    private final String hostname = getHostname().toLowerCase();
    private final String username = System.getProperty("user.name").toLowerCase();

    private Level minimumLevelForShowLogging = Level.ALL;
    private List<String> mdcFieldsToLog = Collections.emptyList();
    
    public void activateOptions() {

    }

    @Override
    public String format(LoggingEvent event) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("timestamp", event.getTimeStamp());
        map.put("date", new Date(event.getTimeStamp()));
        map.put("hostname", hostname);
        map.put("username", username);
        map.put("level", event.getLevel().toString());
        map.put("thread", event.getThreadName());
        map.put("ndc", event.getNDC());
        if (event.getLevel().isGreaterOrEqual(minimumLevelForShowLogging)) {
            map.put("classname", event.getLocationInformation().getClassName());
            map.put("filename", event.getLocationInformation().getFileName());
            map.put("linenumber", event.getLocationInformation().getLineNumber());
            map.put("methodname", event.getLocationInformation().getMethodName());
        }
        map.put("message", safeToString(event.getMessage()));
        map.put("throwable", formatThrowable(event));

        for (String mdcKey : mdcFieldsToLog) {
            if (!map.containsKey(mdcKey)) {
                map.put(mdcKey, safeToString(event.getMDC(mdcKey)));
            }
        }
        
        after(event, map);
        return gson.toJson(map)+"\n";
    }
    
    /**
     * Method called near the end of formatting a LoggingEvent in case users
     * want to override the default object fields. 
     * @param le the event being logged
     * @param r the map which will be output.
     */
    public void after(LoggingEvent le, Map<String,Object> r) {
    }

    @Override
    public boolean ignoresThrowable() {
        return false;
    }

    private static String getHostname() {
        String hostname = null;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = "Unknown, " + e.getMessage();
        }

        return hostname;
    }

    private static String safeToString(Object obj) {
        if (obj == null)
            return null;
        try {
            return obj.toString();
        } catch (Throwable t) {
            return "Error getting message: " + t.getMessage();
        }
    }

    /**
     * If a throwable is present, format it with newlines between stack trace
     * elements. Otherwise return null.
     */
    private String formatThrowable(LoggingEvent le) {
        if (le.getThrowableInformation() == null || le.getThrowableInformation().getThrowable() == null)
            return null;

        return mkString(le.getThrowableStrRep(), "\n");
    }

    private String mkString(Object[] parts, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0;; i++) {
            sb.append(parts[i]);
            if (i == parts.length - 1)
                return sb.toString();
            sb.append(separator);
        }
    }

    /**
     * @param minimumLevelForShowLogging
     */
    public void setMinimumLevelForShowLogging(String level) {
        this.minimumLevelForShowLogging = Level.toLevel(level, Level.ALL);
    }
    
    /*
     * public String getMinimumLevelForSlowLogging() { return
     * minimumLevelForSlowLogging.toString(); }
     */
    public void setMdcFieldsToLog(String toLog) {
        if (toLog == null || toLog.isEmpty()) {
            mdcFieldsToLog = Collections.emptyList();
        } else {
            ArrayList<String> listToLog = new ArrayList<String>();
            for (String token : toLog.split(",")) {
                token = token.trim();
                if (!token.isEmpty()) {
                    listToLog.add(token);
                }
            }
            mdcFieldsToLog = Collections.unmodifiableList(listToLog);
        }
    }

}
