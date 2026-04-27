package com.harebusiness.form.services;

import com.harebusiness.form.dtos.request.AddQuestionRequestDto;
import com.harebusiness.form.dtos.response.AddQuestionResponseDto;
import com.harebusiness.form.models.User;

public interface QuestionService {

    AddQuestionResponseDto addQuestion(String slug, AddQuestionRequestDto request, User user);
}
