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


    public static final String LIST_CLASS_NAME = "java.util.List";

    /**
     * 泛型开始符号
     */
    public static final String GENERIC_START_SYMBOL = "<";

    /**
     * 泛型结束符号
     */
    public static final String GENERIC_END_SYMBOL = ">";

    public static final String GENERATE_SETTER_METHOD = "Generate all setter with default value";
    public static final String GENERATE_BUILDER_METHOD = "Generate builder chain call";
    public static final String GENERATE_ACCESSORS_METHOD = "Generate accessors chain call";
    public static final String GENERATE_SETTER_METHOD_NO_DEFAULT_VALUE = "Generate all setter no default value";
    public static final String ASSERT_ALL_PROPS = "Assert all getters";
    public static final String ASSERT_NOT_NULL = "Assert is not null";
    public static final String GENERATE_CONVERTER_FROM_METHOD = "Generate setter getter converter";
    public static final String BUILDER_CONVERTER_FROM_METHOD = "Generate builder getter converter";
    public static final String GENERATE_CONVERTER_FROM_VARIABLE = "Generate setter getter converter from variable";
    public static final String BUILDER_METHOD_NAME = "builder";
    public static final String GENERATE_GETTER_METHOD = "Generate all getter";

    public static final String GENERATE_SETTER_METHOD_NO_DEAULT_VALUE = "";

    public static final String INDENTATION_CHARACTER_TAB = "\t";
    public static final String INDENTATION_CHARACTER_4_SPACE = "    ";


    /**
     * 代码模板map
     */
    public static final Map<ActionTypeEnum,String> CODE_TEMPLATE_MAP = new HashMap<>();

    static {
        String str = "Map<$fieldType,$simpleClassName> $newVariableName = $variableName.stream().collect(Collectors.toMap($simpleClassName::$fieldGetterMethodName, r -> r));";
        CODE_TEMPLATE_MAP.put(ActionTypeEnum.LIST_TO_MAP,str);

//        List<GenerateCodeInfo> list = new ArrayList<>();
//        Map<String, GenerateCodeInfo> map = list.stream().collect(Collectors.toMap(GenerateCodeInfo::getFieldName, r -> r));
    }
}
