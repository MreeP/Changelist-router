package com.github.mreep.changelistrouter.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ChangelistRouterConfigurable implements Configurable
{

    private final Project project;
    private ChangelistRouterSettingsPanel panel;

    public ChangelistRouterConfigurable(Project project)
    {
        this.project = project;
    }

    @Nls
    @Override
    public String getDisplayName()
    {
        return "Changelist Router";
    }

    @Nullable
    @Override
    public JComponent createComponent()
    {
        this.panel = new ChangelistRouterSettingsPanel(this.project);
        return this.panel.getComponent();
    }

    @Override
    public boolean isModified()
    {
        ChangelistRouterSettings settings = ChangelistRouterSettings.getInstance(this.project);
        return !this.panel.getMappings().equals(settings.getState().getMappings());
    }

    @Override
    public void apply()
    {
        ChangelistRouterSettings settings = ChangelistRouterSettings.getInstance(this.project);
        settings.getState().setMappings(new ArrayList<>(this.panel.getMappings()));
    }

    @Override
    public void reset()
    {
        ChangelistRouterSettings settings = ChangelistRouterSettings.getInstance(this.project);

        List<RouteMapping> copy = new ArrayList<>();

        for (RouteMapping m : settings.getState().getMappings()) {
            copy.add(new RouteMapping(m.getPattern(), m.getChangelistName(), m.getPatternType()));
        }

        this.panel.setMappings(copy);
    }

    @Override
    public void disposeUIResources()
    {
        this.panel = null;
    }
}
