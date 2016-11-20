package info.bem.intellij.bemmet.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import info.bem.intellij.bemmet.plugin.BemmetPluginProjectComponent;
import info.bem.intellij.bemmet.plugin.cli.BemmetResult;
import info.bem.intellij.bemmet.plugin.cli.BemmetRunner;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by melikhov on 20/11/16.
 */
public class text2bemjson extends AnAction {

    private static final Logger LOG = LoggerFactory.getLogger(text2bemjson.class);
    protected Project project;

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        determineVisibility(e);
        project = e.getProject();
        if (project == null) {
            return;
        }

        BemmetPluginProjectComponent component = project.getComponent(BemmetPluginProjectComponent.class);

    }


    @NotNull
    protected Runnable getReplacementRunnable(final Document document, SelectionModel selectionModel) {
        final String selectedText = selectionModel.getSelectedText();
        final int start = selectionModel.getSelectionStart();
        final int end = selectionModel.getSelectionEnd();

        BemmetPluginProjectComponent component = project.getComponent(BemmetPluginProjectComponent.class);

        BemmetResult bemmetResult = BemmetRunner.run(
                component.nodeInterpreter,
                component.bemmetPath,
                selectedText);
        final String bemJson = bemmetResult.getBemJson();

        return new Runnable() {
            @Override
            public void run() {
                if (bemJson == null) {
                    return;
                }
                document.replaceString(
                        start,
                        end,
                        bemJson);

            }
        };
    }


    @Override
    public void actionPerformed(AnActionEvent actionEvent) {
        final Editor editor = getCurrentEditor(actionEvent);
        if (!hasEditorSelectedText(editor)) {
            return;
        }
        Document document = editor.getDocument();
        SelectionModel selectionModel = editor.getSelectionModel();
        Runnable replacementProcess = getReplacementRunnable(document, selectionModel);
        Project project = actionEvent.getRequiredData(CommonDataKeys.PROJECT);
        WriteCommandAction.runWriteCommandAction(project, replacementProcess);
        selectionModel.removeSelection();
    }


    protected void determineVisibility(AnActionEvent actionEvent) {
        try {
            Editor editor = getCurrentEditor(actionEvent);
            if (editor == null) {
                return;
            }
            actionEvent.getPresentation().setEnabled(hasEditorSelectedText(editor));
        } catch (Exception e) {
            LOG.error("Something went wrong during determineVisibility(): " + e.getLocalizedMessage(), e);
        }
    }

    protected Editor getCurrentEditor(AnActionEvent actionEvent) {
        return actionEvent.getData(PlatformDataKeys.EDITOR);
    }

    protected boolean hasEditorSelectedText(Editor editor) {
        if (editor == null) {
            return false;
        }
        final SelectionModel selectionModel = editor.getSelectionModel();
        return isTextSelected(selectionModel.getSelectedText());
    }

    protected boolean isTextSelected(String selectedText) {
        return selectedText != null && selectedText.length() > 0;
    }

}
