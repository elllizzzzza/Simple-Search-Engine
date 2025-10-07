package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    List<String> data = new ArrayList<>();
    Map<String, ArrayList<Integer>> index;

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
        Set<Integer> result = Main.findingMatches(data, index, "ALL", search);

        assertEquals(new HashSet<>(Arrays.asList(0, 3)), result);
    }

    @Test
    void testFindingMatches_ANY() {
        String[] search = {"john", "alice"};
        Set<Integer> result = Main.findingMatches(data, index, "ANY", search);

        assertEquals(new HashSet<>(Arrays.asList(0, 2, 3)), result);
    }

    @Test
    void testFindingMatches_NONE() {
        String[] search = {"john"};
        Set<Integer> result = Main.findingMatches(data, index, "NONE", search);

        assertEquals(new HashSet<>(Arrays.asList(1, 2)), result);
    }

    @Test
    void testFindingMatches_UNKNOWN(){
        String[] search = {"john"};
        Set<Integer> result = Main.findingMatches(data, index, "AAAA", search);
        assertTrue(result.isEmpty());
    }
}
