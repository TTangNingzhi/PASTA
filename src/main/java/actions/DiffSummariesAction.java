package actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.NlsActions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;

import java.util.Arrays;
import java.util.List;
import javax.swing.*;

public class DiffSummariesAction extends BaseAction {
    public DiffSummariesAction(@Nullable @NlsActions.ActionText String text,
                               @Nullable @NlsActions.ActionDescription String description,
                               @Nullable Icon icon) {
        super(text, description, icon);
    }

    public void actionPerformed(@NotNull AnActionEvent e) {
        if (getBase().getOriginalSummary().equals("") || getBase().getTextPane().getText().equals("")) return;
        getBase().getDiffTextPane().setText(getDiffHTML(getBase().getOriginalSummary(), getBase().getTextPane().getText()));
        getBase().getCardLayout().next(getBase().getCardPanel());
    }

    public static String getDiffHTML(String originalText, String revisedText) {
        List<String> originalWords = Arrays.asList(originalText.trim().split("\\s+"));
        List<String> revisedWords = Arrays.asList(revisedText.trim().split("\\s+"));
        Patch<String> patch = DiffUtils.diff(originalWords, revisedWords);
        StringBuilder sb = new StringBuilder();
        sb.append("""
                <html>
                <head>
                    <style>
                        body {
                            font-family: 'Monospaced', monospace;
                            font-size: 14pt;
                        }
                        
                        .inserted {
                            color: red;
                            font-weight: bold;
                        }
                        
                        .deleted {
                            color: green;
                            font-weight: bold;
                            text-decoration: line-through;
                            text-decoration-color: green;
                            text-decoration-style: solid;
                            text-decoration-thickness: 2px;
                        }
                    </style>
                </head>
                <body>
                """);

        int lastIndex = 0;
        for (AbstractDelta<String> delta : patch.getDeltas()) {
            // System.out.println(delta.getSource() + " -> " + delta.getTarget());
            List<String> original = delta.getSource().getLines();
            List<String> revised = delta.getTarget().getLines();
            // Add unchanged parts
            for (int i = lastIndex; i < delta.getSource().getPosition(); i++) {
                sb.append(originalWords.get(i)).append(" ");
            }
            // Handle changes
            if (!original.isEmpty() && !original.get(0).equals("")) {
                sb.append("<span class=\"deleted\">").append(String.join(" ", original)).append("</span> ");
            }
            if (!revised.isEmpty() && !revised.get(0).equals("")) {
                sb.append("<span class=\"inserted\">").append(String.join(" ", revised)).append("</span> ");
            }
            lastIndex = delta.getSource().getPosition() + original.size();
        }
        // Add the rest of the unchanged text
        for (int i = lastIndex; i < originalWords.size(); i++) {
            sb.append(originalWords.get(i)).append(" ");
        }
        sb.append("""
                </body>
                </html>
                """);
        return sb.toString().trim();
    }
}
