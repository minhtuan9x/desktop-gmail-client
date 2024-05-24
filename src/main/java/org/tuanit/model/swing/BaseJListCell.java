package org.tuanit.model.swing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseJListCell<E> {
    private String id;
    private String showValue;
    private E e;
}
