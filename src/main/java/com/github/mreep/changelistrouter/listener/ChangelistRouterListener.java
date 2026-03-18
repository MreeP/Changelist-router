package com.github.mreep.changelistrouter.listener;

import com.github.mreep.changelistrouter.settings.ChangelistRouterSettings;
import com.github.mreep.changelistrouter.settings.RouteMapping;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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

        ChangeListManager clm = ChangeListManager.getInstance(this.project);
        Map<String, List<Change>> routedChanges = new HashMap<>();

        for (Change change : changes) {
            String path = ChangelistRouterListener.getChangePath(change);

            if (path == null) {
                continue;
            }

            String target = ChangelistRouterListener.findMatchingChangelist(path, mappings);

            if (target == null) {
                continue;
            }

            routedChanges.computeIfAbsent(target, k -> new ArrayList<>()).add(change);
        }

        for (Map.Entry<String, List<Change>> entry : routedChanges.entrySet()) {
            String changelistName = entry.getKey();
            List<Change> changesToMove = entry.getValue();

            LocalChangeList targetList = clm.getChangeLists()
                .stream()
                .filter(cl -> cl.getName().equals(changelistName))
                .findFirst()
                .orElseGet(() -> clm.addChangeList(changelistName, ""));

            clm.moveChangesTo(targetList, changesToMove.toArray(new Change[0]));
        }
    }

    @Nullable
    private static String getChangePath(Change change)
    {
        if (change.getVirtualFile() != null) {
            return change.getVirtualFile().getPath();
        }

        if (change.getAfterRevision() != null) {
            return change.getAfterRevision().getFile().getPath();
        }

        return null;
    }

    @Nullable
    public static String findMatchingChangelist(@NotNull String path, @NotNull List<RouteMapping> mappings)
    {
        for (RouteMapping mapping : mappings) {
            if (mapping.matches(path)) {
                return mapping.getChangelistName();
            }
        }

        return null;
    }
}
