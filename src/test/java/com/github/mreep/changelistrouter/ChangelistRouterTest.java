package com.github.mreep.changelistrouter;

import com.github.mreep.changelistrouter.listener.ChangelistRouterListener;
import com.github.mreep.changelistrouter.settings.RouteMapping;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class ChangelistRouterTest {

    @Test
    public void matchesFilePathAgainstPattern() {
        List<RouteMapping> mappings = List.of(new RouteMapping(".*Test.*", "Tests"));
        assertEquals("Tests", ChangelistRouterListener.findMatchingChangelist("/src/MyTest.kt", mappings));
    }

    @Test
    public void returnsNullWhenNoPatternMatches() {
        List<RouteMapping> mappings = List.of(new RouteMapping(".*Test.*", "Tests"));
        assertNull(ChangelistRouterListener.findMatchingChangelist("/src/Main.kt", mappings));
    }

    @Test
    public void firstMatchingPatternWins() {
        List<RouteMapping> mappings = List.of(
                new RouteMapping(".*Test.*", "Tests"),
                new RouteMapping(".*\\.kt", "Kotlin Files")
        );
        assertEquals("Tests", ChangelistRouterListener.findMatchingChangelist("/src/MyTest.kt", mappings));
    }

    @Test
    public void secondPatternMatchesWhenFirstDoesNot() {
        List<RouteMapping> mappings = List.of(
                new RouteMapping(".*Test.*", "Tests"),
                new RouteMapping(".*\\.kt", "Kotlin Files")
        );
        assertEquals("Kotlin Files", ChangelistRouterListener.findMatchingChangelist("/src/Main.kt", mappings));
    }

    @Test
    public void skipsBlankPatterns() {
        List<RouteMapping> mappings = List.of(
                new RouteMapping("", "Empty"),
                new RouteMapping(".*\\.kt", "Kotlin Files")
        );
        assertEquals("Kotlin Files", ChangelistRouterListener.findMatchingChangelist("/src/Main.kt", mappings));
    }

    @Test
    public void skipsInvalidRegexPatterns() {
        List<RouteMapping> mappings = List.of(
                new RouteMapping("[invalid", "Bad Regex"),
                new RouteMapping(".*\\.kt", "Kotlin Files")
        );
        assertEquals("Kotlin Files", ChangelistRouterListener.findMatchingChangelist("/src/Main.kt", mappings));
    }

    @Test
    public void emptyMappingsReturnsNull() {
        assertNull(ChangelistRouterListener.findMatchingChangelist("/src/Main.kt", Collections.emptyList()));
    }

    @Test
    public void matchesDirectoryPatterns() {
        List<RouteMapping> mappings = List.of(new RouteMapping(".*/api/.*", "API Changes"));
        assertEquals("API Changes", ChangelistRouterListener.findMatchingChangelist("/src/main/api/UserController.kt", mappings));
    }
}
