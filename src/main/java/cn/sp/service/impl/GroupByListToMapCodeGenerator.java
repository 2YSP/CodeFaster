package cn.sp.service.impl;

import cn.sp.enums.ActionTypeEnum;
import cn.sp.model.GenerateCodeInfo;
import cn.sp.model.GenerateContext;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

/**
 * @Author: Ship
 * @Description:
 * @Date: Created in 2024/11/2
 */
public class GroupByListToMapCodeGenerator extends AbstractCodeGenerator {

    @Override
    String getFieldName(Project project, DataContext dataContext, PsiFile psiFile) {
        return null;
    }


    @Override
    protected void generateCode(GenerateContext generateContext, GenerateCodeInfo codeInfo) {

    }

    @Override
    public ActionTypeEnum actionType() {
        return ActionTypeEnum.GROUP_BY_LIST_TO_MAP;
    }
}
