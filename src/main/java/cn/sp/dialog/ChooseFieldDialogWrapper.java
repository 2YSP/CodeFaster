package cn.sp.dialog;

import cn.sp.constant.CodeConstants;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiField;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.ListSpeedSearch;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Ship
 * @Description:
 * @Date: Created in 2024/11/9
 */
public class ChooseFieldDialogWrapper extends DialogWrapper {

    private JBList<PsiField> list;

    private String title;

    private DefaultListModel<PsiField> defaultListModel;

    private PsiField[] fields;

    public ChooseFieldDialogWrapper(String title, PsiField[] fields) {
        // use current window as parent
        super(true);
        this.title = title;
        this.fields = fields;
        init();
        setTitle(title);

    }

    @Override
    protected @Nullable
    JComponent createCenterPanel() {
        defaultListModel = new DefaultListModel<>();
        for (PsiField psiField : fields) {
            defaultListModel.addElement(psiField);
        }
        list = new JBList<PsiField>(defaultListModel);

        // 修饰每一行的元素
        ColoredListCellRenderer<PsiField> coloredListCellRenderer = new ColoredListCellRenderer<PsiField>() {
            @Override
            protected void customizeCellRenderer(@NotNull JList<? extends PsiField> list, PsiField psiField, int index, boolean selected, boolean hasFocus) {
                setIcon(IconLoader.findIcon("/icons/field.svg"));
                setIconOnTheRight(false);
                String text = getText(psiField);
                if (selected) {
                    append(text, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
                } else {
                    append(text, SimpleTextAttributes.REGULAR_ATTRIBUTES);
                }
            }
        };
        list.setCellRenderer(coloredListCellRenderer);

        // 触发快速查找
        new ListSpeedSearch<>(list);
        // 默认选中第一个 还有bug暂时注释
//        list.setSelectedIndex(0);

        // 增加工具栏（新增按钮、删除按钮、上移按钮、下移按钮）
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(list);
        decorator.disableAddAction();
        decorator.disableRemoveAction();
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(decorator.createPanel(), BorderLayout.CENTER);
        return panel;
    }

    private String getText(PsiField psiField) {
        String typeText = psiField.getType().getPresentableText();
        return CodeConstants.INDENTATION_CHARACTER_4_SPACE + psiField.getName() + ":" + typeText;
    }

    public PsiField getSelectedValue() {
        return list.getSelectedValue();
    }
}
