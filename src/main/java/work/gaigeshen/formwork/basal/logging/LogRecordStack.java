package work.gaigeshen.formwork.basal.logging;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class LogRecordStack {

    private final Stack<LogRecord> logRecordStack = new Stack<>();

    public void pushLogRecord(LogRecord logRecord) {
        logRecordStack.push(logRecord);
    }

    public List<LogRecord> toLogRecords() {
        List<LogRecord> list = new ArrayList<>();
        while (!logRecordStack.empty()) {
            list.add(logRecordStack.pop());
        }
        return list;
    }
}
