package com.github.mreep.changelistrouter.settings;

public enum PatternType
{

    REGEX("Regex"), GLOB("Glob");

    private final String displayName;

    PatternType(String displayName)
    {
        this.displayName = displayName;
    }

    @Override
    public String toString()
    {
        return this.displayName;
    }
}
