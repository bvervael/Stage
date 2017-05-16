/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package koalytograph;

import javafx.util.StringConverter;

class IntegerStringConverter extends StringConverter<Number> {

    public IntegerStringConverter() {
    }

    @Override
    public String toString(Number object) {
        if (object.intValue() != object.doubleValue()) {
            return "";
        }
        return "" + (object.intValue());
    }

    @Override
    public Number fromString(String string) {
        Number val = Double.parseDouble(string);
        return val.intValue();
    }
}
