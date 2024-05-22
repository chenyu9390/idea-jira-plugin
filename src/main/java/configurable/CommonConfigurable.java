package configurable;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import config.JiraConfig;
import org.jetbrains.annotations.Nullable;
import settings.CommonSettings;
import view.ConfigView;

import javax.swing.*;

/**
 * @author kangChen
 * @Classname CommonConfigurable
 * @Description TODO
 * @Version 1.0.0
 * @Date 2024/5/22 23:37
 */
public class CommonConfigurable implements Configurable {

    private ConfigView view = new ConfigView();

    private JiraConfig config = ServiceManager.getService(CommonSettings.class).getState();


    /**
     * Returns the visible name of the configurable component.
     * Note, that this method must return the display name
     * that is equal to the display name declared in XML
     * to avoid unexpected errors.
     *
     * @return the visible name of the configurable component
     */
    @Override
    public String getDisplayName() {
        return "jira";
    }

    /**
     * Creates new Swing form that enables user to configure the settings.
     * Usually this method is called on the EDT, so it should not take a long time.
     * <p>
     * Also this place is designed to allocate resources (subscriptions/listeners etc.)
     *
     * @return new Swing form to show, or {@code null} if it cannot be created
     * @see #disposeUIResources
     */
    @Nullable
    @Override
    public JComponent createComponent() {
        return view.getContentPane();
    }

    /**
     * Indicates whether the Swing form was modified or not.
     * This method is called very often, so it should not take a long time.
     *
     * @return {@code true} if the settings were modified, {@code false} otherwise
     */
    @Override
    public boolean isModified() {
        /*if (!StrUtil.equals(config.getPassword(),view.getPassword().getText()) || !StrUtil.equals(config.getUserName(),view.getUsername().getText())) {
            return true;
        }*/
        return false;
    }

    /**
     * Stores the settings from the Swing form to the configurable component.
     * This method is called on EDT upon user's request.
     *
     * @throws ConfigurationException if values cannot be applied
     */
    @Override
    public void apply() throws ConfigurationException {
        //config.setPassword(view.getPassword().getText());
        //config.setUserName(view.getUsername().getText());
    }
}
