package entities;

import actions.*;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.TextRange;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBPanelWithEmptyText;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;
import java.awt.*;

public class MySimpleToolWindowPanel extends SimpleToolWindowPanel {
    private static MySimpleToolWindowPanel instance;
    private final Project project;
    private DefaultActionGroup actionGroup;
    private ActionToolbar actionToolbar;
    private JBPanelWithEmptyText contentPanel;
    private CardLayout cardLayout;
    private JBPanel<?> cardPanel;
    private JTextPane proceduralTextPane;
    private JTextPane declarativeTextPane;
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
        CommitProceduralPromptAction commitProceduralPromptAction = new CommitProceduralPromptAction("Commit Summary-Mediated Prompt", "Commit summary-mediated prompt", AllIcons.Actions.Edit);
        CommitDeclarativePromptAction commitDeclarativePromptAction = new CommitDeclarativePromptAction("Commit Direct Instruction Prompt", "Commit direct instruction prompt", AllIcons.Actions.Edit);
        AcceptModifiedCodeAction acceptModifiedCodeAction = new AcceptModifiedCodeAction("Accept Modified Code", "Accept modified code", AllIcons.Actions.Checked);

        retrieveSummaryAction.setBase(this);
        diffSummariesAction.setBase(this);
        commitProceduralPromptAction.setBase(this);
        commitDeclarativePromptAction.setBase(this);
        acceptModifiedCodeAction.setBase(this);

        actionGroup.add(retrieveSummaryAction);
        actionGroup.add(diffSummariesAction);
        actionGroup.add(commitProceduralPromptAction);
        actionGroup.addSeparator();
        actionGroup.add(commitDeclarativePromptAction);
        actionGroup.addSeparator();
        actionGroup.add(acceptModifiedCodeAction);

        if (actionToolbar == null) {
            actionToolbar = ActionManager.getInstance().createActionToolbar("llm-modification", actionGroup, true);
            actionToolbar.setTargetComponent(this);
            setToolbar(actionToolbar.getComponent());
        } else {
            actionToolbar.updateActionsAsync();
        }
    }

    private void initializeContent() {
        contentPanel = new JBPanelWithEmptyText(new GridLayout(3, 1));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Content content = ContentFactory.getInstance().createContent(contentPanel, "", false);
        setContent(content.getComponent());

        createInputArea();
        createCodeEditor();
    }

    private void createInputArea() {
        // Setting up the procedural text pane
        proceduralTextPane = new JTextPane();
        proceduralTextPane.setBackground(JBColor.WHITE);
        proceduralTextPane.setFont(new Font("Monospaced", Font.PLAIN, 14));

        // Setup diff text pane with HTML content
        diffTextPane = new JTextPane();
        diffTextPane.setContentType("text/html");
        diffTextPane.setEditable(false);
        diffTextPane.setBackground(JBColor.WHITE);

        // Card layout to toggle between text displays
        cardLayout = new CardLayout();
        cardPanel = new JBPanel<>(cardLayout);
        cardPanel.add(proceduralTextPane, "Plain Text");
        cardPanel.add(diffTextPane, "Rich Text");

        // Setting up the declarative text pane
        declarativeTextPane = new JTextPane();
        declarativeTextPane.setBackground(JBColor.WHITE);
        declarativeTextPane.setFont(new Font("Monospaced", Font.PLAIN, 14));

        // Setting up the procedural panel
        JBPanelWithEmptyText proceduralPanel = new JBPanelWithEmptyText(new BorderLayout());
        proceduralPanel.add(createJBLabelOfFont14("Summary-Mediated Prompt"), BorderLayout.NORTH);
        JBScrollPane cardScrollPane = new JBScrollPane(cardPanel);
        cardScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        proceduralPanel.add(cardScrollPane, BorderLayout.CENTER);

        // Setting up the declarative panel
        JBPanelWithEmptyText declarativePanel = new JBPanelWithEmptyText(new BorderLayout());
        declarativePanel.add(createJBLabelOfFont14("Direct Instruction Prompt"), BorderLayout.NORTH);
        JBScrollPane declarativeScrollPane = new JBScrollPane(declarativeTextPane);
        declarativeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        declarativePanel.add(declarativeScrollPane, BorderLayout.CENTER);

        // Add the procedural and declarative panels to the content panel
        contentPanel.add(proceduralPanel);
        contentPanel.add(declarativePanel);
    }

    private void createCodeEditor() {
        // Credit: https://github.com/carlrobertoh/CodeGPT/blob/445b71184c7d5c4abd9b5228d8f9c0bd656102cc/src/main/kotlin/ee/carlrobert/codegpt/ui/textarea/CodePreviewTooltipContent.kt
        String ideName = ApplicationInfo.getInstance().getVersionName();

        // TODO: This is a temporary implementation. A better approach is needed to dynamically create new code editors based on file type. Implement this in the future.
        FileType fileType;
        System.out.println(ideName);
        if (ideName.contains("PyCharm")) {
            fileType = FileTypeManager.getInstance().getFileTypeByExtension("py");
        } else if (ideName.contains("WebStorm")) {
            fileType = FileTypeManager.getInstance().getFileTypeByExtension("js");
        } else if (ideName.contains("IntelliJ IDEA")) {
            fileType = FileTypeManager.getInstance().getFileTypeByExtension("java");
        } else if (ideName.contains("CLion")) {
            fileType = FileTypeManager.getInstance().getFileTypeByExtension("cpp");
        } else {
            fileType = FileTypes.PLAIN_TEXT;
        }

        codeDocument = EditorFactory.getInstance().createDocument("");
        EditorFactory editorFactory = EditorFactory.getInstance();
        Editor codeEditor = editorFactory.createEditor(codeDocument, project, fileType, false);
        EditorSettings settings = codeEditor.getSettings();
        settings.setLineNumbersShown(false);
        settings.setFoldingOutlineShown(false);
        settings.setLineMarkerAreaShown(false);

        JBPanelWithEmptyText codePanel = new JBPanelWithEmptyText(new BorderLayout());
        codePanel.add(createJBLabelOfFont14("Modified Code"), BorderLayout.NORTH);
        codePanel.add(codeEditor.getComponent(), BorderLayout.CENTER);
        contentPanel.add(codePanel);
    }

    private JBLabel createJBLabelOfFont14(String text) {
        JBLabel label = new JBLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        return label;
    }

    public void refresh() {
        setOriginalSummary("");
        getProceduralTextPane().setText("");
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

    public JTextPane getProceduralTextPane() {
        return proceduralTextPane;
    }

    public JTextPane getDeclarativeTextPane() {
        return declarativeTextPane;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
