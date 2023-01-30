package work.gaigeshen.formwork.commons.bpmn;

import java.util.Set;

/**
 * 用户任务自动完成结果
 *
 * @author gaigeshen
 */
public interface UserTaskAutoCompletion {

    String getProcessId();

    String getBusinessKey();

    Set<String> getGroups();

    Set<String> getUserId();

    boolean hasMoreUserTasks();
}
