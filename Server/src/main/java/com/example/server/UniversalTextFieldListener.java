package com.example.server;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;


/**
 * class for listening the fields and do not allow to write wrong things
 */
public class UniversalTextFieldListener {
    /**
     * enum for choosing option of field
     */
    public enum RestrictionType {
        NUMBERS_ONLY,
        RUSSIAN_LETTERS_ONLY,
        TXT_FILES_ONLY
    }

    /**
     * realisation logic of listening the fields
     */
    public static class RestrictionListener implements ChangeListener<String> {
        private final RestrictionType restrictionType;

        public RestrictionListener(RestrictionType restrictionType) {
            this.restrictionType = restrictionType;
        }

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            TextField textField = (TextField) ((javafx.beans.property.StringProperty) observable).getBean();
            switch (restrictionType) {
                case NUMBERS_ONLY:
                    if (!newValue.matches("\\d*")) {
                        textField.setText(newValue.replaceAll("[^\\d]", ""));
                    }
                    break;
                case RUSSIAN_LETTERS_ONLY:
                    if (!newValue.matches("[а-яА-ЯёЁ]*")) {
                        textField.setText(newValue.replaceAll("[^а-яА-ЯёЁ]", ""));
                    }
                case TXT_FILES_ONLY:
                    if(!newValue.matches("/.*\\.txt")){
                        textField.setText(newValue.replaceAll("[^/.*\\.txt]", ""));
                    }
                    break;
            }
        }
    }
}
