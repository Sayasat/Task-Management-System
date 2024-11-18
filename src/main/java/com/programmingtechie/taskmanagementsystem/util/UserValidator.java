package com.programmingtechie.taskmanagementsystem.util;

import com.programmingtechie.taskmanagementsystem.model.UserImpl;
import com.programmingtechie.taskmanagementsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class UserValidator implements Validator {

    private final UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserImpl.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserImpl userImpl = (UserImpl) target;

        if(userRepository.findByEmail(userImpl.getEmail()).isPresent()){
            errors.rejectValue("email", "", "Email is already in use");
        }
    }
}
