package org.prokhorovgm.service;

import org.apache.commons.math3.util.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnalyzeService {
    private static final List<Character> SYMBOLS = Arrays.asList(
        'Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K','L','Z','X','C','V','B','N','M',
        'q','w','e','r','t','y','u','i','o','p','a','s','d','f','g','h','j','k','l','z','x','v','v','b','n','m'
    );

    public static Map<String, BigDecimal> analyze(List<String> words) {
        List<Character> allChars =  words.stream()
            .flatMap(w -> w.chars().mapToObj(c -> (char) c))
            .filter(SYMBOLS::contains)
            .collect(Collectors.toList());

        Map<Character, Long> wordsCharacters = allChars.stream()
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<String, Double> wordStat = wordsCharacters.entrySet().stream()
            .collect(Collectors.toMap(e -> e.getKey().toString(), e -> (double) e.getValue() / allChars.size() * 100));

        return SYMBOLS.stream()
            .map(Object::toString)
            .map(String::toUpperCase)
            .distinct()
            .map(s -> new Pair<>(s, Stream.of(wordStat.get(s), wordStat.get(s.toLowerCase()))
                .filter(Objects::nonNull)
                .reduce(Double::sum)
                .map(sum -> 100/sum)
                .map(sum -> BigDecimal.valueOf(sum).setScale(0, RoundingMode.UP))
                .orElse(BigDecimal.ZERO)
            ))
            .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }
}
