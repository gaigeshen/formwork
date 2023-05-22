package work.gaigeshen.formwork.basal.logging;

import java.util.List;

public interface RecordStore {

    void saveRecords(List<LogRecord> records);
}
