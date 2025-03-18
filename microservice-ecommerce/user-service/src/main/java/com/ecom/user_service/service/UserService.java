package com.ecom.user_service.service;

import com.ecom.common.bean.AddressBean;
import com.ecom.common.bean.ApiResponse;
import com.ecom.common.bean.UserBean;
import com.ecom.common.controller.BaseController;
import com.ecom.common.exception.BaseException;
import com.ecom.user_service.exception.UserException;
import com.ecom.user_service.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@Service
public class UserService extends BaseController {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public UserService(UserRepository userRepository, FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    public ApiResponse getAllUser(
            String search,
            int page_number,
            int page_size,
            String sort,
            String sort_type
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        HashMap<String, Object> params = new HashMap<>();
        try {
            if (search != null && !search.isEmpty()) {
                params.put("search", search);
            }
            this.pagination(page_number, page_size, sort, sort_type, params);
            params.put("isCount", false);
            List<UserBean> users = userRepository.findAllUser(params);
            params.put("isCount", true);
            List<UserBean> userCount = userRepository.findAllUser(params);
            res.getPaginate().setLimit(page_size);
            res.getPaginate().setPage(page_number);
            res.getPaginate().setTotal(userCount.size());

            res.setData(users);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse getUserById(Long id) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            UserBean user = userRepository.findUserById(id);
            if (user == null) {
                throw new UserException("not.found", "user not found");
            }
            res.setData(user);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse updateUser(
            Long id,
            UserBean userBean,
            MultipartFile file,
            HttpServletRequest request
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            String userId = request.getHeader("X-User-Id");
            String role = request.getHeader("X-Role");

            userBean.setUpdated_by(Long.valueOf(userId));
            userBean.setId(id);

            UserBean user = userRepository.findUserById(id);
            if(user == null){
                throw new UserException("not.found", "user not found");
            }

            if (file != null && !file.isEmpty()) {
                String originalFilename = file.getOriginalFilename();
                if (originalFilename != null && !originalFilename.isEmpty()) {
                    String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    fileStorageService.saveProfilePicture(file, userBean.getId(), fileExtension);
                    userBean.setProfile_url("/profile_pictures/" + userBean.getId() + fileExtension);
                }
            }

            userRepository.updateUser(userBean);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse deleteUser(
            Long id,
            HttpServletRequest request
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            String userId = request.getHeader("X-User-Id");
            String role = request.getHeader("X-Role");

            UserBean user = userRepository.findUserById(id);
            if(user == null){
                throw new UserException("not.found", "user not found");
            }

            fileStorageService.deleteFile(user.getProfile_url());
            userRepository.deleteUser(id);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse getMyAddress(
            HttpServletRequest request
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        try{
            String userId = request.getHeader("X-User-Id");
            List<AddressBean> address = userRepository.getAddressByUserId(Long.valueOf(userId));

            res.setData(address);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse createAddress(
            HttpServletRequest request,
            AddressBean addressBean
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            String userId = request.getHeader("X-User-Id");
            addressBean.setUser_id(Long.valueOf(userId));

            userRepository.createAddress(addressBean);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse updateAddress(
            Long id,
            AddressBean addressBean
    ) throws BaseException{
        ApiResponse res = new ApiResponse();
        try {
            AddressBean address = userRepository.getAddressById(id);
            if(address == null){
                throw new UserException("address.not.found", "Address not found");
            }

            addressBean.setId(id);
            userRepository.updateAddress(addressBean);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }




}
