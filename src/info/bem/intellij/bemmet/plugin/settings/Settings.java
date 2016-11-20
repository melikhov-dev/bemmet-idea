package info.bem.intellij.bemmet.plugin.settings;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import info.bem.intellij.bemmet.plugin.utils.Strings;
import org.jetbrains.annotations.Nullable;

@State(name = "BemmetProjectComponent",
        storages = {@Storage("bemmetConfig.xml")}
)
public class Settings implements PersistentStateComponent<Settings> {
    public String nodeInterpreter;
    public String bemmetPath;
    public String indent;
    public boolean treatAllIssuesAsWarnings;

    protected Project project;

    public static Settings getInstance(Project project) {
        Settings settings = ServiceManager.getService(project, Settings.class);
        settings.project = project;
        return settings;
    }

    @Nullable
    @Override
    public Settings getState() {
        return this;
    }

    @Override
    public void loadState(Settings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getVersion() {
        return nodeInterpreter  + bemmetPath + indent;
    }

    public boolean isEqualTo(Settings settings) {
        return settings != null &&
                treatAllIssuesAsWarnings == settings.treatAllIssuesAsWarnings &&
                Strings.areEqual(nodeInterpreter, settings.nodeInterpreter) &&
                Strings.areEqual(bemmetPath, settings.bemmetPath) &&
                Strings.areEqual(bemmetPath, settings.indent);
    }
}