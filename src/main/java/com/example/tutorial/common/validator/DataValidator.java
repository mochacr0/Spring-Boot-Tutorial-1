package com.example.tutorial.common.validator;

import com.example.tutorial.common.data.AbstractData;
import com.example.tutorial.exception.InvalidDataException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class DataValidator<D extends AbstractData> {
    public final void validate(D data) {
        log.info("Perform data validation for: " + data.toString());
        if (data == null) {
            throw new InvalidDataException("Data object can't be null");
        }
        ConstraintValidator.validateFields(data);
        validateImpl(data);
    }
    protected abstract void validateImpl(D data);

    protected void validateOnCreate(D data) {

    }
    protected void validateOnUpdate(D data) {

    }

}
