package org.student_api.clinc_system_management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.student_api.clinc_system_management.dto.Request.WaitingListRequestDto;
import org.student_api.clinc_system_management.dto.Response.WaitingListResponseDto;
import org.student_api.clinc_system_management.service.WaitingListService;

@RestController
@RequestMapping("/api/waiting-lists")
@RequiredArgsConstructor
public class WaitingListController {
    private final WaitingListService waitingListService ;

    @PostMapping
    public ResponseEntity<WaitingListResponseDto> joinWaitingList(@Valid @RequestBody WaitingListRequestDto request) {

        WaitingListResponseDto response = waitingListService.joinWaitingList(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
