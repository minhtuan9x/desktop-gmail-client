package org.tuanit.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UrlEnum {
    MAIN(
            "https://raw.githubusercontent.com/minhtuan9x/socialstoollog/main/gg",
            "1Z2tpr15KQfC5C8plVP3LLDbpvd6JXLmN",
            "1EZD2yMNlAWxurQL0ImjP0_LCr3Op1nZK",
            "/icon.png"
    ),
    LONG(
            "https://raw.githubusercontent.com/minhtuan9x/socialstoollog/main/long",
            "1i8MVDOQMVGi7Vk9c43-d7EUWi5TepYbl",
            "1I0PDu9VFAorGjHIfr3ZduoY-RYtJVDCi",
            "/icon-long.png"
    ),
    CTV(
            "https://raw.githubusercontent.com/minhtuan9x/socialstoollog/main/thao",
            "1kBwOxTHu_0dnQJPnj3x7gxO1yO8S77uQ",
            "18rzPfpJF9oEsSKRbj2Ps5L8SZ21OoI6C",
            "/zaloicon.png"
    );
    private final String github;
    private final String liteUpdate;
    private final String fullUpdate;
    private final String icon;
}
