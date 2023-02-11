package work.gaigeshen.formwork.commons.web;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gaigeshen
 */
public class ValidationError {

    private final Map<String, String> violations = new HashMap<>();

    public void addViolation(String name, String message) {
        violations.put(name, message);
    }

    public Map<String, String> getViolations() {
        return violations;
    }

    public String getViolationMessages() {
        if (violations.isEmpty()) {
            return null;
        }
        return String.join("\n", violations.values());
    }
}
