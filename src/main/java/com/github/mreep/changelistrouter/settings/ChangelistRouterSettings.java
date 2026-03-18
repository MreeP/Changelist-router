package com.github.mreep.changelistrouter.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.annotations.XCollection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Service(Service.Level.PROJECT)
@State(name = "ChangelistRouterSettings", storages = @Storage("changelistRouter.xml"))
public final class ChangelistRouterSettings implements PersistentStateComponent<ChangelistRouterSettings.State>
{

    public static class State
    {
        @XCollection(elementTypes = RouteMapping.class)
        private List<RouteMapping> mappings = new ArrayList<>();

        public List<RouteMapping> getMappings()
        {
            return this.mappings;
        }

        public void setMappings(List<RouteMapping> mappings)
        {
            this.mappings = mappings;
        }
    }

    private State state = new State();

    @Override
    public @NotNull State getState()
    {
        return this.state;
    }

    @Override
    public void loadState(@NotNull State state)
    {
        this.state = state;
    }

    public static ChangelistRouterSettings getInstance(@NotNull Project project)
    {
        return project.getService(ChangelistRouterSettings.class);
    }
}
