package cn.sp.service.impl;

import cn.sp.exception.ShipException;
import cn.sp.model.GenerateContext;
import cn.sp.service.CodeGeneratorCommonService;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * @Author: Ship
 * @Description:
 * @Date: Created in 2024/11/3
 */
public class CodeGeneratorCommonServiceImpl implements CodeGeneratorCommonService {


    /**
     * 构建上下文信息
     *
     * @param project
     * @param dataContext
     * @param psiFile
     * @return
     */
    @Override
    public GenerateContext buildGenerateContext(Project project, DataContext dataContext, PsiFile psiFile) {
        GenerateContext generateContext = new GenerateContext();
        generateContext.setProject(project);
        generateContext.setDataContext(dataContext);
        generateContext.setPsiFile(psiFile);

        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        Document document = editor.getDocument();
        generateContext.setEditor(editor);
        generateContext.setDocument(document);
        generateContext.setPsiElement(psiElement);
        PsiElement psiParent = PsiTreeUtil.getParentOfType(psiElement,PsiMethod.class);
        if (psiParent == null) {
            throw new ShipException("使用位置错误");
        }
        if (psiParent instanceof  PsiMethod){
            generateContext.setPsiMethod((PsiMethod) psiParent);
        }
        // 编辑器的位置
        generateContext.setOffset(editor.getCaretModel().getOffset());
        // 编辑器所在行
        generateContext.setLineNumber(document.getLineNumber(generateContext.getOffset()));
        generateContext.setStartOffset(document.getLineStartOffset(generateContext.getLineNumber()));
        generateContext.setEditorText(document.getCharsSequence());

        String clazzName = psiFile.getName().replace(".java", "");
        // 获取类
        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(generateContext.getProject()).getClassesByName(clazzName, GlobalSearchScope.projectScope(generateContext.getProject()));
        generateContext.setPsiClass(psiClasses[0]);
        return generateContext;
    }

}
