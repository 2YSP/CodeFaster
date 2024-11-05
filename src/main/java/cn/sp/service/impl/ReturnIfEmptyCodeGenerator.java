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
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.source.codeStyle.CodeStyleManagerImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: Ship
 * @Description:
 * @Date: Created in 2024/11/5
 */
public class ReturnIfEmptyCodeGenerator implements CodeGenerator {

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


    private GenerateCodeInfo getGenerateCodeInfo(Project project, DataContext dataContext, PsiFile psiFile, GenerateContext generateContext) {
        PsiTypeElement psiTypeElement = CodeUtils.getPsiTypeElement(generateContext.getPsiElement());
        // java.util.List<java.lang.Integer>
        String canonicalText = psiTypeElement.getType().getCanonicalText();
        if (!CodeUtils.containList(canonicalText)) {
            throw new ShipException("usage error");
        }
        PsiIdentifier psiIdentifier = CodeUtils.getPsiIdentifierElement(generateContext.getPsiElement());
        String variableName = psiIdentifier.getText();
        GenerateCodeInfo generateCodeInfo = new GenerateCodeInfo();
        generateCodeInfo.setVariableName(variableName);
        return generateCodeInfo;
    }

    private void generateCode(GenerateContext generateContext, GenerateCodeInfo codeInfo) {
        Application application = ApplicationManager.getApplication();
        String splitText = CodeUtils.extractSplitText(generateContext.getPsiMethod(), generateContext.getDocument());
        String indentText = CodeUtils.extractIndentText(generateContext.getPsiMethod(), generateContext.getDocument());
        boolean hasGuava = CodeUtils.checkGuavaExist(generateContext.getProject(), generateContext.getPsiElement());
        System.out.println("hasGuava:" + hasGuava);
        String codeLine = this.buildCodeLine(codeInfo, splitText, indentText, hasGuava);
        int lineStartOffset = generateContext.getDocument().getLineEndOffset(generateContext.getLineNumber());
        application.runWriteAction(() -> {
            WriteCommandAction.runWriteCommandAction(generateContext.getProject(), () -> {
                PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(generateContext.getProject());
                generateContext.getDocument().insertString(lineStartOffset, codeLine);
                psiDocumentManager.commitDocument(generateContext.getDocument());
            });
        });
        // 自动导包
        Set<String> importSet = Arrays.asList(
                "org.springframework.util.CollectionUtils")
                .stream().collect(Collectors.toSet());
        if (hasGuava) {
            importSet.add("com.google.common.collect.Lists");
        }
        application.runWriteAction(() -> {
            WriteCommandAction.runWriteCommandAction(generateContext.getProject(), () -> {
                PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(generateContext.getProject());
                CodeUtils.addImportToFile(psiDocumentManager, (PsiJavaFile) generateContext.getPsiFile(), generateContext.getDocument(), importSet);
            });
        });
    }


    /**
     * 构建代码
     *
     * @param codeInfo
     * @return
     */
    private String buildCodeLine(GenerateCodeInfo codeInfo, String splitText, String indentText, boolean hasGuava) {
        String template = CodeConstants.CODE_TEMPLATE_MAP.get(actionType());
        // Lists.newArrayList()
        String emptyList = hasGuava ? "Lists.newArrayList()" : "new ArrayList<>()";
        return template.replace("${splitText}", splitText)
                .replace("${indentText}", indentText)
                .replace("$emptyList", emptyList)
                .replace("$variableName", codeInfo.getVariableName());
    }


    @Override
    public ActionTypeEnum actionType() {
        return ActionTypeEnum.RETURN_IF_EMPTY;
    }
}
