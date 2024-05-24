package org.tuanit.enums;

import lombok.Getter;

@Getter
public enum ExecuteStatusEnum {
    DANG_CHAY("Đang chạy"),
    DA_XONG("Đã xong");
    
    private String value;

    ExecuteStatusEnum(String value) {
        this.value = value;
    }

}
