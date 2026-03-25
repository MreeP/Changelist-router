package com.github.mreep.changelistrouter;

import com.github.mreep.changelistrouter.ChangelistRouter;
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
        assertEquals("Java Files", ChangelistRouter.findMatchingChangelist("Main.java", mappings));
    }

    @Test
    public void matchesDoubleStarPattern()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("**/*.java", "Java Files", PatternType.GLOB));
        assertEquals("Java Files", ChangelistRouter.findMatchingChangelist("src/main/Main.java", mappings));
    }

    @Test
    public void matchesBraceAlternatives()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("**/*.{ts,js}", "JS/TS Files", PatternType.GLOB));
        assertEquals("JS/TS Files", ChangelistRouter.findMatchingChangelist("src/app.ts", mappings));
        assertEquals("JS/TS Files", ChangelistRouter.findMatchingChangelist("src/app.js", mappings));
    }

    @Test
    public void matchesNestedTestPaths()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("modules/*/Tests/**/*.java", "Module Tests", PatternType.GLOB));
        assertEquals("Module Tests", ChangelistRouter.findMatchingChangelist("modules/core/Tests/unit/MyTest.java", mappings));
    }

    @Test
    public void doesNotMatchWhenPatternDoesNotFit()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("*.java", "Java Files", PatternType.GLOB));
        assertNull(ChangelistRouter.findMatchingChangelist("src/Main.kt", mappings));
    }

    @Test
    public void skipsInvalidPatterns()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("[invalid", "Bad Glob", PatternType.GLOB), new RouteMapping("**/*.kt", "Kotlin Files", PatternType.GLOB));
        assertEquals("Kotlin Files", ChangelistRouter.findMatchingChangelist("src/Main.kt", mappings));
    }

    @Test
    public void matchesRelativePathWithDirectoryPrefix()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("testowy/**/*.xml", "Testowy XML", PatternType.GLOB));
        assertEquals("Testowy XML", ChangelistRouter.findMatchingChangelist("testowy/sub/pliczek.xml", mappings));
    }

    @Test
    public void matchesRelativePathWithSingleLevel()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("testowy/*.xml", "Testowy XML", PatternType.GLOB));
        assertEquals("Testowy XML", ChangelistRouter.findMatchingChangelist("testowy/pliczek.xml", mappings));
    }

    @Test
    public void matchesDeeplyNestedRelativePath()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("testowy/**/*.xml", "Testowy XML", PatternType.GLOB));
        assertEquals("Testowy XML", ChangelistRouter.findMatchingChangelist("testowy/sub/dir/pliczek.xml", mappings));
    }

    @Test
    public void doesNotMatchOutsideTargetDirectory()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("testowy/**/*.xml", "Testowy XML", PatternType.GLOB));
        assertNull(ChangelistRouter.findMatchingChangelist("other/pliczek.xml", mappings));
    }

    @Test
    public void matchesSrcDirectoryGlob()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("src/**/*.java", "Source Files", PatternType.GLOB));
        assertEquals("Source Files", ChangelistRouter.findMatchingChangelist("src/main/java/com/example/App.java", mappings));
    }

    @Test
    public void singleStarDoesNotCrossDirectoryBoundaries()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("src/*.java", "Top-level Source", PatternType.GLOB));
        assertNull(ChangelistRouter.findMatchingChangelist("src/main/App.java", mappings));
        assertEquals("Top-level Source", ChangelistRouter.findMatchingChangelist("src/App.java", mappings));
    }

    @Test
    public void starDotStarMatchesFilesContainingDot()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("*.*", "Dotted Files", PatternType.GLOB));
        assertEquals("Dotted Files", ChangelistRouter.findMatchingChangelist("Main.java", mappings));
        assertEquals("Dotted Files", ChangelistRouter.findMatchingChangelist("config.yaml", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("Makefile", mappings));
    }

    @Test
    public void questionMarkMatchesExactlyOneCharacter()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("foo.?", "Single Ext", PatternType.GLOB));
        assertEquals("Single Ext", ChangelistRouter.findMatchingChangelist("foo.a", mappings));
        assertEquals("Single Ext", ChangelistRouter.findMatchingChangelist("foo.z", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("foo.ab", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("foo.", mappings));
    }

    @Test
    public void bracketExpressionMatchesSingleCharacter()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("[abc].txt", "ABC", PatternType.GLOB));
        assertEquals("ABC", ChangelistRouter.findMatchingChangelist("a.txt", mappings));
        assertEquals("ABC", ChangelistRouter.findMatchingChangelist("b.txt", mappings));
        assertEquals("ABC", ChangelistRouter.findMatchingChangelist("c.txt", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("d.txt", mappings));
    }

    @Test
    public void bracketRangeMatchesCharacterRange()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("file[0-9].txt", "Numbered", PatternType.GLOB));
        assertEquals("Numbered", ChangelistRouter.findMatchingChangelist("file3.txt", mappings));
        assertEquals("Numbered", ChangelistRouter.findMatchingChangelist("file0.txt", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("filea.txt", mappings));
    }

    @Test
    public void bracketNegationExcludesCharacters()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("[!0-9].txt", "Non-digit", PatternType.GLOB));
        assertEquals("Non-digit", ChangelistRouter.findMatchingChangelist("a.txt", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("3.txt", mappings));
    }

    @Test
    public void doubleStarMatchesZeroOrMoreDirectories()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("src/**", "All Source", PatternType.GLOB));
        assertEquals("All Source", ChangelistRouter.findMatchingChangelist("src/Main.java", mappings));
        assertEquals("All Source", ChangelistRouter.findMatchingChangelist("src/com/example/Main.java", mappings));
    }

    @Test
    public void doubleStarMatchesZeroDirectories()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("test/**/*.ts", "Test TS", PatternType.GLOB));
        assertEquals("Test TS", ChangelistRouter.findMatchingChangelist("test/first.ts", mappings));
        assertEquals("Test TS", ChangelistRouter.findMatchingChangelist("test/sub/first.ts", mappings));
        assertEquals("Test TS", ChangelistRouter.findMatchingChangelist("test/a/b/first.ts", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("other/first.ts", mappings));
    }

    @Test
    public void multipleDoubleStarsMatchZeroDirectories()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("tests/**/View/**/*.ts", "View Tests", PatternType.GLOB));
        assertEquals("View Tests", ChangelistRouter.findMatchingChangelist("tests/unit/View/components/App.ts", mappings));
        assertEquals("View Tests", ChangelistRouter.findMatchingChangelist("tests/View/components/App.ts", mappings));
        assertEquals("View Tests", ChangelistRouter.findMatchingChangelist("tests/unit/View/App.ts", mappings));
        assertEquals("View Tests", ChangelistRouter.findMatchingChangelist("tests/View/App.ts", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("src/View/App.ts", mappings));
    }

    @Test
    public void patternStartingWithDoubleStarsMatchZeroDirectories()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("**/View/*.ts", "View Tests", PatternType.GLOB));
        assertEquals("View Tests", ChangelistRouter.findMatchingChangelist("tests/unit/View/App.ts", mappings));
        assertEquals("View Tests", ChangelistRouter.findMatchingChangelist("View/App.ts", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("App.ts", mappings));
    }

    @Test
    public void singleStarMatchesHiddenFiles()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("*", "All Files", PatternType.GLOB));
        assertEquals("All Files", ChangelistRouter.findMatchingChangelist(".gitignore", mappings));
        assertEquals("All Files", ChangelistRouter.findMatchingChangelist(".login", mappings));
    }

    @Test
    public void doubleStarSlashStarMatchesAllNestedFiles()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("**/*Test*.java", "Test Files", PatternType.GLOB));
        assertEquals("Test Files", ChangelistRouter.findMatchingChangelist("src/test/java/MyTest.java", mappings));
        assertEquals("Test Files", ChangelistRouter.findMatchingChangelist("src/TestRunner.java", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("src/Main.java", mappings));
    }

    @Test
    public void singleStarMatchesPartialFileName()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("src/*Controller.java", "Controllers", PatternType.GLOB));
        assertEquals("Controllers", ChangelistRouter.findMatchingChangelist("src/UserController.java", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("src/UserService.java", mappings));
    }

    @Test
    public void braceGroupWithMultipleAlternatives()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("**/*.{java,kt,scala}", "JVM Files", PatternType.GLOB));
        assertEquals("JVM Files", ChangelistRouter.findMatchingChangelist("src/Main.java", mappings));
        assertEquals("JVM Files", ChangelistRouter.findMatchingChangelist("src/Main.kt", mappings));
        assertEquals("JVM Files", ChangelistRouter.findMatchingChangelist("src/Main.scala", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("src/Main.py", mappings));
    }

    @Test
    public void caseInsensitiveGlobMatchesIgnoringCase()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("**/*.java", "Java Files", PatternType.GLOB, false));
        assertEquals("Java Files", ChangelistRouter.findMatchingChangelist("src/Main.java", mappings));
        assertEquals("Java Files", ChangelistRouter.findMatchingChangelist("src/Main.JAVA", mappings));
        assertEquals("Java Files", ChangelistRouter.findMatchingChangelist("src/Main.Java", mappings));
    }

    @Test
    public void caseSensitiveGlobDoesNotMatchDifferentCase()
    {
        List<RouteMapping> mappings = List.of(new RouteMapping("**/*.java", "Java Files", PatternType.GLOB, true));
        assertEquals("Java Files", ChangelistRouter.findMatchingChangelist("src/Main.java", mappings));
        assertNull(ChangelistRouter.findMatchingChangelist("src/Main.JAVA", mappings));
    }
}
