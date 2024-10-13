package org.prokhorovgm.service;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.apache.commons.math3.util.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnalyzeService {
    private static final List<Character> SYMBOLS = Arrays.asList(
        'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Z', 'X', 'C', 'V', 'B', 'N', 'M',
        'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'v', 'v', 'b', 'n', 'm'
    );

    public static Map<String, BigDecimal> analyzeLiters(List<String> words) {
        List<Character> allChars = words.stream()
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
                .map(sum -> 100 / sum)
                .map(sum -> BigDecimal.valueOf(sum).setScale(0, RoundingMode.UP))
                .orElse(BigDecimal.ZERO)
            ))
            .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    public static Map<String, BigDecimal> analyze(List<String> words) {
        Properties properties = new Properties();
        properties.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        StanfordCoreNLP nlp = new StanfordCoreNLP(properties);

        StringBuilder textBuilder = new StringBuilder();
        for (String word : words.stream()
            .filter(w -> SYMBOLS.stream().anyMatch(s -> w.contains(s.toString())))
            .collect(Collectors.toList())
        ) {
            textBuilder.append(word).append(" ");
        }
        String text = textBuilder.toString().trim();

        CoreDocument document = new CoreDocument(text);
        nlp.annotate(document);

        Map<String, BigDecimal> wordFrequencies = new HashMap<>();
        for (CoreSentence sentence : document.sentences()) {
            for (CoreLabel token : sentence.tokens()) {
                String lemma = token.lemma().toLowerCase(); // Лемматизация и приведение к нижнему регистру
                wordFrequencies.put(lemma, wordFrequencies.getOrDefault(lemma, BigDecimal.ZERO).add(BigDecimal.ONE));
            }
        }

        return wordFrequencies;
    }
}
