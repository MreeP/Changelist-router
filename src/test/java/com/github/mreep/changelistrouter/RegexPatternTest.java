package com.github.mreep.changelistrouter;

import com.github.mreep.changelistrouter.ChangelistRouter;
import com.github.mreep.changelistrouter.settings.PatternType;
import com.github.mreep.changelistrouter.settings.RouteMapping;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RegexPatternTest
{

    @Test
    public void matchesFilePathAgainstPattern()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping(".*Test.*", "Tests"));
        assertEquals("Tests", ChangelistRouter.findMatchingChangelist("src/MyTest.kt", mappings));
    }

    @Test
    public void returnsNullWhenNoPatternMatches()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping(".*Test.*", "Tests"));
        assertNull(ChangelistRouter.findMatchingChangelist("src/Main.kt", mappings));
    }

    @Test
    public void firstMatchingPatternWins()
    {
        List<RouteMapping> mappings = List.of(
            new RouteMapping(".*Test.*", "Tests"),
            new RouteMapping(".*\\.kt", "Kotlin Files")
        );
        assertEquals("Tests", ChangelistRouter.findMatchingChangelist("src/MyTest.kt", mappings));
    }

    @Test
    public void secondPatternMatchesWhenFirstDoesNot()
    {
        List<RouteMapping> mappings = List.of(
            new RouteMapping(".*Test.*", "Tests"),
            new RouteMapping(".*\\.kt", "Kotlin Files")
        );
        assertEquals("Kotlin Files", ChangelistRouter.findMatchingChangelist("src/Main.kt", mappings));
    }

    @Test
    public void skipsBlankPatterns()
    {
        List<RouteMapping> mappings = List.of(
            new RouteMapping("", "Empty"),
            new RouteMapping(".*\\.kt", "Kotlin Files")
        );
        assertEquals("Kotlin Files", ChangelistRouter.findMatchingChangelist("src/Main.kt", mappings));
    }

    @Test
    public void skipsInvalidRegexPatterns()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("[invalid", "Bad Regex"), new RouteMapping(".*\\.kt", "Kotlin Files"));
        assertEquals("Kotlin Files", ChangelistRouter.findMatchingChangelist("src/Main.kt", mappings));
    }

    @Test
    public void emptyMappingsReturnsNull()
    {
        assertNull(ChangelistRouter.findMatchingChangelist("src/Main.kt", Collections.emptyList()));
    }

    @Test
    public void matchesDirectoryPatterns()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping(".*/api/.*", "API Changes"));
        assertEquals("API Changes", ChangelistRouter.findMatchingChangelist("src/main/api/UserController.kt", mappings));
    }

    @Test
    public void matchesRelativePathWithNestedDirectories()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("src/test/.*", "Test Files"));
        assertEquals("Test Files", ChangelistRouter.findMatchingChangelist("src/test/java/MyTest.java", mappings));
    }

    @Test
    public void matchesRelativePathWithExtension()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping(".*\\.xml", "XML Files"));
        assertEquals("XML Files", ChangelistRouter.findMatchingChangelist("testowy/pliczek.xml", mappings));
    }

    @Test
    public void matchesFilesContainingDot()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("[^/]*\\.[^/]*", "Dotted Files"));
        assertEquals("Dotted Files", ChangelistRouter.findMatchingChangelist("Main.java", mappings));
        assertEquals("Dotted Files", ChangelistRouter.findMatchingChangelist("config.yaml", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("Makefile", mappings));
    }

    @Test
    public void matchesSingleCharacterExtension()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("^foo\\..{1}$", "Single Ext"));
        assertEquals("Single Ext", ChangelistRouter.findMatchingChangelist("foo.a", mappings));
        assertEquals("Single Ext", ChangelistRouter.findMatchingChangelist("foo.z", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("foo.ab", mappings));
    }

    @Test
    public void characterClassMatchesSingleCharacter()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("^[abc]\\.txt$", "ABC"));
        assertEquals("ABC", ChangelistRouter.findMatchingChangelist("a.txt", mappings));
        assertEquals("ABC", ChangelistRouter.findMatchingChangelist("b.txt", mappings));
        assertEquals("ABC", ChangelistRouter.findMatchingChangelist("c.txt", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("d.txt", mappings));
    }

    @Test
    public void characterRangeMatchesDigits()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("^file[0-9]\\.txt$", "Numbered"));
        assertEquals("Numbered", ChangelistRouter.findMatchingChangelist("file3.txt", mappings));
        assertEquals("Numbered", ChangelistRouter.findMatchingChangelist("file0.txt", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("filea.txt", mappings));
    }

    @Test
    public void negatedCharacterClassExcludesCharacters()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("^[^0-9]\\.txt$", "Non-digit"));
        assertEquals("Non-digit", ChangelistRouter.findMatchingChangelist("a.txt", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("3.txt", mappings));
    }

    @Test
    public void matchesAllFilesUnderDirectory()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("^src/.*", "All Source"));
        assertEquals("All Source", ChangelistRouter.findMatchingChangelist("src/Main.java", mappings));
        assertEquals("All Source", ChangelistRouter.findMatchingChangelist("src/com/example/Main.java", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("test/Main.java", mappings));
    }

    @Test
    public void matchesHiddenFiles()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("^\\.", "Hidden Files"));
        assertEquals("Hidden Files", ChangelistRouter.findMatchingChangelist(".gitignore", mappings));
        assertEquals("Hidden Files", ChangelistRouter.findMatchingChangelist(".login", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("visible.txt", mappings));
    }

    @Test
    public void matchesTestFilesAcrossDirectories()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping(".*Test[^/]*\\.java$", "Test Files"));
        assertEquals("Test Files", ChangelistRouter.findMatchingChangelist("src/test/java/MyTest.java", mappings));
        assertEquals("Test Files", ChangelistRouter.findMatchingChangelist("src/TestRunner.java", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("src/Main.java", mappings));
    }

    @Test
    public void matchesPartialFileName()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("^src/[^/]*Controller\\.java$", "Controllers"));
        assertEquals("Controllers", ChangelistRouter.findMatchingChangelist("src/UserController.java", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("src/UserService.java", mappings));
    }

    @Test
    public void alternationMatchesMultipleExtensions()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping(".*\\.(java|kt|scala)$", "JVM Files"));
        assertEquals("JVM Files", ChangelistRouter.findMatchingChangelist("src/Main.java", mappings));
        assertEquals("JVM Files", ChangelistRouter.findMatchingChangelist("src/Main.kt", mappings));
        assertEquals("JVM Files", ChangelistRouter.findMatchingChangelist("src/Main.scala", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("src/Main.py", mappings));
    }

    @Test
    public void caseInsensitiveRegexMatchesIgnoringCase()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping(".*test.*", "Tests", PatternType.REGEX, false));
        assertEquals("Tests", ChangelistRouter.findMatchingChangelist("src/MyTest.kt", mappings));
        assertEquals("Tests", ChangelistRouter.findMatchingChangelist("src/mytest.kt", mappings));
        assertEquals("Tests", ChangelistRouter.findMatchingChangelist("src/MYTEST.kt", mappings));
    }

    @Test
    public void caseSensitiveRegexDoesNotMatchDifferentCase()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping(".*test.*", "Tests", PatternType.REGEX, true));
        assertNull(ChangelistRouter.findMatchingChangelist("src/MyTest.kt", mappings));
        assertEquals("Tests", ChangelistRouter.findMatchingChangelist("src/mytest.kt", mappings));
    }
}
