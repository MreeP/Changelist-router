package com.github.mreep.changelistrouter;

import com.github.mreep.changelistrouter.listener.ChangelistRouterListener;
import com.github.mreep.changelistrouter.settings.PatternType;
import com.github.mreep.changelistrouter.settings.RouteMapping;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GlobPatternTest
{

    @Test
    public void matchesSimpleExtension()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("*.java", "Java Files", PatternType.GLOB));
        assertEquals("Java Files", ChangelistRouterListener.findMatchingChangelist("Main.java", mappings));
    }

    @Test
    public void matchesDoubleStarPattern()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("**/*.java", "Java Files", PatternType.GLOB));
        assertEquals("Java Files", ChangelistRouterListener.findMatchingChangelist("src/main/Main.java", mappings));
    }

    @Test
    public void matchesBraceAlternatives()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("**/*.{ts,js}", "JS/TS Files", PatternType.GLOB));
        assertEquals("JS/TS Files", ChangelistRouterListener.findMatchingChangelist("src/app.ts", mappings));
        assertEquals("JS/TS Files", ChangelistRouterListener.findMatchingChangelist("src/app.js", mappings));
    }

    @Test
    public void matchesNestedTestPaths()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("modules/*/Tests/**/*.java", "Module Tests", PatternType.GLOB));
        assertEquals("Module Tests", ChangelistRouterListener.findMatchingChangelist("modules/core/Tests/unit/MyTest.java", mappings));
    }

    @Test
    public void doesNotMatchWhenPatternDoesNotFit()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("*.java", "Java Files", PatternType.GLOB));
        assertNull(ChangelistRouterListener.findMatchingChangelist("src/Main.kt", mappings));
    }

    @Test
    public void skipsInvalidPatterns()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("[invalid", "Bad Glob", PatternType.GLOB), new RouteMapping("**/*.kt", "Kotlin Files", PatternType.GLOB));
        assertEquals("Kotlin Files", ChangelistRouterListener.findMatchingChangelist("src/Main.kt", mappings));
    }

    @Test
    public void matchesRelativePathWithDirectoryPrefix()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("testowy/**/*.xml", "Testowy XML", PatternType.GLOB));
        assertEquals("Testowy XML", ChangelistRouterListener.findMatchingChangelist("testowy/sub/pliczek.xml", mappings));
    }

    @Test
    public void matchesRelativePathWithSingleLevel()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("testowy/*.xml", "Testowy XML", PatternType.GLOB));
        assertEquals("Testowy XML", ChangelistRouterListener.findMatchingChangelist("testowy/pliczek.xml", mappings));
    }

    @Test
    public void matchesDeeplyNestedRelativePath()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("testowy/**/*.xml", "Testowy XML", PatternType.GLOB));
        assertEquals("Testowy XML", ChangelistRouterListener.findMatchingChangelist("testowy/sub/dir/pliczek.xml", mappings));
    }

    @Test
    public void doesNotMatchOutsideTargetDirectory()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("testowy/**/*.xml", "Testowy XML", PatternType.GLOB));
        assertNull(ChangelistRouterListener.findMatchingChangelist("other/pliczek.xml", mappings));
    }

    @Test
    public void matchesSrcDirectoryGlob()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("src/**/*.java", "Source Files", PatternType.GLOB));
        assertEquals("Source Files", ChangelistRouterListener.findMatchingChangelist("src/main/java/com/example/App.java", mappings));
    }

    @Test
    public void singleStarDoesNotCrossDirectoryBoundaries()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("src/*.java", "Top-level Source", PatternType.GLOB));
        assertNull(ChangelistRouterListener.findMatchingChangelist("src/main/App.java", mappings));
        assertEquals("Top-level Source", ChangelistRouterListener.findMatchingChangelist("src/App.java", mappings));
    }

    @Test
    public void starDotStarMatchesFilesContainingDot()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("*.*", "Dotted Files", PatternType.GLOB));
        assertEquals("Dotted Files", ChangelistRouterListener.findMatchingChangelist("Main.java", mappings));
        assertEquals("Dotted Files", ChangelistRouterListener.findMatchingChangelist("config.yaml", mappings));
        assertNull(ChangelistRouterListener.findMatchingChangelist("Makefile", mappings));
    }

    @Test
    public void questionMarkMatchesExactlyOneCharacter()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("foo.?", "Single Ext", PatternType.GLOB));
        assertEquals("Single Ext", ChangelistRouterListener.findMatchingChangelist("foo.a", mappings));
        assertEquals("Single Ext", ChangelistRouterListener.findMatchingChangelist("foo.z", mappings));
        assertNull(ChangelistRouterListener.findMatchingChangelist("foo.ab", mappings));
        assertNull(ChangelistRouterListener.findMatchingChangelist("foo.", mappings));
    }

    @Test
    public void bracketExpressionMatchesSingleCharacter()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("[abc].txt", "ABC", PatternType.GLOB));
        assertEquals("ABC", ChangelistRouterListener.findMatchingChangelist("a.txt", mappings));
        assertEquals("ABC", ChangelistRouterListener.findMatchingChangelist("b.txt", mappings));
        assertEquals("ABC", ChangelistRouterListener.findMatchingChangelist("c.txt", mappings));
        assertNull(ChangelistRouterListener.findMatchingChangelist("d.txt", mappings));
    }

    @Test
    public void bracketRangeMatchesCharacterRange()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("file[0-9].txt", "Numbered", PatternType.GLOB));
        assertEquals("Numbered", ChangelistRouterListener.findMatchingChangelist("file3.txt", mappings));
        assertEquals("Numbered", ChangelistRouterListener.findMatchingChangelist("file0.txt", mappings));
        assertNull(ChangelistRouterListener.findMatchingChangelist("filea.txt", mappings));
    }

    @Test
    public void bracketNegationExcludesCharacters()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("[!0-9].txt", "Non-digit", PatternType.GLOB));
        assertEquals("Non-digit", ChangelistRouterListener.findMatchingChangelist("a.txt", mappings));
        assertNull(ChangelistRouterListener.findMatchingChangelist("3.txt", mappings));
    }

    @Test
    public void doubleStarMatchesZeroOrMoreDirectories()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("src/**", "All Source", PatternType.GLOB));
        assertEquals("All Source", ChangelistRouterListener.findMatchingChangelist("src/Main.java", mappings));
        assertEquals("All Source", ChangelistRouterListener.findMatchingChangelist("src/com/example/Main.java", mappings));
    }

    @Test
    public void singleStarMatchesHiddenFiles()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("*", "All Files", PatternType.GLOB));
        assertEquals("All Files", ChangelistRouterListener.findMatchingChangelist(".gitignore", mappings));
        assertEquals("All Files", ChangelistRouterListener.findMatchingChangelist(".login", mappings));
    }

    @Test
    public void doubleStarSlashStarMatchesAllNestedFiles()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("**/*Test*.java", "Test Files", PatternType.GLOB));
        assertEquals("Test Files", ChangelistRouterListener.findMatchingChangelist("src/test/java/MyTest.java", mappings));
        assertEquals("Test Files", ChangelistRouterListener.findMatchingChangelist("src/TestRunner.java", mappings));
        assertNull(ChangelistRouterListener.findMatchingChangelist("src/Main.java", mappings));
    }

    @Test
    public void singleStarMatchesPartialFileName()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("src/*Controller.java", "Controllers", PatternType.GLOB));
        assertEquals("Controllers", ChangelistRouterListener.findMatchingChangelist("src/UserController.java", mappings));
        assertNull(ChangelistRouterListener.findMatchingChangelist("src/UserService.java", mappings));
    }

    @Test
    public void braceGroupWithMultipleAlternatives()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("**/*.{java,kt,scala}", "JVM Files", PatternType.GLOB));
        assertEquals("JVM Files", ChangelistRouterListener.findMatchingChangelist("src/Main.java", mappings));
        assertEquals("JVM Files", ChangelistRouterListener.findMatchingChangelist("src/Main.kt", mappings));
        assertEquals("JVM Files", ChangelistRouterListener.findMatchingChangelist("src/Main.scala", mappings));
        assertNull(ChangelistRouterListener.findMatchingChangelist("src/Main.py", mappings));
    }
}
