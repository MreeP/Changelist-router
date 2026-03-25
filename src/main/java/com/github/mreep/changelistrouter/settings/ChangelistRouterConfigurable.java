package com.github.mreep.changelistrouter.settings;

import com.github.mreep.changelistrouter.ChangelistRouter;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.LocalChangeList;
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
        List<RouteMapping> newMappings = new ArrayList<>(this.panel.getMappings());
        settings.getState().setMappings(newMappings);

        this.rerouteDefaultChangelist(newMappings);
    }

    private void rerouteDefaultChangelist(List<RouteMapping> mappings)
    {
        if (mappings.isEmpty()) {
            return;
        }

        ChangeListManager clm = ChangeListManager.getInstance(this.project);
        LocalChangeList defaultList = clm.getDefaultChangeList();

        if (defaultList.getChanges().isEmpty()) {
            return;
        }

        ChangelistRouter.routeChanges(this.project, defaultList.getChanges(), mappings);
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
