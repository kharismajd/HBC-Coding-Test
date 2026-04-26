package com.harebusiness.form.services;

import com.harebusiness.form.constants.ResponseMessageConstant;
import com.harebusiness.form.dtos.request.CreateFormRequestDto;
import com.harebusiness.form.dtos.response.CreateFormResponseDto;
import com.harebusiness.form.dtos.response.GetAllFormsResponseDto;
import com.harebusiness.form.exceptions.UserNotFoundException;
import com.harebusiness.form.models.AllowedDomain;
import com.harebusiness.form.models.Form;
import com.harebusiness.form.models.User;
import com.harebusiness.form.repositories.AllowedDomainRepository;
import com.harebusiness.form.repositories.FormRepository;
import com.harebusiness.form.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class FormServiceImpl implements FormService {

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private AllowedDomainRepository allowedDomainRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public CreateFormResponseDto createForm(CreateFormRequestDto request, User user) {
        Form form = new Form();
        form.setName(request.getName());
        form.setSlug(request.getSlug());
        form.setDescription(request.getDescription());
        form.setLimitOneResponse(request.isLimitOneResponse());
        form.setCreator(user);
        Form savedForm = formRepository.save(form);

        List<AllowedDomain> allowedDomains = new ArrayList<>();
        for (String domain : request.getAllowedDomains()) {
            AllowedDomain allowedDomain = new AllowedDomain();
            allowedDomain.setDomain(domain);
            allowedDomain.setForm(form);
            allowedDomains.add(allowedDomain);
        }
        allowedDomainRepository.saveAll(allowedDomains);

        CreateFormResponseDto createFormResponseDto = new CreateFormResponseDto();
        createFormResponseDto.setMessage(ResponseMessageConstant.CREATE_FORM_SUCCESS_MESSAGE);

        CreateFormResponseDto.FormDataResponse responseForm = new CreateFormResponseDto.FormDataResponse();
        responseForm.setId(savedForm.getId());
        responseForm.setName(savedForm.getName());
        responseForm.setSlug(savedForm.getSlug());
        responseForm.setDescription(savedForm.getDescription());
        responseForm.setLimitOneResponse(savedForm.isLimitOneResponse());
        responseForm.setCreatorId(user.getId());
        createFormResponseDto.setForm(responseForm);

        return createFormResponseDto;
    }

    @Transactional
    public GetAllFormsResponseDto getAllForms(User user) {
        List<Form> forms = formRepository.findAllByCreatorIdOrderByIdDesc(user.getId());

        List<GetAllFormsResponseDto.FormDataResponse> formDtos = forms.stream()
                .map(form -> {
                    GetAllFormsResponseDto.FormDataResponse dto = new GetAllFormsResponseDto.FormDataResponse();
                    dto.setId(form.getId());
                    dto.setName(form.getName());
                    dto.setSlug(form.getSlug());
                    dto.setDescription(form.getDescription());

                    dto.setLimitOneResponse(form.isLimitOneResponse() ? 1 : 0);

                    dto.setCreatorId(form.getCreator().getId());
                    return dto;
                }).toList();

        GetAllFormsResponseDto response = new GetAllFormsResponseDto();
        response.setMessage(ResponseMessageConstant.GET_ALL_FORMS_SUCCESS_MESSAGE);
        response.setForms(formDtos);

        return response;
    }
}
