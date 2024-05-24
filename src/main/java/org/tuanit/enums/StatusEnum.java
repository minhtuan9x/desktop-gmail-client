package org.tuanit.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusEnum {
    STARTING("Đang Chờ"),
    PROCESSING("Đang Chuẩn Bị Gửi"),
    DONE("Đã Thực Thi"),
    ERROR("Lỗi");

    private final String value;
}
