package work.gaigeshen.formwork.basal.logging;

import java.util.List;

public interface LogRecordStore {

    void saveLogRecords(List<LogRecord> logRecords);
}
