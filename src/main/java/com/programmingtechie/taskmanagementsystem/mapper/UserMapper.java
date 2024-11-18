package com.programmingtechie.taskmanagementsystem.mapper;

import com.programmingtechie.taskmanagementsystem.dto.AuthenticationDTO;
import com.programmingtechie.taskmanagementsystem.model.UserImpl;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ModelMapper modelMapper;

    public UserImpl convertToUserImpl(AuthenticationDTO authenticationDTO) {
        return modelMapper.map(authenticationDTO, UserImpl.class);
    }
}
