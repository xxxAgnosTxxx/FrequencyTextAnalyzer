package org.prokhorovgm.service;

import java.util.List;
import java.util.Map;

public class OrchestratorService {
    private static final String DEFAULT_FILE = "src/main/resources/test.xlsx";

    public static void start() {
        List<String> words = DataService.readWords(DEFAULT_FILE, 0);
        AnalyzeService.analyze(words).entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .forEach(e -> System.out.printf("Every %sth liter is a '%s' character.\n", e.getValue(), e.getKey()));
    }
}
