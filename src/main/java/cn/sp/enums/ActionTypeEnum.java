package cn.sp.enums;

/**
 * @Author: Ship
 * @Description:
 * @Date: Created in 2024/11/2
 */
public enum ActionTypeEnum {

    LIST_TO_MAP("list_to_map",""),
    /**
     *
     */
    GROUP_BY_LIST_TO_MAP("group_by_list_to_map","");

    private String code;

    private String desc;

    ActionTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
