package com.crowdcoin.mainBoard.Interactive.output;

import com.crowdcoin.mainBoard.Interactive.Field;

public interface OutputField extends Field {

    /**
     * Sets the value of the OutputField
     * @param value the value to display as a String
     */
    void setValue(String value);

}
