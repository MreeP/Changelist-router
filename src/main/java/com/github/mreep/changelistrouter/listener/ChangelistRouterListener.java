package com.github.mreep.changelistrouter.listener;

import com.github.mreep.changelistrouter.ChangelistRouter;
import com.github.mreep.changelistrouter.settings.ChangelistRouterSettings;
import com.github.mreep.changelistrouter.settings.RouteMapping;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeList;
import com.intellij.openapi.vcs.changes.ChangeListListener;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class ChangelistRouterListener implements ChangeListListener
{

    private final Project project;

    public ChangelistRouterListener(Project project)
    {
        this.project = project;
    }

    @Override
    public void changesAdded(@NotNull Collection<? extends Change> changes, @NotNull ChangeList toList)
    {
        if (!(toList instanceof LocalChangeList localList)) {
            return;
        }

        if (!localList.isDefault()) {
            return;
        }

        ChangelistRouterSettings settings = ChangelistRouterSettings.getInstance(this.project);
        List<RouteMapping> mappings = settings.getState().getMappings();

        if (mappings.isEmpty()) {
            return;
        }

        ChangelistRouter.routeChanges(this.project, changes, mappings);
    }
}
