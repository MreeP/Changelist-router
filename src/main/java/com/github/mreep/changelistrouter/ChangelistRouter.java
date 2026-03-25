package com.github.mreep.changelistrouter;

import com.github.mreep.changelistrouter.settings.RouteMapping;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;

public class ChangelistRouter
{

    public static void routeChanges(@NotNull Project project, @NotNull Collection<? extends Change> changes, @NotNull List<RouteMapping> mappings)
    {
        ChangeListManager clm = ChangeListManager.getInstance(project);
        Map<String, List<Change>> routedChanges = new HashMap<>();

        String basePath = project.getBasePath();

        for (Change change : changes) {
            String path = ChangelistRouter.getChangePath(change);

            if (path == null) {
                continue;
            }

            if (basePath != null) {
                path = Path.of(basePath).relativize(Path.of(path)).toString();
            }

            String target = ChangelistRouter.findMatchingChangelist(path, mappings);

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
