package entities;

import actions.*;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.TextRange;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBPanelWithEmptyText;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import logger.InteractionLogger;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class MySimpleToolWindowPanel extends SimpleToolWindowPanel {
    private static MySimpleToolWindowPanel instance;
    private boolean GAM = true;
    private final Project project;
    private DefaultActionGroup actionGroup;
    private ActionToolbar actionToolbar;
    private JBPanelWithEmptyText contentPanel;
    private CardLayout cardLayout;
    private JBPanel<?> cardPanel;
    private JTextPane textPane;
    private JTextPane diffTextPane;
    private String originalCode;
    private String originalSummary;
    private Document codeDocument;
    private String filePath;
    private TextRange selectedRange;

    private MySimpleToolWindowPanel(boolean vertical, Project project) {
        super(vertical);
        this.project = project;

        initializeActionToolbar();
        initializeContent();
    }

    public static MySimpleToolWindowPanel getInstance(boolean vertical, Project project) {
        if (instance == null) {
            instance = new MySimpleToolWindowPanel(vertical, project);
        }
        return instance;
    }

    public static MySimpleToolWindowPanel getInstance() {
        assert instance != null;
        return instance;
    }

    private void initializeActionToolbar() {
        if (actionGroup == null) {
            actionGroup = new DefaultActionGroup();
        } else {
            actionGroup.removeAll();
        }

        RetrieveSummaryAction retrieveSummaryAction = new RetrieveSummaryAction("Retrieve Summary", "Retrieve summary", AllIcons.Actions.Find);
        DiffSummariesAction diffSummariesAction = new DiffSummariesAction("Diff Summaries", "Diff summaries", AllIcons.Actions.Diff);
        CommitModifiedSummaryAction commitModifiedSummaryAction = new CommitModifiedSummaryAction("Commit Modified Summary", "Commit modified summary", AllIcons.Actions.Edit);
        CommitPromptAction commitPromptAction = new CommitPromptAction("Commit Prompt", "Commit prompt", AllIcons.Actions.Edit);
        AcceptModifiedCodeAction acceptModifiedCodeAction = new AcceptModifiedCodeAction("Accept Modified Code", "Accept modified code", AllIcons.Actions.Checked);

        retrieveSummaryAction.setBase(this);
        diffSummariesAction.setBase(this);
        commitModifiedSummaryAction.setBase(this);
        commitPromptAction.setBase(this);
        acceptModifiedCodeAction.setBase(this);

        actionGroup.add(new ModeLabelAction(this));
        if (GAM) {
            actionGroup.add(retrieveSummaryAction);
            actionGroup.add(diffSummariesAction);
            actionGroup.addSeparator();
            actionGroup.add(commitModifiedSummaryAction);
        } else {
            actionGroup.add(commitPromptAction);
        }
        actionGroup.add(acceptModifiedCodeAction);

        if (actionToolbar == null) {
            actionToolbar = ActionManager.getInstance().createActionToolbar("GAM", actionGroup, true);
            actionToolbar.setTargetComponent(this);
            setToolbar(actionToolbar.getComponent());
        } else {
            actionToolbar.updateActionsAsync();
        }
    }

    private void initializeContent() {
        contentPanel = new JBPanelWithEmptyText(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Content content = ContentFactory.getInstance().createContent(contentPanel, "", false);
        setContent(content.getComponent());

        createInputArea();
        createCodeEditor();
    }

    private void createInputArea() {
        // Setting up the input area with a placeholder
        textPane = new JTextPane();
        textPane.setBackground(JBColor.WHITE);
        textPane.setFont(new Font("Monospaced", Font.PLAIN, 14));

        // Setup diff text pane with HTML content
        diffTextPane = new JTextPane();
        diffTextPane.setContentType("text/html");
        diffTextPane.setEditable(false);
        diffTextPane.setBackground(JBColor.WHITE);

        // Card layout to toggle between text displays
        cardLayout = new CardLayout();
        cardPanel = new JBPanel<>(cardLayout);
        cardPanel.add(textPane, "Plain Text");
        cardPanel.add(diffTextPane, "Rich Text");
        contentPanel.add(cardPanel, BorderLayout.NORTH);
    }

    private void createCodeEditor() {
        // Credit: https://github.com/carlrobertoh/CodeGPT/blob/445b71184c7d5c4abd9b5228d8f9c0bd656102cc/src/main/kotlin/ee/carlrobert/codegpt/ui/textarea/CodePreviewTooltipContent.kt
        codeDocument = EditorFactory.getInstance().createDocument("");
        FileType fileType = FileTypeManager.getInstance().getFileTypeByExtension("c");
        EditorFactory editorFactory = EditorFactory.getInstance();
        Editor codeEditor = editorFactory.createEditor(codeDocument, project, fileType, false);
        EditorSettings settings = codeEditor.getSettings();
        settings.setLineNumbersShown(false);
        settings.setFoldingOutlineShown(false);
        settings.setLineMarkerAreaShown(false);
        contentPanel.add(codeEditor.getComponent(), BorderLayout.CENTER);
    }

    public void toggleGAM() {
        GAM = !GAM;
        initializeActionToolbar();
        refresh();
        InteractionLogger.log(new HashMap<>() {{
            put("event", "toggle_state");
            put("project_path", project.getBasePath());
            put("new_state", GAM ? "GAM" : "Baseline");
        }});
    }

    public void refresh() {
        setOriginalSummary("");
        getTextPane().setText("");
        getDiffTextPane().setText("");
        if (getDiffTextPane().isVisible()) {
            getCardLayout().next(getCardPanel());
        }
        WriteCommandAction.runWriteCommandAction(project, () -> codeDocument.setText(""));
    }

    public void setOriginalCode(String originalCode) {
        this.originalCode = originalCode;
    }

    public void setOriginalSummary(String originalSummary) {
        this.originalSummary = originalSummary;
    }

    public JTextPane getTextPane() {
        return textPane;
    }

    public JTextPane getDiffTextPane() {
        return diffTextPane;
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public JBPanel<?> getCardPanel() {
        return cardPanel;
    }

    public String getOriginalCode() {
        return originalCode;
    }

    public String getOriginalSummary() {
        return originalSummary;
    }

    public Document getCodeDocument() {
        return codeDocument;
    }

    public Project getProject() {
        return project;
    }

    public void setSelectedRange(TextRange selectedRange) {
        this.selectedRange = selectedRange;
    }

    public TextRange getSelectedRange() {
        return selectedRange;
    }

    public boolean isGAM() {
        return GAM;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
