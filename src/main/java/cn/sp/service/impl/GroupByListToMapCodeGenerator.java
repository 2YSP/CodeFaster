package cn.sp.service.impl;

import cn.sp.enums.ActionTypeEnum;
import cn.sp.exception.ShipException;
import cn.sp.model.GenerateCodeInfo;
import cn.sp.model.GenerateContext;
import cn.sp.service.CodeGenerator;
import cn.sp.service.CodeGeneratorCommonService;
import cn.sp.util.CodeUtils;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

/**
 * @Author: Ship
 * @Description:
 * @Date: Created in 2024/11/2
 */
public class GroupByListToMapCodeGenerator implements CodeGenerator {

    private final CodeGeneratorCommonService commonService = new CodeGeneratorCommonServiceImpl();

    @Override
    public void doGenerate(Project project, DataContext dataContext, PsiFile psiFile) {
        GenerateContext generateContext = commonService.buildGenerateContext(project, dataContext, psiFile);
        if (!CodeUtils.isJava8OrHigher(generateContext)){
            throw new ShipException("only support Java8 or higher!");
        }
        GenerateCodeInfo codeInfo = getGenerateCodeInfo(project, dataContext, psiFile, generateContext);
        this.generateCode(generateContext, codeInfo);
    }

    /**
     * 生成代码
     * @param generateContext
     * @param codeInfo
     */
    private void generateCode(GenerateContext generateContext, GenerateCodeInfo codeInfo) {


    }

    private GenerateCodeInfo getGenerateCodeInfo(Project project, DataContext dataContext, PsiFile psiFile, GenerateContext generateContext) {
        return null;
    }

    @Override
    public ActionTypeEnum actionType() {
        return ActionTypeEnum.GROUP_BY_LIST_TO_MAP;
    }
}
