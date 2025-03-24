package entities;

import actions.*;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
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
import java.lang.reflect.Method;

public class MySimpleToolWindowPanel extends SimpleToolWindowPanel {
    private static MySimpleToolWindowPanel instance;
    private final ToggleableLoadingAction loadingAction = new ToggleableLoadingAction();
    private final Project project;
    private Editor editor;
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
    private String filePath;
    private TextRange selectedRange;

    MySimpleToolWindowPanel(boolean vertical, Project project) {
        super(vertical);
        this.project = project;
        initializeActionToolbar();
        initializeContent();
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

        retrieveSummaryAction.setBase(this);
        diffSummariesAction.setBase(this);
        commitProceduralPromptAction.setBase(this);
        commitDeclarativePromptAction.setBase(this);

        actionGroup.add(loadingAction);
        actionGroup.addSeparator();
        actionGroup.add(new MyToolbarLabelAction("Summary"));
        actionGroup.add(retrieveSummaryAction);
        actionGroup.add(diffSummariesAction);
        actionGroup.add(commitProceduralPromptAction);
        actionGroup.addSeparator();
        actionGroup.add(new MyToolbarLabelAction("Direct"));
        actionGroup.add(commitDeclarativePromptAction);

        if (actionToolbar == null) {
            actionToolbar = ActionManager.getInstance().createActionToolbar("llm-modification", actionGroup, true);
            actionToolbar.setTargetComponent(this);
            setToolbar(actionToolbar.getComponent());
        } else {
            actionToolbar.updateActionsAsync();
        }
    }

    private void initializeContent() {
        contentPanel = new JBPanelWithEmptyText(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(contentPanel);
        createNotification();
        createInputArea();
    }

    private void createNotification() {
        JBPanelWithEmptyText notificationPanel = new JBPanelWithEmptyText(new BorderLayout());
        JBLabel notificationLabel = new JBLabel("<html><i><u>Please use our tool as much as possible instead of manual editing.</u></i></html>");
        notificationLabel.setFont(new Font("Arial", Font.PLAIN, 12));
//        notificationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        notificationLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        notificationPanel.add(notificationLabel, BorderLayout.NORTH);
        contentPanel.add(notificationPanel, BorderLayout.NORTH);
    }

    private void createInputArea() {
        // Create a panel to hold the procedural and declarative text panes
        JBPanelWithEmptyText inputPanel = new JBPanelWithEmptyText(new GridLayout(2, 1));
        contentPanel.add(inputPanel, BorderLayout.CENTER);

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
        inputPanel.add(proceduralPanel);
        inputPanel.add(declarativePanel);
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
        getDeclarativeTextPane().setText("");
        getDiffTextPane().setText("");
        if (getDiffTextPane().isVisible()) {
            getCardLayout().next(getCardPanel());
        }
    }

    public void refreshActions() {
        try {
            Method method = ActionToolbar.class.getMethod("updateActionsAsync");
            method.invoke(actionToolbar);
        } catch (NoSuchMethodException e) {
            actionToolbar.updateActionsImmediately(); // Fallback for older versions
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public Project getProject() {
        return project;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }

    public Editor getEditor() {
        return editor;
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

    public ToggleableLoadingAction getLoadingAction() {
        return loadingAction;
    }

}
