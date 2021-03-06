package io.antmedia.logger;

import org.red5.server.Launcher;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;

/**
 * Appender for logback in charge of sending the logged events to a Google Analytics server.
 */
public class AntmediaAppender extends AppenderBase<ILoggingEvent> {
    private final GoogleAnalyticsLogger googleAnalyticsLogger;

    public AntmediaAppender() {
        googleAnalyticsLogger = new GoogleAnalyticsLoggerImp(Launcher.getInstanceId());
    }

    public AntmediaAppender(GoogleAnalyticsLogger googleAnalyticsLogger) {
        this.googleAnalyticsLogger = googleAnalyticsLogger;
    }

    @Override
    public void append(ILoggingEvent iLoggingEvent) {
        if (LoggerEnvironment.isManagingThread()) {
            return;
        }
        LoggerEnvironment.startManagingThread();
        try {
            IThrowableProxy throwbleProxy = iLoggingEvent.getThrowableProxy();
            if (throwbleProxy != null) {
                googleAnalyticsLogger.log(throwbleProxy);
            }
        } catch (Exception e) {
            addError("An exception occurred", e);
        } finally {
            LoggerEnvironment.stopManagingThread();
        }
    }

    @Override
    public void stop() {
        LoggerEnvironment.startManagingThread();
        try {
            if (!isStarted()) {
                return;
            }
            super.stop();
        } catch (Exception e) {
            addError("An exception occurred", e);
        } finally {
            LoggerEnvironment.stopManagingThread();
        }
    }
}