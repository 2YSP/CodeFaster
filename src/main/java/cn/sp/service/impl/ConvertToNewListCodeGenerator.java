package cn.sp.service.impl;

import cn.sp.constant.CodeConstants;
import cn.sp.enums.ActionTypeEnum;
import cn.sp.exception.ShipException;
import cn.sp.model.GenerateCodeInfo;
import cn.sp.model.GenerateContext;
import cn.sp.service.CodeGenerator;
import cn.sp.service.CodeGeneratorCommonService;
import cn.sp.util.CodeUtils;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * @Author: Ship
 * @Description:
 * @Date: Created in 2024/11/4
 */
public class ConvertToNewListCodeGenerator implements CodeGenerator {

    private final CodeGeneratorCommonService commonService = new CodeGeneratorCommonServiceImpl();

    @Override
    public void doGenerate(Project project, DataContext dataContext, PsiFile psiFile) {
        GenerateContext generateContext = commonService.buildGenerateContext(project, dataContext, psiFile);
        if (!CodeUtils.isJava8OrHigher(generateContext)) {
            throw new ShipException("only support Java8 or higher!");
        }
        GenerateCodeInfo codeInfo = getGenerateCodeInfo(project, dataContext, psiFile, generateContext);
        generateCode(generateContext, codeInfo);
    }


    /**
     * 获取代码生成需要的一些信息
     *
     * @param project
     * @param dataContext
     * @param psiFile
     * @return
     */
    private GenerateCodeInfo getGenerateCodeInfo(Project project, DataContext dataContext, PsiFile psiFile,
                                                 GenerateContext generateContext) {
        String fieldName = getFieldName(project, dataContext, psiFile);
        PsiTypeElement psiTypeElement = CodeUtils.getPsiTypeElement(generateContext.getPsiElement());
        // java.util.List<java.lang.Integer>
        String canonicalText = psiTypeElement.getType().getCanonicalText();
        if (!CodeUtils.isLegal(canonicalText)) {
            throw new ShipException("usage error");
        }
        String className = CodeUtils.parseGenericTypeName(canonicalText);
        GenerateCodeInfo generateCodeInfo = new GenerateCodeInfo();
        generateCodeInfo.setClassName(className);
        generateCodeInfo.setFieldName(fieldName);

        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        PsiClass psiClass = javaPsiFacade.findClass(className, GlobalSearchScope.allScope(project));
        generateCodeInfo.setSimpleClassName(psiClass.getName());
        PsiField psiField = CodeUtils.getField(psiClass, fieldName);
        // 校验是否具备该字段
        if (psiField == null) {
            throw new ShipException("Invalid field name!");
        }
        generateCodeInfo.setFieldType(psiField.getType().getPresentableText());

        String getterMethodName = CodeUtils.getGetterMethodName(fieldName);
        generateCodeInfo.setFieldGetterMethodName(getterMethodName);

        PsiIdentifier psiIdentifier = CodeUtils.getPsiIdentifierElement(generateContext.getPsiElement());
        String variableName = psiIdentifier.getText();
        generateCodeInfo.setVariableName(variableName);
        // 字段名+List,如 idList
        generateCodeInfo.setNewVariableName(fieldName + "List");
        return generateCodeInfo;
    }


    /**
     * 获取输入的字段名
     *
     * @param project
     * @param dataContext
     * @param psiFile
     * @return
     */
    private String getFieldName(Project project, DataContext dataContext, PsiFile psiFile) {
        String fieldName = Messages.showInputDialog(project, "Which field do you want to use for convert new list?", "Input the field name", Messages.getQuestionIcon());
        return fieldName;
    }

    /**
     * 生成代码
     *
     * @param generateContext
     * @param codeInfo
     */
    private void generateCode(GenerateContext generateContext, GenerateCodeInfo codeInfo) {
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
        return ActionTypeEnum.TO_NEW_LIST;
    }
}