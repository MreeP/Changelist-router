package com.github.mreep.changelistrouter;

import com.github.mreep.changelistrouter.listener.ChangelistRouterListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vcs.changes.ChangeListListener;
import org.jetbrains.annotations.NotNull;

public class ChangelistRouterActivity implements StartupActivity.DumbAware
{

    @Override
    public void runActivity(@NotNull Project project)
    {
        project
            .getMessageBus()
            .connect()
            .subscribe(
                ChangeListListener.TOPIC,
                new ChangelistRouterListener(project)
            );
    }
}
