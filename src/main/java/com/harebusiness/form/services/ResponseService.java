package com.harebusiness.form.services;

import com.harebusiness.form.dtos.request.SubmitResponseRequestDto;
import com.harebusiness.form.dtos.response.SubmitResponseResponseDto;
import com.harebusiness.form.models.User;

public interface ResponseService {

    SubmitResponseResponseDto submitResponse(String slug, SubmitResponseRequestDto request, User user);
}
