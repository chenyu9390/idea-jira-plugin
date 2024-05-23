package com.ck.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializer;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.ck.config.JiraConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author kangChen
 * @Classname CommonSettions
 * @Description TODO
 * @Version 1.0.0
 * @Date 2024/5/22 23:34
 */
@State(name = "JiraPlugin", storages = @Storage("JiraPlugin.xml"))
public class CommonSettings implements PersistentStateComponent<JiraConfig> {
    private JiraConfig jiraConfig;
    /**
     * @return a component state. All properties, public and annotated fields are serialized. Only values, which differ
     * from the default (i.e., the value of newly instantiated class) are serialized. {@code null} value indicates
     * that the returned state won't be stored, as a result previously stored state will be used.
     * @see XmlSerializer
     */
    @Nullable
    @Override
    public JiraConfig getState() {
        if(jiraConfig == null){
            jiraConfig = new JiraConfig();
        }
        return jiraConfig;
    }

    /**
     * This method is called when new component state is loaded. The method can and will be called several times, if
     * com.ck.config files were externally changed while IDE was running.
     * <p>
     * State object should be used directly, defensive copying is not required.
     *
     * @param state loaded component state
     * @see XmlSerializerUtil#copyBean(Object, Object)
     */
    @Override
    public void loadState(@NotNull JiraConfig state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }
}
