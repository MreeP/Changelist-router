package com.github.mreep.changelistrouter.settings;

import com.intellij.util.xmlb.annotations.Tag;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Pattern;

public class RouteMapping
{

    @Tag("pattern")
    private String pattern;

    @Tag("changelistName")
    private String changelistName;

    @Tag("patternType")
    private PatternType patternType;

    public RouteMapping()
    {
        this.pattern = "";
        this.changelistName = "";
        this.patternType = PatternType.REGEX;
    }

    public RouteMapping(String pattern, String changelistName)
    {
        this.pattern = pattern;
        this.changelistName = changelistName;
        this.patternType = PatternType.REGEX;
    }

    public RouteMapping(String pattern, String changelistName, PatternType patternType)
    {
        this.pattern = pattern;
        this.changelistName = changelistName;
        this.patternType = patternType;
    }

    public String getPattern()
    {
        return this.pattern;
    }

    public void setPattern(String pattern)
    {
        this.pattern = pattern;
    }

    public String getChangelistName()
    {
        return this.changelistName;
    }

    public void setChangelistName(String changelistName)
    {
        this.changelistName = changelistName;
    }

    public PatternType getPatternType()
    {
        return this.patternType;
    }

    public void setPatternType(PatternType patternType)
    {
        this.patternType = patternType;
    }

    public boolean matches(String path)
    {
        if (this.pattern.isBlank()) {
            return false;
        }

        try {
            return switch (this.patternType) {
                case REGEX -> Pattern.compile(this.pattern).matcher(path).find();
                case GLOB -> FileSystems.getDefault().getPathMatcher("glob:" + this.pattern).matches(Path.of(path));
            };
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        RouteMapping that = (RouteMapping) o;

        return Objects.equals(this.pattern, that.pattern)
            && Objects.equals(this.changelistName, that.changelistName)
            && this.patternType == that.patternType;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.pattern, this.changelistName, this.patternType);
    }
}
