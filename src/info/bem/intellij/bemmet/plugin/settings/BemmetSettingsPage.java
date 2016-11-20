package info.bem.intellij.bemmet.plugin.settings;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiManager;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.TextFieldWithHistory;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.util.NotNullProducer;
import com.intellij.util.ui.UIUtil;
import com.intellij.webcore.ui.SwingHelper;
import info.bem.intellij.bemmet.plugin.BemmetPluginProjectComponent;
import info.bem.intellij.bemmet.plugin.cli.BemmetSettings;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.io.File;
import java.util.List;

public class BemmetSettingsPage implements Configurable {
    public static final String FIX_IT = "Fix it";
    public static final String HOW_TO_USE_BEM = "How to Use Bemmet";
    public static final String HOW_TO_USE_LINK = "https://github.com/tadatuta/bemmet";
    protected Project project;

    private JPanel panel;
    private JPanel errorPanel;
    private TextFieldWithHistoryWithBrowseButton bemmetField;
    private TextFieldWithHistoryWithBrowseButton nodeInterpreterField;
    private HyperlinkLabel usageLink;
    private JLabel nodeInterpreterLabel;
    private JLabel pathToBemmetLabel;
    private JLabel IndentLabel;
    private JCheckBox useTabCheckbox;

    public BemmetSettingsPage(@NotNull final Project project) {
        this.project = project;

        configNodeField();
        configBemmetField();

        DocumentAdapter docAdp = new DocumentAdapter() {
            protected void textChanged(DocumentEvent e) {
                updateLaterInEDT();
            }
        };

        nodeInterpreterField.getChildComponent().getTextEditor().getDocument().addDocumentListener(docAdp);
        bemmetField.getChildComponent().getTextEditor().getDocument().addDocumentListener(docAdp);
    }

    private void addDocumentListenerToComp(TextFieldWithHistoryWithBrowseButton field, DocumentAdapter docAdp) {
        field.getChildComponent().getTextEditor().getDocument().addDocumentListener(docAdp);
    }

    private File getProjectPath() {
        if (project == null || project.getBaseDir() == null) {
            return null;
        }
        return new File(project.getBaseDir().getPath());
    }

    private void updateLaterInEDT() {
        UIUtil.invokeLaterIfNeeded(new Runnable() {
            public void run() {
                BemmetSettingsPage.this.update();
            }
        });
    }

    private void update() {
        ApplicationManager.getApplication().assertIsDispatchThread();
    }


    private void validate() {

    }

    private BemmetSettings settings;

    private static TextFieldWithHistory configWithDefaults(TextFieldWithHistoryWithBrowseButton field) {
        TextFieldWithHistory textFieldWithHistory = field.getChildComponent();
        textFieldWithHistory.setHistorySize(-1);
        textFieldWithHistory.setMinimumAndPreferredWidth(0);
        return textFieldWithHistory;
    }

    private void configNodeField() {
        TextFieldWithHistory textFieldWithHistory = configWithDefaults(nodeInterpreterField);
        SwingHelper.installFileCompletionAndBrowseDialog(project, nodeInterpreterField, "Select node interpreter",
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Bemmet";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        loadSettings();
        return panel;
    }

    private static boolean areEqual(TextFieldWithHistoryWithBrowseButton field, String value) {
        return field.getChildComponent().getText().equals(value);
    }

    @Override
    public boolean isModified() {
        Settings s = getSettings();
        Settings uiSettings = toSettings();
        return !s.isEqualTo(uiSettings);
    }

    @Override
    public void apply() throws ConfigurationException {
        saveSettings();
        PsiManager.getInstance(project).dropResolveCaches();
    }

    protected void saveSettings() {
        Settings settings = getSettings();
        copyTo(settings);
        project.getComponent(BemmetPluginProjectComponent.class).validateSettings();
        DaemonCodeAnalyzer.getInstance(project).restart();
    }

    public Settings toSettings() {
        Settings settings = new Settings();
        copyTo(settings);
        return settings;
    }

    public void copyTo(Settings settings) {
        settings.nodeInterpreter = nodeInterpreterField.getChildComponent().getText();
        settings.bemmetPath = bemmetField.getChildComponent().getText();
        settings.indent = useTabCheckbox.isSelected() ? "\t" : " ";
    }

    protected void loadSettings() {
        Settings settings = getSettings();
        nodeInterpreterField.getChildComponent().setText(settings.nodeInterpreter);
        bemmetField.getChildComponent().setText(settings.bemmetPath);
        useTabCheckbox.setSelected(settings.indent.equals("\t"));
    }

    @Override
    public void reset() {
        loadSettings();
    }

    @Override
    public void disposeUIResources() {
    }

    protected Settings getSettings() {
        return Settings.getInstance(project);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        usageLink = SwingHelper.createWebHyperlink(HOW_TO_USE_BEM, HOW_TO_USE_LINK);
    }

    private void configBemmetField() {
        configWithDefaults(bemmetField);
        SwingHelper.installFileCompletionAndBrowseDialog(project, bemmetField, "Select bemmet",
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());

    }
}