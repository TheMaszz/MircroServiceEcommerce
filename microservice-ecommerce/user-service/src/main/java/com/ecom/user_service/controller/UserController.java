package com.ecom.user_service.controller;

import com.ecom.user_service.bean.AddressBean;
import com.ecom.user_service.bean.ApiResponse;
import com.ecom.user_service.bean.UserBean;
import com.ecom.user_service.exception.BaseException;
import com.ecom.user_service.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/apiendpoint/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getAll")
    public ApiResponse getAll(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page_number", required = false) int page_number,
            @RequestParam(name = "page_size", required = false) int page_size,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "sort_type", required = false) String sort_type
    ) throws BaseException {
        ApiResponse res = userService.getAllUser(search, page_number, page_size, sort, sort_type);
        return res;
    }

    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Long id) throws BaseException {
        ApiResponse res = userService.getUserById(id);
        return res;
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse update(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestPart("user") String userJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws BaseException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        UserBean userBean = objectMapper.readValue(userJson, UserBean.class);

        ApiResponse res = userService.updateUser(id, userBean, file, request);
        return res;
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse delete(
            HttpServletRequest request,
            @PathVariable Long id
    ) throws BaseException {
        ApiResponse res = userService.deleteUser(id, request);
        return res;
    }

    @PutMapping("/changeRole/{id}")
    public ApiResponse changeRole() throws BaseException {
        ApiResponse res = new ApiResponse();
        return res;
    }

    @GetMapping("/address/my")
    public ApiResponse getMyAddress(
            HttpServletRequest request
    ) throws BaseException {
        ApiResponse res = userService.getMyAddress(request);
        return res;
    }

    @PostMapping("/address/create")
    public ApiResponse createAddress(
            HttpServletRequest request,
            @RequestBody AddressBean addressBean
    ) throws BaseException {
        ApiResponse res = userService.createAddress(request, addressBean);
        return res;
    }

    @PutMapping("/address/update/{id}")
    public ApiResponse updateAddress(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody AddressBean addressBean
    ) throws BaseException {
        ApiResponse res = userService.updateAddress(id, addressBean);
        return res;
    }

}
