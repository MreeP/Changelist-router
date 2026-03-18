package com.github.mreep.changelistrouter.settings;

import com.intellij.util.xmlb.annotations.Tag;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RouteMapping
{

    @Tag("pattern")
    private String pattern;

    @Tag("changelistName")
    private String changelistName;

    public RouteMapping()
    {
        this.pattern = "";
        this.changelistName = "";
    }

    public RouteMapping(String pattern, String changelistName)
    {
        this.pattern = pattern;
        this.changelistName = changelistName;
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

    public boolean matches(String path)
    {
        if (this.pattern.isBlank()) {
            return false;
        }

        try {
            return Pattern.compile(this.pattern).matcher(path).find();
        } catch (PatternSyntaxException ignored) {
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

        return Objects.equals(this.pattern, that.pattern) && Objects.equals(this.changelistName, that.changelistName);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.pattern, this.changelistName);
    }
}
