package com.gitee.hperfect.task.windows;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/1/22 3:19 下午
 */
public class TestWindowsFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        TestWindows testWindows = new TestWindows(project, toolWindow);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(testWindows.getTestPanel(), "displayName", false);
        toolWindow.getContentManager().addContent(content);
    }

}
