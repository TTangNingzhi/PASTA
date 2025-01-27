package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.NlsActions;
import entities.MySimpleToolWindowPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class BaseAction extends AnAction {
    private MySimpleToolWindowPanel base;

    public BaseAction(@Nullable @NlsActions.ActionText String text,
                      @Nullable @NlsActions.ActionDescription String description,
                      @Nullable Icon icon) {
        super(text, description, icon);
    }

    public void setBase(MySimpleToolWindowPanel base) {
        this.base = base;
    }

    public MySimpleToolWindowPanel getBase() {
        return base;
    }

    @Override
    public abstract void actionPerformed(@NotNull AnActionEvent e);
}
