package cn.sp.util;

import cn.sp.constant.CodeConstants;
import cn.sp.exception.ShipException;
import cn.sp.model.GenerateContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author: Ship
 * @Description:
 * @Date: Created in 2024/11/2
 */
public class CodeUtils {

    /**
     * 是否为list类型
     *
     * @param className
     * @return
     */
    public static boolean isList(String className) {
        if (className == null) {
            return false;
        }
        return CodeConstants.LIST_CLASS_NAME.equals(className);
    }

    /**
     * 是否包含List
     *
     * @param canonicalText
     * @return
     */
    public static boolean containList(String canonicalText) {
        if (canonicalText == null) {
            return false;
        }
        return canonicalText.startsWith(CodeConstants.LIST_CLASS_NAME);
    }

    /**
     * java.lang.List<java.lang.Integer> 非法
     * java.lang.List<cn.sp.User> 合法
     *
     * @param canonicalText
     * @return
     */
    public static boolean isLegal(String canonicalText) {
        if (!containList(canonicalText)) {
            return false;
        }
        if (!(canonicalText.contains(CodeConstants.GENERIC_START_SYMBOL) &&
                canonicalText.contains(CodeConstants.GENERIC_END_SYMBOL))) {
            return false;
        }
        String className = canonicalText.replace(CodeConstants.LIST_CLASS_NAME, "")
                .replace(CodeConstants.GENERIC_START_SYMBOL, "")
                .replace(CodeConstants.GENERIC_END_SYMBOL, "");
        if (isWrapperType(className)) {
            return false;
        }
        return true;
    }


    /**
     * 解析泛型类名称
     *
     * @param canonicalText
     * @return
     */
    public static String parseGenericTypeName(String canonicalText) {
        String className = canonicalText.replace(CodeConstants.LIST_CLASS_NAME, "")
                .replace(CodeConstants.GENERIC_START_SYMBOL, "")
                .replace(CodeConstants.GENERIC_END_SYMBOL, "");
        return className;
    }

    /**
     * 判断是否为包装类型
     *
     * @param className
     * @return
     */
    public static boolean isWrapperType(String className) {
        Set<String> wrapperTypes = new HashSet<>(8);
        wrapperTypes.add("java.lang.Boolean");
        wrapperTypes.add("java.lang.Character");
        wrapperTypes.add("java.lang.Byte");
        wrapperTypes.add("java.lang.Short");
        wrapperTypes.add("java.lang.Integer");
        wrapperTypes.add("java.lang.Long");
        wrapperTypes.add("java.lang.Float");
        wrapperTypes.add("java.lang.Double");

        // 基本类型对应的包装类名
        String[] primitiveWrapperTypeNames = new String[]{
                "boolean", "char", "byte", "short", "int", "long", "float", "double"
        };
        for (String primitiveWrapperTypeName : primitiveWrapperTypeNames) {
            if (className.equals(primitiveWrapperTypeName)
                    || className.equals("java.lang." + primitiveWrapperTypeName)) {
                return true;
            }
        }
        return wrapperTypes.contains(className);
    }


    public static String getGetterMethodName(String fieldName) {
        String[] words = fieldName.split("\\s+"); // 使用空白字符作为分隔符
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            char firstChar = word.charAt(0);
            char upperCaseFirstChar = Character.toUpperCase(firstChar);
            String updatedWord = upperCaseFirstChar + word.substring(1);
            sb.append(updatedWord).append(" ");
        }
        String result = sb.toString().trim(); // 去除末尾的空格
        System.out.println(result); // 输出: Java Words Capitalize
        return "get" + result;
    }

    /**
     * 获取字段
     *
     * @param psiClass
     * @param name
     * @return
     */
    public static PsiField getField(PsiClass psiClass, String name) {
        for (PsiField psiField : psiClass.getFields()) {
            if (psiField.getName().equals(name)) {
                return psiField;
            }
        }
        return null;
    }

    /**
     * 获取子元素的PsiIdentifier
     *
     * @param element
     * @return
     */
    public static PsiIdentifier getPsiIdentifierElement(PsiElement element) {
        for (PsiElement child : element.getChildren()) {
            if (child instanceof PsiIdentifier) {
                return (PsiIdentifier) child;
            }
        }
        return null;
    }

    /**
     * 获取子元素的PsiTypeElement
     *
     * @param element
     * @return
     */
    public static PsiTypeElement getPsiTypeElement(PsiElement element) {
        for (PsiElement child : element.getChildren()) {
            if (child instanceof PsiTypeElement) {
                return (PsiTypeElement) child;
            }
        }
        return null;
    }

    /**
     * list结构的变量名转map结构的变量名，
     * 如 projectList -> projectMap
     * projects -> projectsMap
     * list -> map
     * @param variableName
     * @return
     */
    public static String convertListVariableNameToMap(String variableName){
        String name = variableName.replace("List", "Map")
                .replace("list", "map");
        if (name.contains("Map") || name.contains("map")){
            return name;
        }
        return name+"Map";
    }


    /**
     * jdk版本是至少1.8
     *
     * @return
     */
    public static boolean isJava8OrHigher(GenerateContext generateContext) {
        if (!(generateContext.getPsiFile() instanceof PsiJavaFile)) {
            throw new ShipException("system error");
        }
        PsiJavaFile psiJavaFile = (PsiJavaFile) generateContext.getPsiFile();
        boolean isJava8OrHigher = psiJavaFile.getLanguageLevel().isAtLeast(LanguageLevel.JDK_1_8);
        return isJava8OrHigher;
    }

    /**
     *  分析当前方法的缩进符, 并追加缩进
     *  为保持原方法功能, 返回值的开头会增加一个"\n"
     * @param method
     * @param document
     * @return
     */
    public static String extractSplitText(PsiMethod method, Document document) {
        int methodStartOffset = method.getTextRange().getStartOffset();
        int lineNumber = document.getLineNumber(methodStartOffset);
        int lineStartOffset = document.getLineStartOffset(lineNumber);
        String currIndentedText = document.getText(new TextRange(lineStartOffset, methodStartOffset));
        return "\n" + indentText(currIndentedText);
    }

    /**
     * 对文本进行缩进
     * @param text
     * @return
     */
    public static String indentText(String text) {
        if (text == null) {
            return CodeConstants.INDENTATION_CHARACTER_4_SPACE;
        }
        return text +
                (text.contains(CodeConstants.INDENTATION_CHARACTER_TAB)
                        ? CodeConstants.INDENTATION_CHARACTER_TAB
                        : CodeConstants.INDENTATION_CHARACTER_4_SPACE);
    }

    /**
     * 获取缩进符
     * @param method
     * @param document
     * @return
     */
    public static String extractIndentText(PsiMethod method, Document document) {
        int methodStartOffset = method.getTextRange().getStartOffset();
        int lineNumber = document.getLineNumber(methodStartOffset);
        int lineStartOffset = document.getLineStartOffset(lineNumber);
        String currIndentedText = document.getText(new TextRange(lineStartOffset, methodStartOffset));
        if (currIndentedText == null) {
            return CodeConstants.INDENTATION_CHARACTER_4_SPACE;
        }
        return currIndentedText.contains(CodeConstants.INDENTATION_CHARACTER_TAB)
                ? CodeConstants.INDENTATION_CHARACTER_TAB : CodeConstants.INDENTATION_CHARACTER_4_SPACE;
    }


    /**
     * 校验是否存在guava包依赖
     * @param project
     * @param element
     * @return
     */
    public static boolean checkGuavaExist(Project project, PsiElement element) {
        @Nullable Module moduleForPsiElement = ModuleUtilCore.findModuleForPsiElement(element);
        if(moduleForPsiElement==null){
            return false;
        }
        PsiClass[] listss = PsiShortNamesCache.getInstance(project).getClassesByName("Lists", GlobalSearchScope.moduleRuntimeScope(moduleForPsiElement, false));
        for (PsiClass psiClass : listss) {
            if (psiClass.getQualifiedName().equals("com.google.common.collect.Lists")){
                return true;
            }
        }
        return false;
    }


    public static void addImportToFile(PsiDocumentManager psiDocumentManager, PsiJavaFile containingFile, Document document, Set<String> newImportList) {
        if (newImportList.size() > 0) {
            Iterator<String> iterator = newImportList.iterator();
            while (iterator.hasNext()) {
                String u = iterator.next();
                if (u.startsWith("java.lang")) {
                    iterator.remove();
                }
            }
        }

        if (newImportList.size() > 0) {
            PsiJavaFile javaFile = containingFile;
            PsiImportStatement[] importStatements = javaFile.getImportList().getImportStatements();
            Set<String> containedSet = new HashSet<>();
            for (PsiImportStatement s : importStatements) {
                containedSet.add(s.getQualifiedName());
            }
            StringBuilder newImportText = new StringBuilder();
            for (String newImport : newImportList) {
                if (!containedSet.contains(newImport)) {
                    newImportText.append("\nimport " + newImport + ";");
                }
            }
            PsiPackageStatement packageStatement = javaFile.getPackageStatement();
            int start = 0;
            if (packageStatement != null) {
                start = packageStatement.getTextLength() + packageStatement.getTextOffset();
            }
            String insertText = newImportText.toString();
            if (StringUtils.isNotBlank(insertText)) {
                document.insertString(start, insertText);
                // 格式化代码
                psiDocumentManager.commitDocument(document);
            }
        }
    }


    public static void main(String[] args) {
        System.out.println(getGetterMethodName("code"));
    }


}
