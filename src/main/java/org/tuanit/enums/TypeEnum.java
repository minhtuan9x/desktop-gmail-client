package org.tuanit.enums;

public enum TypeEnum {
    NHOM("Nhắn thành viên nhóm"),
    EXCEL("Nhắn SĐT"),
    DANHBA("Nhắn danh bạ"),
    GROUP("Nhắn nhóm");

    private final String value;

    TypeEnum(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
