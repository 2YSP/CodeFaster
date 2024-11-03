package cn.sp.service;

import cn.sp.model.GenerateCodeInfo;
import cn.sp.model.GenerateContext;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

/**
 * @Author: Ship
 * @Description:
 * @Date: Created in 2024/11/3
 */
public interface CodeGeneratorCommonService {

    /**
     * 构建上下文信息
     *
     * @param project
     * @param dataContext
     * @param psiFile
     * @return
     */
    GenerateContext buildGenerateContext(Project project, DataContext dataContext, PsiFile psiFile);



}
