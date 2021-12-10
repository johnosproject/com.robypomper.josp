package com.robypomper.java;

import com.robypomper.josp.consts.JOSPConstants;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 * Utils class that provide default Java Utils Logging instances like
 * {@link Formatter}s and {@link Handler}s.
 *
 * Moreover, this class provide also static methods to initialize standard
 * logging environment (development and release) and manage {@link Logger}s.
 */
@SuppressWarnings("unused")
public class JavaLogging {

    // Default formatters

    /**
     * A custom SimpleFormatter implementation with different params for the
     * format string (date,level,source,message,throwable).
     *
     * <ul>
     *     <li>date: message's date time formatted via {@link #dateFormatter}</li>
     *     <li>level: default {@link Level#toString()}</li>
     *     <li>source: if fields are present 'CLASS::METHOD()', otherwise 'CLASS' or even 'LOGGER'</li>
     *     <li>message: the log's messages</li>
     *     <li>throwable: the throwable instance toString() method, if any</li>
     * </ul>
     */
    public static class CustomFormatter extends SimpleFormatter {

        public static final String FORMAT_JAVA_DEFAULT = "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %5$-7s [%4$s] %6$s %7$s%n";
        public static final String FORMAT_BASE = "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %5$-7s [%4$s] %6$s %7$s%n";
        public static final String FORMAT_DEV = "%1$s %2$-7s [%3$-60s] %4$s %5$s%n";            // 09/12/21 17:50:25.160 INFO    [com.robypomper.josp.jod.JOD_002::instance()                 ] Init JOD instance id '3938'
        public static final String FORMAT_REL = "%1$s %2$-7s [%3$-40s] %4$s %5$s%n";            // 09/12/21 17:48:41.102 INFO    [c.r.josp.jod.JOD_002::instance()        ] Init JOD instance id '3924'
        public static final String FORMAT_FILE = "[%3$-80s @ %1$s | %2$-7s] %4$s %5$s%n";       // [ com.robypomper.josp.jod.JOD_002::instance()                                     @ 09/12/21 17:45:25.691 | INFO   ] Init JOD instance id '1326'
        public static final String FORMAT_DEFAULT = FORMAT_REL;
        public static final SimpleDateFormat DATE_FORMATTER_DEFAULT = JavaDate.getDateTimeSerializationFormatter();
        public static final int SOURCE_LENGTH_DEV = 60;
        public static final int SOURCE_LENGTH_REL = 40;
        public static final int SOURCE_LENGTH_FILE = 80;
        public static final int SOURCE_LENGTH_DEFAULT = SOURCE_LENGTH_REL;
        public static final String ANSI_RESET = "\u001B[0m";
        public static final String ANSI_BLACK = "\u001B[30m";
        public static final String ANSI_RED = "\u001B[31m";
        public static final String ANSI_GREEN = "\u001B[32m";
        public static final String ANSI_YELLOW = "\u001B[33m";
        public static final String ANSI_BLUE = "\u001B[34m";
        public static final String ANSI_PURPLE = "\u001B[35m";
        public static final String ANSI_CYAN = "\u001B[36m";
        public static final String ANSI_WHITE = "\u001B[37m";
        public static final boolean COLORED_DEFAULT = true;

        private final String format;
        private final SimpleDateFormat dateFormatter;
        private final int sourceLength;                     // for better results, set it according to format's 3rd param padding ('%3$-40s')
        private final boolean colored;

        public CustomFormatter() {
            this(FORMAT_DEFAULT);
        }

        public CustomFormatter(String format) {
            this(format, DATE_FORMATTER_DEFAULT, SOURCE_LENGTH_DEFAULT, COLORED_DEFAULT);
        }

        public CustomFormatter(String format, SimpleDateFormat dateFormatter, int sourceLength, boolean colored) {
            this.format = format;
            this.dateFormatter = dateFormatter;
            this.sourceLength = sourceLength;
            this.colored = colored;
        }

        @Override
        public synchronized String format(LogRecord record) {
            String date = dateFormatter.format(new Date(record.getMillis()));
            String level = record.getLevel().toString();
            String source = record.getSourceClassName() == null
                    ? record.getLoggerName()
                    : record.getSourceMethodName() == null
                    ? record.getSourceClassName()
                    : record.getSourceClassName() + "::" + record.getSourceMethodName() + "()";
            source = reduceSource(source, sourceLength);
            String message = formatMessage(record);
            String throwable = "";
            if (record.getThrown() != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                pw.println();
                record.getThrown().printStackTrace(pw);
                pw.close();
                throwable = sw.toString();
            }
            String output = String.format(format,
                    date,
                    level,
                    source,
                    message,
                    throwable);

            if (colored)
                return getColorByLevel(record.getLevel()) + output + ANSI_RESET;

            return output;
        }

        private String getColorByLevel(Level level) {
            if (level == Level.SEVERE) return ANSI_RED;
            if (level == Level.WARNING) return ANSI_PURPLE;
            if (level == Level.INFO) return ANSI_BLACK;
            if (level == Level.CONFIG) return ANSI_GREEN;
            if (level == Level.FINE) return ANSI_CYAN;
            if (level == Level.FINER) return ANSI_CYAN;
            if (level == Level.FINEST) return ANSI_CYAN;
            return ANSI_BLACK;
        }

        private static String reduceSource(String source, int maxLength) {
            if (source.length() <= maxLength)
                return source;

            String[] subs = source.split("\\.");
            int count = 0;
            while (source.length() > maxLength && count < subs.length - 1) {
                if (!subs[count].isEmpty())
                    source = source.replace(subs[count], subs[count].substring(0, 1));
                count++;
            }

            return source.substring(Math.max(0, source.length() - maxLength));
        }

    }

    /**
     * Default Formatter.
     *
     * @return a new CustomFormatter instance.
     */
    public static Formatter newFormatterDefault() {
        return newFormatterRelease();
    }

    /**
     * Formatter for release purposes (ASCII colors).
     *
     * @return a new CustomFormatter instance.
     */
    public static Formatter newFormatterRelease() {
        return new CustomFormatter(CustomFormatter.FORMAT_REL, JavaDate.getDateTimeFormatter(), CustomFormatter.SOURCE_LENGTH_REL, CustomFormatter.COLORED_DEFAULT);
    }

    /**
     * Formatter for release purposes (black and white).
     *
     * @return a new CustomFormatter instance.
     */
    public static Formatter newFormatterReleaseNoColor() {
        return new CustomFormatter(CustomFormatter.FORMAT_REL, JavaDate.getDateTimeFormatter(), CustomFormatter.SOURCE_LENGTH_REL, false);
    }

    /**
     * Formatter for release purposes.
     *
     * @return a new CustomFormatter instance.
     */
    public static Formatter newFormatterReleaseFile() {
        return new CustomFormatter(CustomFormatter.FORMAT_FILE, JavaDate.getDateTimeFormatter(), CustomFormatter.SOURCE_LENGTH_FILE, false);
    }

    /**
     * Formatter for development purposes (ASCII colors).
     *
     * @return a new CustomFormatter instance.
     */
    public static Formatter newFormatterDevelopment() {
        return new CustomFormatter(CustomFormatter.FORMAT_DEV, JavaDate.getDateTimeFormatter(), CustomFormatter.SOURCE_LENGTH_DEV, CustomFormatter.COLORED_DEFAULT);
    }

    /**
     * Formatter for development purposes (black and white).
     *
     * @return a new CustomFormatter instance.
     */
    public static Formatter newFormatterDevelopmentNoColor() {
        return new CustomFormatter(CustomFormatter.FORMAT_DEV, JavaDate.getDateTimeFormatter(), CustomFormatter.SOURCE_LENGTH_DEV, false);
    }


    // Default handlers

    /**
     * A custom Handler to print on specified {@link OutputStream}, by default
     * (<code>System.out</code>).
     * <p>
     * NB: it implements also a <code>buffered</code> flag, but it was not
     * tested. Once it was checked, you must update the {@link JavaConsoleHandler}
     * as default Handler generator (replacing actual {@link #newHandlerConsoleRelease(boolean)}
     * method).
     */
    public static class BufferedStreamHandler extends StreamHandler {

        public static OutputStream STREAM_OUT = System.out;
        public static OutputStream STREAM_ERR = System.out;
        public static OutputStream STREAM_DEFAULT = STREAM_OUT;
        public static boolean BUFFERING_DEFAULT = true;
        public static Formatter FORMATTER_DEFAULT = newFormatterDefault();
        boolean buffering;

        public BufferedStreamHandler() {
            this(STREAM_DEFAULT);
        }

        public BufferedStreamHandler(OutputStream stream) {
            this(stream, BUFFERING_DEFAULT);
        }

        public BufferedStreamHandler(OutputStream stream, boolean buffering) {
            this(stream, buffering, FORMATTER_DEFAULT);
        }

        public BufferedStreamHandler(OutputStream stream, boolean buffering, Formatter formatter) {
            super(stream, formatter);
            this.buffering = buffering;
            // by default this handler, prints all levels
            setLevel(Level.ALL);
        }

        @Override
        public void publish(LogRecord record) {
            super.publish(record);
            if (!buffering)
                super.flush();
        }

    }

    /**
     * @return fallback handler (default formatter), used by JavaLogging and other classes as safety handler.
     */
    public static Handler newHandlerFallback() {
        return newHandlerConsoleOut(newFormatterDefault());
    }

    /**
     * @return default console handler (default formatter), used to print logs on <code>System.out</code>.
     */
    public static Handler newHandlerConsoleOut() {
        return newHandlerConsoleOut(newFormatterDefault());
    }

    /**
     * @param formatter the formatter used to initialize the new handler.
     * @return default console handler, used to print logs on <code>System.out</code>.
     */
    public static Handler newHandlerConsoleOut(Formatter formatter) {
        return new BufferedStreamHandler(BufferedStreamHandler.STREAM_OUT, false, formatter);
    }

    /**
     * @return default ERROR console handler (default formatter), used to print logs on <code>System.err</code>.
     */
    public static Handler newHandlerConsoleErr() {
        return newHandlerConsoleErr(newFormatterDefault());
    }

    /**
     * @param formatter the formatter used to initialize the new handler.
     * @return default ERROR console handler, used to print logs on <code>System.err</code>.
     */
    public static Handler newHandlerConsoleErr(Formatter formatter) {
        return new BufferedStreamHandler(BufferedStreamHandler.STREAM_ERR, false, formatter);
    }

    /**
     * A custom Handler to print on {@link System#out} (INFO and lesser) and on
     * {@link System#err} (WARNING and higher).
     */
    public static class JavaConsoleHandler extends StreamHandler {

        public static Handler STREAM_OUT_DEFAULT = newHandlerConsoleOut();
        public static Handler STREAM_ERR_DEFAULT = newHandlerConsoleErr();

        private final Handler outHandler;
        private final Handler errHandler;

        public JavaConsoleHandler() {
            this(STREAM_OUT_DEFAULT, STREAM_ERR_DEFAULT);
        }

        public JavaConsoleHandler(Handler ourHandler, Handler errHandler) {
            super();
            this.outHandler = ourHandler;
            this.errHandler = errHandler;
            // by default this handler, prints all levels
            setLevel(Level.ALL);
        }

        @Override
        public void publish(LogRecord record) {
            if (record.getLevel().intValue() <= Level.INFO.intValue()) {
                outHandler.publish(record);
                outHandler.flush();
            } else {
                errHandler.publish(record);
                errHandler.flush();
            }
        }
    }

    /**
     * @return default console handler (development formatter), used to print logs on <code>System.out</code>.
     */
    public static Handler newHandlerConsoleDeveloper(boolean isColored) {
        Formatter formatter = isColored ? newFormatterDevelopment() : newFormatterDevelopmentNoColor();
        return newHandlerConsoleOut(formatter);

        // Code for JavaConsoleHandler
        /*
        Formatter formatter = newFormatterDevelopment();
        Handler out = newHandlerConsoleOut(formatter);
        Handler err = newHandlerConsoleErr(formatter);
        return new JavaConsoleHandler(out, err);
        */
    }

    /**
     * @return default console handler (release formatter), used to print logs on <code>System.out</code>.
     */
    public static Handler newHandlerConsoleRelease(boolean isColored) {
        Formatter formatter = isColored ? newFormatterRelease() : newFormatterReleaseNoColor();
        return newHandlerConsoleOut(formatter);

        // Code for JavaConsoleHandler
        //Formatter formatter = newFormatterRelease();
        //Handler out = newHandlerConsoleOut(formatter);
        //Handler err = newHandlerConsoleErr(formatter);
        //return new JavaConsoleHandler(out, err);
    }

    /**
     * @param filePath the pattern for naming the output file.
     * @return default file handler (release formatter), used to print logs on <code>filePath</code> file.
     * @throws IOException              – if there are IO problems opening the files.
     * @throws SecurityException        – if a security manager exists and if the caller does not have LoggingPermission("control").
     * @throws IllegalArgumentException – if limit < 0, or count < 1.
     * @throws IllegalArgumentException – if pattern is an empty string
     */
    public static Handler newHandlerFileRelease(String filePath) throws IOException {
        Handler fileHandler = new FileHandler(filePath, 5 * JOSPConstants.BYTE_TRANSFORM, 10);
        fileHandler.setLevel(Level.ALL);
        fileHandler.setFormatter(newFormatterReleaseFile());
        return fileHandler;
    }

    /**
     * @return default handler (console with default formatter), used to print logs on <code>System.out</code>.
     */
    public static Handler newHandlerDefault(boolean isColored) {
        return newHandlerConsoleRelease(isColored);
    }


    // LoggerManager management

    /**
     * Setup a log environment that DO NOT print any message.
     */
    public static void setupRootLoggerNOP() {
        LogManager.getLogManager().reset();
    }

    /**
     * Setup a log environment that print all messages on
     * {@link #newHandlerConsoleDeveloper(boolean)} console handler.
     */
    public static Handler[] setupRootLoggerDevelopment(boolean isColored) {
        LogManager.getLogManager().reset();
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(Level.SEVERE);
        Handler mainHandler = newHandlerConsoleDeveloper(isColored);
        rootLogger.addHandler(mainHandler);
        return rootLogger.getHandlers();
    }

    /**
     * Setup a log environment that print all messages on
     * {@link #newHandlerConsoleRelease(boolean)} console and {@link FileHandler} handlers.
     */
    public static Handler[] setupRootLoggerRelease(boolean isColored, String filePath) throws IOException {
        LogManager.getLogManager().reset();
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(Level.SEVERE);
        rootLogger.addHandler(newHandlerConsoleRelease(isColored));
        rootLogger.addHandler(newHandlerFileRelease(filePath));
        return rootLogger.getHandlers();
    }

    /**
     * Setup a log environment that print all messages on
     * {@link #newHandlerConsoleRelease(boolean)} console handler.
     */
    public static Handler[] setupRootLoggerReleaseConsole(boolean isColored) {
        LogManager.getLogManager().reset();
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(Level.SEVERE);
        rootLogger.addHandler(newHandlerConsoleRelease(isColored));
        return rootLogger.getHandlers();
    }

    /**
     * Setup a log environment that print all messages on
     * {@link FileHandler} handler.
     */
    public static Handler[] setupRootLoggerReleaseFile(String filePath) throws IOException {
        LogManager.getLogManager().reset();
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(Level.SEVERE);
        rootLogger.addHandler(newHandlerFileRelease(filePath));
        return rootLogger.getHandlers();
    }


    // Loggers management

    /**
     * Set given level and handler for specified loggers.
     * <p>
     * If a specified logger name doesn't exist, then it's created and set.
     *
     * @param loggerName the logger's name.
     * @param level      the level to set to all loggers.
     * @param handlers   the handlers list to set to the logger.
     */
    public static void setLoggerLevel(String loggerName, Level level, Handler[] handlers) {
        Logger l = Logger.getLogger(loggerName);
        l.setUseParentHandlers(false);
        l.setLevel(level);
        for (Handler h : handlers)
            l.addHandler(h);
    }

    /**
     * Set given level and handler for all specified loggers.
     * <p>
     * If a specified logger name doesn't exist, then it's created and set.
     *
     * @param loggerNames the logger's names list.
     * @param level       the level to set to all loggers.
     * @param handlers    the handlers list to set to the logger.
     */
    public static void setLoggerLevel(String[] loggerNames, Level level, Handler[] handlers) {
        for (String l : loggerNames)
            setLoggerLevel(l, level, handlers);
    }

}
