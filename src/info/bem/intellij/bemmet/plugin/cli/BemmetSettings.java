package info.bem.intellij.bemmet.plugin.cli;

import org.jetbrains.annotations.NotNull;

/**
 * Created by melikhov on 20/11/16.
 */
public class BemmetSettings {
    public String node;
    public String bemmetPath;
    public String text;
    public String indent;
    public boolean singleQuotes;

    public static BemmetSettings build(@NotNull String nodeInterpreter,
                                         @NotNull String bemmetPath,
                                         @NotNull String text,
                                         @NotNull String indent,
                                         @NotNull Boolean singleQuotes
    ) {
        BemmetSettings settings = new BemmetSettings();
        settings.node = nodeInterpreter;
        settings.bemmetPath = bemmetPath;
        settings.text = text;
        settings.indent = indent;
        settings.singleQuotes = singleQuotes;
        return settings;
    }
}
