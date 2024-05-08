package com.ec.common.utils.bean;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

/**
 * bean对象属性验证
 */
public class BeanValidators {
    /**
     * 使用指定的validator对象并根据指定的分组对传入的对象进行验证
     * @param validator
     * @param object
     * @param groups
     * @throws ConstraintViolationException
     */
    public static void validateWithException(Validator validator, Object object, Class<?>... groups)
            throws ConstraintViolationException {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }
}
