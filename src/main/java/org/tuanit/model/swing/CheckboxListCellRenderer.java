package org.tuanit.model.swing;

import org.tuanit.model.swing.BaseJListCell;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CheckboxListCellRenderer<E> extends JCheckBox implements ListCellRenderer<BaseJListCell<E>> {

    private static final long serialVersionUID = 3734536442230283966L;

    public List<String> filtered = new ArrayList<>();

    @Override
    public Component getListCellRendererComponent(JList<? extends BaseJListCell<E>> list, BaseJListCell<E> value, int index, boolean isSelected, boolean cellHasFocus) {
        setComponentOrientation(list.getComponentOrientation());

        setFont(list.getFont());
        setText(String.format("%4s", index + 1) + ". " + value.getShowValue());

        setBackground(list.getBackground());
        setForeground(list.getForeground());

        // Check if the item is a filtered item
        if (!filtered.isEmpty()) {
            boolean isFiltered = filtered.contains(value.getId());

            if (isFiltered) {
                setBackground(Color.YELLOW);
            } else {
                setBackground(list.getBackground());
            }
        }
        setSelected(isSelected);
        setEnabled(list.isEnabled());

        return this;
    }
}
