package com.github.mreep.changelistrouter;

import com.github.mreep.changelistrouter.listener.ChangelistRouterListener;
import com.github.mreep.changelistrouter.settings.PatternType;
import com.github.mreep.changelistrouter.settings.RouteMapping;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ChangelistRouterTest
{

    @Test
    public void matchesFilePathAgainstPattern()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping(".*Test.*", "Tests"));
        assertEquals("Tests", ChangelistRouterListener.findMatchingChangelist("/src/MyTest.kt", mappings));
    }

    @Test
    public void returnsNullWhenNoPatternMatches()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping(".*Test.*", "Tests"));
        assertNull(ChangelistRouterListener.findMatchingChangelist("/src/Main.kt", mappings));
    }

    @Test
    public void firstMatchingPatternWins()
    {
        List<RouteMapping> mappings = List.of(
            new RouteMapping(".*Test.*", "Tests"),
            new RouteMapping(".*\\.kt", "Kotlin Files")
        );
        assertEquals("Tests", ChangelistRouterListener.findMatchingChangelist("/src/MyTest.kt", mappings));
    }

    @Test
    public void secondPatternMatchesWhenFirstDoesNot()
    {
        List<RouteMapping> mappings = List.of(
            new RouteMapping(".*Test.*", "Tests"),
            new RouteMapping(".*\\.kt", "Kotlin Files")
        );
        assertEquals("Kotlin Files", ChangelistRouterListener.findMatchingChangelist("/src/Main.kt", mappings));
    }

    @Test
    public void skipsBlankPatterns()
    {
        List<RouteMapping> mappings = List.of(
            new RouteMapping("", "Empty"),
            new RouteMapping(".*\\.kt", "Kotlin Files")
        );
        assertEquals("Kotlin Files", ChangelistRouterListener.findMatchingChangelist("/src/Main.kt", mappings));
    }

    @Test
    public void skipsInvalidRegexPatterns()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("[invalid", "Bad Regex"), new RouteMapping(".*\\.kt", "Kotlin Files"));
        assertEquals("Kotlin Files", ChangelistRouterListener.findMatchingChangelist("/src/Main.kt", mappings));
    }

    @Test
    public void emptyMappingsReturnsNull()
    {
        assertNull(ChangelistRouterListener.findMatchingChangelist("/src/Main.kt", Collections.emptyList()));
    }

    @Test
    public void matchesDirectoryPatterns()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping(".*/api/.*", "API Changes"));
        assertEquals("API Changes", ChangelistRouterListener.findMatchingChangelist("/src/main/api/UserController.kt", mappings));
    }

    // Glob pattern tests

    @Test
    public void globMatchesSimpleExtension()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("*.java", "Java Files", PatternType.GLOB));
        assertEquals("Java Files", ChangelistRouterListener.findMatchingChangelist("Main.java", mappings));
    }

    @Test
    public void globMatchesDoubleStarPattern()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("**/*.java", "Java Files", PatternType.GLOB));
        assertEquals("Java Files", ChangelistRouterListener.findMatchingChangelist("src/main/Main.java", mappings));
    }

    @Test
    public void globMatchesBraceAlternatives()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("**/*.{ts,js}", "JS/TS Files", PatternType.GLOB));
        assertEquals("JS/TS Files", ChangelistRouterListener.findMatchingChangelist("src/app.ts", mappings));
        assertEquals("JS/TS Files", ChangelistRouterListener.findMatchingChangelist("src/app.js", mappings));
    }

    @Test
    public void globMatchesNestedTestPaths()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("modules/*/Tests/**/*.java", "Module Tests", PatternType.GLOB));
        assertEquals("Module Tests", ChangelistRouterListener.findMatchingChangelist("modules/core/Tests/unit/MyTest.java", mappings));
    }

    @Test
    public void globDoesNotMatchWhenItShouldNot()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("*.java", "Java Files", PatternType.GLOB));
        assertNull(ChangelistRouterListener.findMatchingChangelist("src/Main.kt", mappings));
    }

    @Test
    public void globSkipsInvalidPatterns()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("[invalid", "Bad Glob", PatternType.GLOB), new RouteMapping("**/*.kt", "Kotlin Files", PatternType.GLOB));
        assertEquals("Kotlin Files", ChangelistRouterListener.findMatchingChangelist("src/Main.kt", mappings));
    }
}
