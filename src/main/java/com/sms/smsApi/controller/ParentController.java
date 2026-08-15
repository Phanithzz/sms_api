package com.sms.smsApi.controller;


import com.sms.smsApi.dto.requestDto.*;
import com.sms.smsApi.model.Parent;
import com.sms.smsApi.service.ParentService.ParentService;
import com.sms.smsApi.service.StudentService.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/parents")
public class ParentController {

    private final ParentService parentService;

    public ParentController(ParentService parentService) {
        this.parentService = parentService;
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody ParentSearchFilter request) {
        return ResponseEntity.ok(parentService.getParent(request));
    }

    //    @PostMapping("/create")
//    public ResponseEntity<?> create(@RequestBody StudentCreateDto dto) {
//
//    }
    @PutMapping( "/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody ParentRequest parent) throws IOException {
        Parent updatedParent = parentService.updateParent(id, parent);
        return ResponseEntity.ok(updatedParent);
    }


    @GetMapping
    public List<ParentResponse> findAll() {
        return parentService.findAll();
    }

    @GetMapping("/{id}")
    public ParentResponse findById(@PathVariable String id) {
        return parentService.findById(id);
    }

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public StudentResponse create(@Valid @RequestBody StudentRequest request) {
//        return service.create(request);
//    }

//    @PatchMapping("/{id}/status")
//    public StudentResponse updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
//        return parentService.updateStatus(id, StudentStatus.valueOf(body.get("status")));
//    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        parentService.delete(id);
    }
}
