package cn.sp.constant;

import cn.sp.enums.ActionTypeEnum;
import cn.sp.model.GenerateCodeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: Ship
 * @Description:
 * @Date: Created in 2024/11/2
 */
public class CodeConstants {

    /**
     * list类型全类名
     */
    public static final String LIST_CLASS_NAME = "java.util.List";

    /**
     * 泛型开始符号
     */
    public static final String GENERIC_START_SYMBOL = "<";

    /**
     * 泛型结束符号
     */
    public static final String GENERIC_END_SYMBOL = ">";

    public static final String INDENTATION_CHARACTER_TAB = "\t";
    public static final String INDENTATION_CHARACTER_4_SPACE = "    ";


    /**
     * 代码模板map
     */
    public static final Map<ActionTypeEnum, String> CODE_TEMPLATE_MAP = new HashMap<>();

    static {
        String str = "Map<$fieldType,$simpleClassName> $newVariableName = $variableName.stream().collect(Collectors.toMap($simpleClassName::$fieldGetterMethodName, r -> r));";
        CODE_TEMPLATE_MAP.put(ActionTypeEnum.LIST_TO_MAP, str);

        String groupByCodeStr = "Map<$fieldType,List<$simpleClassName>> $newVariableName = $variableName.stream().collect(Collectors.groupingBy($simpleClassName::$fieldGetterMethodName));";
        CODE_TEMPLATE_MAP.put(ActionTypeEnum.GROUP_BY_LIST_TO_MAP, groupByCodeStr);

    }
}
