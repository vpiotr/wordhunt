package wordhunt;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MatcherUtilsTest {

    private final String[] TEST_TERMS_INPUT = new String[] {"Hello, world! prepareWords fast.", "terms"};
    private final String[] TEST_TERMS_SEP = new String[] {"HELLO", "WORLD", "PREPAREWORDS", "FAST", "TERMS"};
    private final String TEST_TEXT = "Lorem ipsum dictumst volutpat vel";
    private final String[] TEST_TEXT_WORDS = new String[] {"Lorem", "ipsum", "dictumst", "volutpat", "vel"};
    private final String[] TEST_TEXT_WORDS_SEL_UP = new String[] {"LOREM", "IPSUM", "DICTUMST", "VOLUTPAT", "VEL"};
    private final String[] TEST_TEXT_WORDS_STRIP = new String[] {"Lorem", "ipsum", "volutpat", "vel", "additional"};
    private final String TEST_TEXT_WORDS_STRIP_OUT = "additional";

    private final String TEST_TEXT_WITH_TERMS = "LoremIpsum dictumst volutpatVel.";
    private final String[] TEST_TEXT_WORDS_FROM_TERMS_CASE_SENS = new String[] {"Lorem", "Ipsum", "dictumst", "volutpat", "Vel", "volutpatVel", "LoremIpsum"};
    private final String[] TEST_TEXT_WORDS_FROM_TERMS_CASE_INSENS = new String[] {"LOREM", "IPSUM", "DICTUMST", "VOLUTPAT", "VEL", "VOLUTPATVEL", "LOREMIPSUM"};

    @Test
    void prepareWordsFromTerms() {
        String[] actual = MatcherUtils.prepareWordsFromTerms(TEST_TERMS_INPUT);
        String[] expected = TEST_TERMS_SEP;
        assertArrayEquals(expected, actual);
    }

    @Test
    void matchesAllWordsCaseSens() {
        boolean actual = MatcherUtils.matchesAllWords(TEST_TEXT, TEST_TEXT_WORDS, true, true);
        assertTrue(actual);
    }

    @Test
    void matchesAllWordsCaseInsens() {
        boolean actual = MatcherUtils.matchesAllWords(TEST_TEXT, TEST_TEXT_WORDS_SEL_UP, false, true);
        assertTrue(actual);
    }

    @Test
    void stripMatchingWordsWithAdditional() {
        List<String> expected = Arrays.asList(TEST_TEXT_WORDS_STRIP_OUT);
        List<String> actual = MatcherUtils.stripMatchingWords(TEST_TEXT_WORDS_STRIP, TEST_TEXT, true, true);
        assertEquals(expected, actual);
    }

    @Test
    void stripMatchingWordsWithEmptyText() {
        List<String> expected = new ArrayList<>();
        Collections.addAll(expected, TEST_TEXT_WORDS_STRIP);
        List<String> actual = MatcherUtils.stripMatchingWords(TEST_TEXT_WORDS_STRIP, "", true, true);
        assertEquals(expected, actual);
    }

    @Test
    void stripMatchingWordsWithEmptyWords() {
        List<String> expected = new ArrayList<>();
        List<String> actual = MatcherUtils.stripMatchingWords(new String[0], TEST_TEXT, true, true);
        assertEquals(expected, actual);
    }

    @Test
    void extractWordsCaseSens() {
        Set<String> expected = new HashSet<>();
        Collections.addAll(expected, TEST_TEXT_WORDS);
        Set<String> actual = MatcherUtils.extractWords(TEST_TEXT, true, true);
        assertEquals(expected, actual);
    }

    @Test
    void extractWordsFromTermsCaseSens() {
        Set<String> expected = new HashSet<>();
        Collections.addAll(expected, TEST_TEXT_WORDS_FROM_TERMS_CASE_SENS);
        Set<String> actual = MatcherUtils.extractWords(TEST_TEXT_WITH_TERMS, true, true);
        assertEquals(expected, actual);
    }

    @Test
    void extractWordsFromTermsCaseInsens() {
        Set<String> expected = new HashSet<>();
        Collections.addAll(expected, TEST_TEXT_WORDS_FROM_TERMS_CASE_INSENS);
        Set<String> actual = MatcherUtils.extractWords(TEST_TEXT_WITH_TERMS, false, true);
        assertEquals(expected, actual);
    }
}