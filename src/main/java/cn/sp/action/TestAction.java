//package cn.sp.action;
//
//import cn.sp.exception.ShipException;
//import cn.sp.model.GenerateContext;
//import cn.sp.service.CodeGeneratorCommonService;
//import cn.sp.service.impl.CodeGeneratorCommonServiceImpl;
//import cn.sp.util.CodeUtils;
//import com.intellij.openapi.actionSystem.AnAction;
//import com.intellij.openapi.actionSystem.AnActionEvent;
//import com.intellij.openapi.actionSystem.CommonDataKeys;
//import com.intellij.openapi.actionSystem.PlatformDataKeys;
//import com.intellij.openapi.project.Project;
//import com.intellij.psi.JavaPsiFacade;
//import com.intellij.psi.PsiClass;
//import com.intellij.psi.PsiFile;
//import com.intellij.psi.PsiTypeElement;
//import com.intellij.psi.search.GlobalSearchScope;
//
//public class TestAction extends AnAction {
//
//    private final CodeGeneratorCommonService commonService = new CodeGeneratorCommonServiceImpl();
//
//    @Override
//    public void actionPerformed(AnActionEvent e) {
//        Project project = e.getData(PlatformDataKeys.PROJECT);
//        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
//
//        GenerateContext generateContext = commonService.buildGenerateContext(project, e.getDataContext(), psiFile);
//        if (!CodeUtils.isJava8OrHigher(generateContext)) {
//            throw new ShipException("only support Java8 or higher!");
//        }
//        PsiTypeElement psiTypeElement = CodeUtils.getPsiTypeElement(generateContext.getPsiElement());
//        // java.util.List<java.lang.Integer>
//        String canonicalText = psiTypeElement.getType().getCanonicalText();
//        if (!CodeUtils.isLegal(canonicalText)) {
//            throw new ShipException("usage error");
//        }
//        String className = CodeUtils.parseGenericTypeName(canonicalText);
//
//        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
//        PsiClass psiClass = javaPsiFacade.findClass(className, GlobalSearchScope.allScope(project));
//
//        ChooseFieldDialogWrapper wrapper = new ChooseFieldDialogWrapper("请选择你的字段名", psiClass.getFields());
//        if (wrapper.showAndGet()) {
//            System.out.println("===========" + wrapper.getSelectedValue().getName());
//        }
//    }
//
//}
