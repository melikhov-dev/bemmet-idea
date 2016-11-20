package info.bem.intellij.bemmet.plugin.cli;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Created by melikhov on 20/11/16.
 */
public class BemmetRunner {
    private BemmetRunner() {
    }

    private static final Logger LOG = Logger.getInstance(BemmetRunner.class);
    private static final int TIME_OUT = (int) TimeUnit.SECONDS.toMillis(120L);


    public static BemmetResult run(
            @NotNull String nodeInterpreter,
            @NotNull String bemmetPath,
            @NotNull String text,
            String indent
    ) {
        if (indent.isEmpty()){
            indent = "    ";
        }
        BemmetSettings settings = BemmetSettings.build(nodeInterpreter, bemmetPath, text, indent);

        return run(settings);
    }

    public static BemmetResult run(@NotNull BemmetSettings settings) {
        BemmetResult result = new BemmetResult();
        try {
            GeneralCommandLine commandLine = createCommandLineLint(settings);
            ProcessOutput out = execute(commandLine, TIME_OUT);
            result.errorOutput = out.getStderr();
            try {
                result.bemJson = out.getStdout();
            } catch (Exception e) {
                LOG.error(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.errorOutput = e.toString();
        }
        return result;
    }

    private static GeneralCommandLine createCommandLineLint(BemmetSettings settings) {
        GeneralCommandLine commandLine = createCommandLine(settings);
        return commandLine;
    }

    @NotNull
    private static GeneralCommandLine createCommandLine(@NotNull BemmetSettings settings) {
        GeneralCommandLine commandLine = new GeneralCommandLine();

        commandLine.setExePath(settings.node);

        commandLine.addParameter("-e");

        String nodeCode = "console.log(require('" + settings.bemmetPath + "').stringify('" + settings.text  +"', {indent: '"+ settings.indent + "' }))";

        commandLine.addParameter(nodeCode);

        return commandLine;
    }

    @NotNull
    private static ProcessOutput execute(@NotNull GeneralCommandLine commandLine, int timeoutInMilliseconds) throws ExecutionException {
        Process process = commandLine.createProcess();
        OSProcessHandler processHandler = new ColoredProcessHandler(process, commandLine.getCommandLineString());
        final ProcessOutput output = new ProcessOutput();
        processHandler.addProcessListener(new ProcessAdapter() {
            public void onTextAvailable(ProcessEvent event, Key outputType) {
                if (outputType.equals(ProcessOutputTypes.STDERR)) {
                    output.appendStderr(event.getText());
                } else if (!outputType.equals(ProcessOutputTypes.SYSTEM)) {
                    output.appendStdout(event.getText());
                }
            }
        });
        processHandler.startNotify();
        if (processHandler.waitFor(timeoutInMilliseconds)) {
            output.setExitCode(process.exitValue());
        } else {
            processHandler.destroyProcess();
            output.setTimeout();
        }
        if (output.isTimeout()) {
            throw new ExecutionException("Command '" + commandLine.getCommandLineString() + "' is timed out.");
        }
        return output;
    }
}
