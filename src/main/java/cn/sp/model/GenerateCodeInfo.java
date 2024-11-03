package cn.sp.model;

/**
 * @Author: Ship
 * @Description: 代码生成需要的一些信息
 * @Date: Created in 2024/11/2
 */
public class GenerateCodeInfo {

    /**
     * 全类名
     */
    private String className;
    /**
     * 类名（不包含包名）
     */
    private String simpleClassName;
    /**
     * 属性名
     */
    private String fieldName;

    /**
     * 字段get方法名
     */
    private String fieldGetterMethodName;
    /**
     * 属性类型，如Integer
     */
    private String fieldType;

    /**
     * 变量名，如 projectList
     */
    private String variableName;

    /**
     * 新变量名，如 projectMap
     */
    private String newVariableName;


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }


    public String getFieldGetterMethodName() {
        return fieldGetterMethodName;
    }

    public void setFieldGetterMethodName(String fieldGetterMethodName) {
        this.fieldGetterMethodName = fieldGetterMethodName;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getNewVariableName() {
        return newVariableName;
    }

    public void setNewVariableName(String newVariableName) {
        this.newVariableName = newVariableName;
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public void setSimpleClassName(String simpleClassName) {
        this.simpleClassName = simpleClassName;
    }
}
