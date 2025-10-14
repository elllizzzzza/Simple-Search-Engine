package org.example;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

public final class Main {
    private Main() {

    }
    /**
     * The entry point of the program.
     *
     * @param args the input arguments
     */
    @SuppressFBWarnings("PATH_TRAVERSAL_IN")
    public static void main(final String[] args) {
        if (args.length < 2 || !args[0].equals("--data")) {
            System.out.println("Usage: java SimpleReader --data <filename>");
            return;
        }

        String filename = args[1];
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filename),
                        StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Failed to read file. " + e.getMessage());
        }

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        int input = -1;

        while (input != 0) {
            System.out.println("""
                    === Menu ===
                    1. Search information.
                    2. Print all data.
                    0. Exit.""");
            System.out.print(">");

            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
            } else {
                System.out.println("Incorrect option! Try again.");
                scanner.nextLine();
                continue;
            }

            while (input < 0 || input > 2) {
                System.out.println("Incorrect option! Try again.");
            }

            System.out.println();

            switch (input) {
                case 0:
                    System.out.println("Bye!");
                    break;
                case 1:
                    System.out.println("Select a matching strategy: "
                            + "ALL, ANY, NONE ");
                    scanner.nextLine();
                    Strategy strategy = null;
                    while (strategy == null) {
                        try {
                            strategy = Strategy.valueOf(
                                    scanner.nextLine().trim().toUpperCase());
                        } catch (IllegalArgumentException e) {
                            System.out.println("Unknown strategy. "
                                    + "Please enter ALL, ANY, or NONE."
                                    + e.getMessage());
                        }
                    }

                    System.out.println("\n Enter a name or email to "
                            + "search all suitable people.");
                    String names = scanner.nextLine().toLowerCase();
                    String[] keywords = names.split("\\s+");

                    Map<String, List<Integer>> inverted =
                            invertedIndex(lines);
                    Set<Integer> results = findingMatches(
                            lines, inverted, strategy, keywords);

                    printSearchResults(results, lines);
                    break;
                case 2:
                    printData(lines);
                    break;
                default:
                    break;
            }
            System.out.println();
        }
    }

    /***
     * Method for printing data.
     * @param lines - the input argument
     */
    private static void printData(final List<String> lines) {
        for (String line : lines) {
            System.out.println(line);
        }
    }

    /**
     * Method for printing result.
     * @param results - the input argument
     * @param lines - the input argument
     */
    private static void printSearchResults(final Set<Integer> results,
                                           final List<String> lines) {
        if (!results.isEmpty()) {
            System.out.println();
            System.out.println(results.size() + " persons found:");
            for (Integer i : results) {
                System.out.println(lines.get(i));
            }
        } else {
            System.out.println("No matching people found.");
        }
    }

    /**
     * Builds an inverted index from the list of input lines.
     *
     * @param lines the list of lines to index
     * @return a map of word to list of line indices
     */
    public static Map<String, List<Integer>> invertedIndex(
            final List<String> lines) {
        Map<String, List<Integer>> indexes = new HashMap<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] words = line.split("\\s+");
            for (String word : words) {
                if (indexes.containsKey(word.toLowerCase())) {
                    indexes.get(word.toLowerCase()).add(i);
                } else {
                    ArrayList<Integer> arr = new ArrayList<>();
                    arr.add(i);
                    indexes.put(word.toLowerCase(), arr);
                }
            }
        }
        return indexes;
    }

    /**
     * Finds matching line indices based on the selected
     * strategy and search keywords.
     *
     * @param lines the list of all lines
     * @param invertedIndexes the inverted index map
     * @param strategy the matching strategy: ALL, ANY, NONE
     * @param words the keywords to search for
     * @return a set of matching line indices
     */
    public static Set<Integer> findingMatches(final List<String> lines,
                            final Map<String,
                            List<Integer>> invertedIndexes,
                            final Strategy strategy, final String[] words) {
        Set<Integer> matchedIndexes = new HashSet<>();

        switch (strategy) {
            case ALL:
                boolean first = true;
                for (String word : words) {
                    List<Integer> list = invertedIndexes.get(
                            word.toLowerCase());
                    if (list == null) {
                        return matchedIndexes;
                    }
                    if (first) {
                        matchedIndexes.addAll(list);
                        first = false;
                    } else {
                        matchedIndexes.retainAll(list);
                    }
                }
                break;
            case ANY:
                for (String word : words) {
                    List<Integer> list = invertedIndexes.get(
                            word.toLowerCase());
                    if (list != null) {
                        matchedIndexes.addAll(list);
                    }
                }
                break;
            case NONE:
                for (int i = 0; i < lines.size(); i++) {
                    matchedIndexes.add(i);
                }
                for (String word : words) {
                    List<Integer> list = invertedIndexes.get(
                            word.toLowerCase());
                    if (list != null) {
                        matchedIndexes.removeAll(list);
                    }
                }
                break;
            default:
                System.out.println("Unknown strategy.");
                break;
        }
        return matchedIndexes;
    }
}
