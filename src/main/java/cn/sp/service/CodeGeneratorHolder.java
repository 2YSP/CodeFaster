package cn.sp.service;

import cn.sp.enums.ActionTypeEnum;
import cn.sp.exception.ShipException;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Ship
 * @Description:
 * @Date: Created in 2024/11/2
 */
public class CodeGeneratorHolder {
    /**
     * 实例缓存map
     */
    private static final Map<ActionTypeEnum, CodeGenerator> CODE_GENERATOR_MAP = new ConcurrentHashMap<>();

    private static CodeGenerator getCodeGeneratorFromLoader(ActionTypeEnum actionTypeEnum){
        ServiceLoader<CodeGenerator> loader = ServiceLoader.load(CodeGenerator.class);
        for (CodeGenerator codeGenerator : loader) {
            if (codeGenerator.actionType().equals(actionTypeEnum)){
                return codeGenerator;
            }
        }
        throw new ShipException("CodeGenerator not exist!");
    }

    /**
     * 获取代码生成器
     *
     * @param actionTypeEnum
     * @return
     */
    public static CodeGenerator get(ActionTypeEnum actionTypeEnum) {
        return CODE_GENERATOR_MAP.computeIfAbsent(actionTypeEnum, (k) -> getCodeGeneratorFromLoader(actionTypeEnum));
    }
}
