package cn.sp.service.impl;

import cn.sp.exception.ShipException;
import cn.sp.model.GenerateCodeInfo;
import cn.sp.model.GenerateContext;
import cn.sp.service.CodeGenerator;
import cn.sp.util.CodeUtils;
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
 * @Date: Created in 2024/11/2
 */
public abstract class AbstractCodeGenerator implements CodeGenerator {

    @Override
    public void doGenerate(Project project, DataContext dataContext, PsiFile psiFile) {
        GenerateContext generateContext = buildGenerateContext(project, dataContext, psiFile);
        if (!CodeUtils.isJava8OrHigher(generateContext)){
            throw new ShipException("only support Java8 or higher!");
        }
        GenerateCodeInfo codeInfo = getGenerateCodeInfo(project, dataContext, psiFile, generateContext);
        generateCode(generateContext, codeInfo);
    }

    /**
     * 生成代码
     * @param generateContext
     * @param codeInfo
     */
    protected abstract void generateCode(GenerateContext generateContext, GenerateCodeInfo codeInfo);



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
        String fieldName = null;
        if (needFieldName()) {
            fieldName = getFieldName(project, dataContext, psiFile);
        }
        PsiTypeElement psiTypeElement = CodeUtils.getPsiTypeElement(generateContext.getPsiElement());
        // java.util.List<java.lang.Integer>
        String canonicalText = psiTypeElement.getType().getCanonicalText();
        if (!CodeUtils.isLegal(canonicalText)){
            throw new ShipException("usage error");
        }
        String className = CodeUtils.parseGenericTypeName(canonicalText);
        GenerateCodeInfo generateCodeInfo = new GenerateCodeInfo();
        generateCodeInfo.setClassName(className);
        // todo 校验是否具备该字段
        generateCodeInfo.setFieldName(fieldName);
        String getterMethodName = CodeUtils.getGetterMethodName(fieldName);
        generateCodeInfo.setFieldGetterMethodName(getterMethodName);

        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        PsiClass psiClass = javaPsiFacade.findClass(className, GlobalSearchScope.allScope(project));
//        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(generateContext.getProject())
//                .getClassesByName(className, GlobalSearchScope.projectScope(generateContext.getProject()));
//        PsiClass psiClass = psiClasses[0];

        generateCodeInfo.setSimpleClassName(psiClass.getName());
        PsiField psiField = CodeUtils.getField(psiClass, fieldName);
        generateCodeInfo.setFieldType(psiField.getType().getPresentableText());
        PsiIdentifier psiIdentifier = CodeUtils.getPsiIdentifierElement(generateContext.getPsiElement());
        String variableName = psiIdentifier.getText();
        generateCodeInfo.setVariableName(variableName);
        generateCodeInfo.setNewVariableName(CodeUtils.convertListVariableNameToMap(variableName));
        return generateCodeInfo;
    }


    /**
     * 构建上下文信息
     *
     * @param project
     * @param dataContext
     * @param psiFile
     * @return
     */
    private GenerateContext buildGenerateContext(Project project, DataContext dataContext, PsiFile psiFile) {
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


    /**
     * 默认需要输入字段名，如果不需要重写此方法
     *
     * @return
     */
    protected boolean needFieldName() {
        return true;
    }


    /**
     * 获取字段名
     *
     * @return
     */
    abstract String getFieldName(Project project, DataContext dataContext, PsiFile psiFile);
}
