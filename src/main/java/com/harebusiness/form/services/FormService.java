package com.harebusiness.form.services;

import com.harebusiness.form.dtos.request.CreateFormRequestDto;
import com.harebusiness.form.dtos.response.CreateFormResponseDto;

public interface FormService {

    CreateFormResponseDto createForm(CreateFormRequestDto request, Long userId);
}
