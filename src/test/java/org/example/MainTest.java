package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import static org.example.Strategy.*;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    List<String> data = new ArrayList<>();
    Map<String, List<Integer>> index;

    @BeforeEach
    public void initializeData(){
        data = Arrays.asList(
                "John Smith john.smith@gmail.com",
                "Jane Doe jane.doe@gmail.com",
                "Alice Johnson alice@gmail.com",
                "John David david@gmail.com"
        );
        index = Main.invertedIndex(data);
    }

    @Test
    void testInvertedIndex() {
        assertEquals(Arrays.asList(0, 3), index.get("john"));
        assertEquals(List.of(0), index.get("smith"));
        assertEquals(List.of(1), index.get("jane"));
        assertEquals(List.of(2), index.get("alice"));
    }

    @Test
    void testFindingMatches_ALL() {
        String[] search = {"john"};
        Set<Integer> result = Main.findingMatches(data, index, ALL, search);
        assertEquals(new HashSet<>(Arrays.asList(0, 3)), result);
    }

    @Test
    void testFindingMatches_ANY() {
        String[] search = {"john", "alice"};
        Set<Integer> result = Main.findingMatches(data, index, ANY, search);
        assertEquals(new HashSet<>(Arrays.asList(0, 2, 3)), result);
    }

    @Test
    void testFindingMatches_NONE() {
        String[] search = {"john"};
        Set<Integer> result = Main.findingMatches(data, index, NONE, search);
        assertEquals(new HashSet<>(Arrays.asList(1, 2)), result);
    }

    @Test
    void testCaseInsensitiveSearch() {
        String[] search = {"joHn"};
        Set<Integer> result = Main.findingMatches(data, index, ALL, search);
        assertEquals(new HashSet<>(Arrays.asList(0, 3)), result);
    }

    @Test
    void testEmptySearch(){
        String[] search = { };
        Set<Integer> result = Main.findingMatches(data, index, ALL, search);
        assertTrue(result.isEmpty());
    }

    @Test
    void testEmptyData(){
        List<String> emptyData = new ArrayList<>();
        Map<String, List<Integer>> emptyIndex = Main.invertedIndex(emptyData);
        String[] search = {"John"};
        Set<Integer> result = Main.findingMatches(emptyData, emptyIndex, ANY, search);
        assertTrue(result.isEmpty());
    }

    @Test
    void testMainMenuSearchAndPrint() {
        try {
            File tempFile = File.createTempFile("testData", ".txt");
            tempFile.deleteOnExit();
            Files.write(tempFile.toPath(), data, StandardCharsets.UTF_8);

            String simulatedUserInput = String.join(System.lineSeparator(),
                    "1",
                    "ANY",
                    "john",
                    "2",
                    "0"
            ) + System.lineSeparator();

            InputStream originalIn = System.in;
            PrintStream originalOut = System.out;
            ByteArrayInputStream in = new ByteArrayInputStream(simulatedUserInput.getBytes(StandardCharsets.UTF_8));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setIn(in);
            System.setOut(new PrintStream(out));

            Main.main(new String[]{"--data", tempFile.getAbsolutePath()});

            System.setIn(originalIn);
            System.setOut(originalOut);

            String output = out.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("=== Menu ==="));
            assertTrue(output.contains("persons found"));
            assertTrue(output.contains("john.smith@gmail.com"));
            assertTrue(output.contains("david@gmail.com"));
            assertTrue(output.contains("Jane Doe"));
            assertTrue(output.contains("Bye!"));

        } catch (IOException e) {
            fail("Test failed due to I/O exception: " + e.getMessage());
        }
    }

    @Test
    void testMainWithMissingArguments() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));

        Main.main(new String[]{});

        System.setOut(originalOut);

        String output = out.toString();
        assertTrue(output.contains("Usage: java SimpleReader --data <filename>"));
    }
}
