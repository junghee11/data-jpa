package com.develop.datajpa.controller.admin;


import com.develop.datajpa.request.admin.RecordMatchResultRequest;
import com.develop.datajpa.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @PatchMapping("/baseball/result")
    public Map<String, Object> recordMatchResult(RecordMatchResultRequest request) {
        return adminService.recordMatchResult(request);
    }

    @PatchMapping("/baseball/{id}")
    public Map<String, Object> cancelMatch(@PathVariable long id) {
        return adminService.cancelMatch(id);
    }

}
