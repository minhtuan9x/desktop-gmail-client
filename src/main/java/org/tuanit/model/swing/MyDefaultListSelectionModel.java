package org.tuanit.model.swing;

import javax.swing.*;

public class MyDefaultListSelectionModel extends DefaultListSelectionModel {
    private int i0 = -1;
    private int i1 = -1;

    @Override
    public void setSelectionInterval(int index0, int index1) {
        if (i0 == index0 && i1 == index1) {
            if (getValueIsAdjusting()) {
                setValueIsAdjusting(false);
                setSelection(index0, index1);
            }
        } else {
            i0 = index0;
            i1 = index1;
            setValueIsAdjusting(false);
            setSelection(index0, index1);
        }
    }

    private void setSelection(int index0, int index1) {
        if ((super.isSelectedIndex(index0) && index0 == index1) || (!super.isSelectedIndex(index0) && index0 != index1)) {
            super.removeSelectionInterval(index0, index1);
        } else {
            super.addSelectionInterval(index0, index1);
        }
    }
}
