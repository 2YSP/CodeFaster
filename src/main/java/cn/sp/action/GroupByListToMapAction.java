package cn.sp.action;

import cn.sp.exception.ShipException;
import cn.sp.service.CodeGenerator;
import cn.sp.service.impl.GroupByListToMapCodeGenerator;
import cn.sp.service.impl.ListToMapCodeGenerator;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;

/**
 * @Author: Ship
 * @Description:
 * @Date: Created in 2024/11/2
 */
public class GroupByListToMapAction extends AnAction {

    private final CodeGenerator codeGenerator = new GroupByListToMapCodeGenerator();

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        try {
            codeGenerator.doGenerate(project, e.getDataContext(), psiFile);
        } catch (ShipException shipException) {
            Messages.showErrorDialog(project, shipException.getMessage(), "Operation Error");
        } catch (Exception exception) {
            exception.printStackTrace();
            Messages.showErrorDialog(project, "system error", "System Error");
        }
    }
}
