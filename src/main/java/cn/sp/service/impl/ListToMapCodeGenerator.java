package cn.sp.service.impl;

import cn.sp.constant.CodeConstants;
import cn.sp.enums.ActionTypeEnum;
import cn.sp.model.GenerateCodeInfo;
import cn.sp.model.GenerateContext;
import cn.sp.util.CodeUtils;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

/**
 * @Author: Ship
 * @Description:
 * @Date: Created in 2024/11/2
 */
public class ListToMapCodeGenerator extends AbstractCodeGenerator {

    @Override
    String getFieldName(Project project, DataContext dataContext, PsiFile psiFile) {
        String fieldName = Messages.showInputDialog(project, "What is the field name of map key?", "Input the field name", Messages.getQuestionIcon());
        System.out.println("================" + fieldName);
        return fieldName;
    }

    @Override
    protected void generateCode(GenerateContext generateContext, GenerateCodeInfo codeInfo) {
        Application application = ApplicationManager.getApplication();
        String splitText = CodeUtils.extractSplitText(generateContext.getPsiMethod(), generateContext.getDocument());
        String codeLine = this.buildCodeLine(codeInfo, splitText);
        int lineStartOffset = generateContext.getDocument().getLineEndOffset(generateContext.getLineNumber());
        application.runWriteAction(() -> {
            WriteCommandAction.runWriteCommandAction(generateContext.getProject(), () -> {
                PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(generateContext.getProject());
                generateContext.getDocument().insertString(lineStartOffset, codeLine);
                psiDocumentManager.commitDocument(generateContext.getDocument());
            });
        });
    }


    /**
     * 构建代码
     *
     * @param codeInfo
     * @return
     */
    private String buildCodeLine(GenerateCodeInfo codeInfo, String splitText) {
        String template = CodeConstants.CODE_TEMPLATE_MAP.get(actionType());
        return splitText + "" + template.replace("$fieldType", codeInfo.getFieldType())
                .replace("$className", codeInfo.getClassName())
                .replace("$simpleClassName", codeInfo.getSimpleClassName())
                .replace("$newVariableName", codeInfo.getNewVariableName())
                .replace("$variableName", codeInfo.getVariableName())
                .replace("$fieldGetterMethodName", codeInfo.getFieldGetterMethodName());
    }

    @Override
    public ActionTypeEnum actionType() {
        return ActionTypeEnum.LIST_TO_MAP;
    }
}
