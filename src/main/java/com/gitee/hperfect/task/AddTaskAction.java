package com.gitee.hperfect.task;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.gitee.hperfect.task.dialog.AddTaskDialogWrapper;
import org.jetbrains.annotations.NotNull;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/1/22 5:37 下午
 */
public class AddTaskAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new AddTaskDialogWrapper().show();
    }
}
