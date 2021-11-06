package com.gitee.hperfect.yapi.action;

import com.gitee.hperfect.utils.MessageUtils;
import com.gitee.hperfect.yapi.model.ApiCat;
import com.gitee.hperfect.yapi.parse.parser.impl.DefaultApiCatParser;
import com.gitee.hperfect.yapi.parse.parser.ApiCatParser;
import com.gitee.hperfect.yapi.service.YapiUploadService;
import com.google.gson.Gson;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/1/21 6:02 下午
 */
public class UploadToYapiAction extends AnAction {


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        Project project = editor.getProject();
        PsiFile psiFile = e.getDataContext().getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            return;
        }
        YapiUploadService uploadService = new YapiUploadService(e.getProject());
        PsiElement referenceAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass selectedClass = (PsiClass) PsiTreeUtil.getContextOfType(referenceAt, new Class[]{PsiClass.class});
        PsiMethod selectedMethod = (PsiMethod) PsiTreeUtil.getContextOfType(referenceAt, new Class[]{PsiMethod.class});
        if (selectedClass != null) {
            //解析api
            ApiCatParser apiCatParser =new DefaultApiCatParser();
            ApiCat cat = apiCatParser.parse(selectedClass, project, selectedMethod);
            //上传到yapi
            try {
                uploadService.upload(cat);
            } catch (Throwable throwable) {
                MessageUtils.error("上传发生错误:" + throwable.getMessage());
            }
        }
    }
}
