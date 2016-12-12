package info.bem.intellij.bemmet.plugin;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import info.bem.intellij.bemmet.plugin.settings.Settings;

/**
 * Created by melikhov on 20/11/16.
 */
public class BemmetPluginProjectComponent implements ProjectComponent {
    public Settings settings;
    protected Project project;
    public String nodeInterpreter;
    public String bemmetPath;
    public String indent;
    public boolean singleQuotes;
    public boolean treatAsWarnings;
    protected boolean settingValidStatus;
    protected String settingValidVersion;

    public static final String PLUGIN_NAME = "BEM";

    public BemmetPluginProjectComponent(Project project) {
        this.project = project;
        settings = Settings.getInstance(project);
    }

    @Override
    public void projectOpened() {
        if (isEnabled()) {
            isSettingsValid();
        }
    }

    @Override
    public void projectClosed() {

    }

    @Override
    public void initComponent() {
        if (isEnabled()) {
            isSettingsValid();
        }
    }

    @Override
    public void disposeComponent() {

    }

    @Override
    public String getComponentName() {
        return "BemmetPluginProjectComponent";
    }

    public boolean isEnabled() {
        return true;
    }

    public boolean isSettingsValid() {
        if (!settings.getVersion().equals(settingValidVersion)) {
            validateSettings();
            settingValidVersion = settings.getVersion();
        }
        return settingValidStatus;
    }

    public boolean validateSettings() {
        nodeInterpreter = settings.nodeInterpreter;
        bemmetPath = settings.bemmetPath;
        indent = settings.indent;
        singleQuotes = settings.singleQuotes;
        treatAsWarnings = settings.treatAllIssuesAsWarnings;
        settingValidStatus = true;
        return true;
    }
}
