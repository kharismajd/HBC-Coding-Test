package com.harebusiness.form.services;

import com.harebusiness.form.dtos.request.CreateFormRequestDto;
import com.harebusiness.form.dtos.response.CreateFormResponseDto;
import com.harebusiness.form.dtos.response.GetAllFormsResponseDto;
import com.harebusiness.form.dtos.response.GetFormDetailResponseDto;
import com.harebusiness.form.models.User;

public interface FormService {

    CreateFormResponseDto createForm(CreateFormRequestDto request, User user);

    GetAllFormsResponseDto getAllForms(User user);

    GetFormDetailResponseDto getFormDetail(String slug, User user);
}
