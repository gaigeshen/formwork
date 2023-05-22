package work.gaigeshen.formwork.basal.logging;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class LogRecordStack {

    private final Stack<LogRecord> recordStack = new Stack<>();

    public void pushRecord(LogRecord record) {
        recordStack.push(record);
    }

    public List<LogRecord> toList() {
        List<LogRecord> list = new ArrayList<>();
        while (!recordStack.empty()) {
            list.add(recordStack.pop());
        }
        return list;
    }
}
