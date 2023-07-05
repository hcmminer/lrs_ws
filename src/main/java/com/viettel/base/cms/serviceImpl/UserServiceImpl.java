package com.viettel.base.cms.serviceImpl;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.*;
import com.viettel.base.cms.model.Staff;
import com.viettel.base.cms.model.VSAValidate;
import com.viettel.base.cms.repo.StaffRepo;
import com.viettel.base.cms.service.UserService;
import com.viettel.base.cms.utils.JwtTokenUtil;
import com.viettel.security.PassTranformer;
import com.viettel.vfw5.base.utils.DataUtils;
import com.viettel.vfw5.base.utils.ResourceBundle;
import com.viettel.vfw5.base.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import viettel.passport.client.ObjectToken;
import viettel.passport.client.RoleToken;
import viettel.passport.client.UserToken;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Service

public class UserServiceImpl implements UserService {

    @Autowired
    Environment env;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    @PersistenceContext(unitName = Constant.UNIT_NAME_ENTITIES_CMS)
    private EntityManager cms;

    @Autowired
    StaffRepo staffRepo;

    @Override
    public LoginDTO loginAuthentication(CommonInputDTO commonInputDTO, String locate) {
        VSAValidate vsaValidate = new VSAValidate();
        LoginDTO loginDTO = new LoginDTO();
        ResourceBundle r = new ResourceBundle(locate);
        PassTranformer.setInputKey(env.getProperty("key.token.secret"));
        String password = null;
        if (commonInputDTO.getPassword() != null) {
            password = PassTranformer.decrypt(commonInputDTO.getPassword());
            password = password.replace(env.getProperty("key.public.salt"), "");
        } else {
            return null;
        }
        vsaValidate.setUser(commonInputDTO.getUserName());
        vsaValidate.setPassword(password);
        vsaValidate.setDomainCode(env.getProperty("system.name"));
        vsaValidate.setCasValidateUrl(env.getProperty("vsa.url"));
        vsaValidate.setIp(env.getProperty("ip.local"));
        try {
            vsaValidate.validate();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        List<MenuParent> menuParentList = new ArrayList<>();
        if (vsaValidate.isAuthenticationSuccesful()) {
            UserToken userToken = vsaValidate.getUserToken();
            if (userToken != null) {
                ArrayList objects = userToken.getParentMenu();
                for (int i = 0; i < objects.size(); i++) {
                    ObjectToken objectToken = (ObjectToken) objects.get(i);
                    if ("M".equals(objectToken.getObjectType())) {
                        MenuParent menuParent = new MenuParent();
//                        menuParent.setTitle(objectToken.getObjectName());
                        menuParent.setTitle(r.getResourceMessage(objectToken.getObjectCode()));
                        menuParent.setCode(objectToken.getObjectCode());
                        menuParent.setRoot(true);
                        menuParent.setBullet("dot");
                        menuParent.setIcon(objectToken.getDescription());
                        menuParent.setPage(objectToken.getObjectUrl());
                        ArrayList childs = objectToken.getChildObjects();

                        List<SubMenu> subMenus = new ArrayList<>();

                        for (int j = 0; j < childs.size(); j++) {
                            ObjectToken childToken = (ObjectToken) childs.get(j);
                            if ("M".equals(childToken.getObjectType())) {
                                SubMenu subMenu = new SubMenu();
                                subMenu.setBullet("dot");
                                subMenu.setPage(childToken.getObjectUrl());
                                subMenu.setTitle(childToken.getObjectName());
                                subMenus.add(subMenu);
                            }
                        }
                        menuParent.setSubmenu(subMenus);
//                        bandv...
                        if (i == 0) {
                            List<SubMenu> subMenus0 = new ArrayList<>();
                            //
                            SubMenu subMenu1 = new SubMenu();
                            subMenu1.setTitle("optionsManager");
                            subMenu1.setBullet("dot");
                            subMenu1.setPage("/config-system/options");
                            subMenu1.setOrd(1L);
                            subMenus0.add(0,subMenu1);
                           //
                            SubMenu subMenu2 = new SubMenu();
                            subMenu2.setTitle("priceRangeManager");
                            subMenu2.setBullet("dot");
                            subMenu2.setPage("/config-system/priceRange");
                            subMenu2.setOrd(2L);
                            subMenus0.add(1,subMenu2);
                            //
                            SubMenu subMenu3 = new SubMenu();
                            subMenu3.setTitle("staffManager");
                            subMenu3.setBullet("dot");
                            subMenu3.setPage("/config-system/staff");
                            subMenu3.setOrd(3L);
                            subMenus0.add(2,subMenu3);
                            //
                            menuParent.setSubmenu(subMenus0);

                        }
//                        ...bandv
                        menuParentList.add(menuParent);
                    }
                }
            } else {
                System.out.println("Exp token authentication unsuccessful");
                return null;
            }

            loginDTO.setLstMenu(menuParentList);

            UserDetails userDetails = new User(commonInputDTO.getUserName() + "----" + password, password, new ArrayList<>());
            String token = jwtTokenUtil.generateToken(userDetails);
            loginDTO.setToken(token);
            loginDTO.setUserName(userToken.getUserName());
            loginDTO.setUserFullName(userToken.getFullName());
            if (!StringUtils.isStringNullOrEmpty(userToken.getRolesList())) {
                if (userToken.getRolesList().size() > 0) {
                    if ("GSCT".equals(commonInputDTO.getAppCode())) {
                        for (RoleToken roleToken : userToken.getRolesList()) {
                            System.out.println("roleCode: " + roleToken.getRoleCode());
                            if (Constant.CMS_ROLES.CMS_CORP_STAFF.equals(roleToken.getRoleCode()) || Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT.equals(roleToken.getRoleCode())
                                    || Constant.CMS_ROLES.CMS_PROV_INFA_STAFF.equals(roleToken.getRoleCode()) || Constant.CMS_ROLES.CMS_PROV_TECH_STAFF.equals(roleToken.getRoleCode())) {
                                loginDTO.setRoleCode(roleToken.getRoleCode());
                                loginDTO.setRoleName(roleToken.getRoleName());
                                loginDTO.setRoleDescription(roleToken.getDescription());
                                System.out.println("roleCode ra : " + loginDTO.getRoleCode());
                            }
                        }
                    } else if ("IMT".equals(commonInputDTO.getAppCode())) {
                        for (RoleToken roleToken : userToken.getRolesList()) {
                            System.out.println("roleCode: " + roleToken.getRoleCode());
                            if (Constant.BTS_ROLES.CMS_BTS_PNO_STAFF.equals(roleToken.getRoleCode())
                                    || Constant.BTS_ROLES.CMS_BTS_CN_STAFF.equals(roleToken.getRoleCode())
                                    || Constant.BTS_ROLES.CMS_BTS_TCCN_STAFF.equals(roleToken.getRoleCode())
                                    || Constant.BTS_ROLES.CMS_BTS_CND_STAFF.equals(roleToken.getRoleCode())
                                    || Constant.BTS_ROLES.CMS_BTS_NOC_STAFF.equals(roleToken.getRoleCode())
                                    || Constant.BTS_ROLES.CMS_BTS_GRAND_TC_STAFF.equals(roleToken.getRoleCode())) {
                                loginDTO.setRoleCode(roleToken.getRoleCode());
                                loginDTO.setRoleName(roleToken.getRoleName());
                                loginDTO.setRoleDescription(roleToken.getDescription());
                                System.out.println("roleCode ra : " + loginDTO.getRoleCode());
                            }
                        }
                    } else {
                        loginDTO.setRoleCode(userToken.getRolesList().get(0).getRoleCode());
                        loginDTO.setRoleName(userToken.getRolesList().get(0).getRoleName());
                        loginDTO.setRoleDescription(userToken.getRolesList().get(0).getDescription());
                    }
                }
            }

            // Xử lý menu cho 2 hệ thống

            List<MenuParent> sortMenuParent = new ArrayList<>();
            for (MenuParent menuParent : menuParentList) {
                System.out.println("Size " + menuParentList.size());
                System.out.println("Menu " + menuParent.getCode() + " " + menuParent.getPage());
                if (Constant.CMS_ROLES.CMS_CORP_STAFF.equals(loginDTO.getRoleCode()) || Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT.equals(loginDTO.getRoleCode())
                        || Constant.CMS_ROLES.CMS_PROV_INFA_STAFF.equals(loginDTO.getRoleCode()) || Constant.CMS_ROLES.CMS_PROV_TECH_STAFF.equals(loginDTO.getRoleCode())) {
                    if ("GSCT".equals(menuParent.getCode()) || "QLCT".equals(menuParent.getCode()) || "REPORT_CMS".equals(menuParent.getCode())) {
                        sortMenuParent.add(menuParent);
                    }
                } else if (Constant.BTS_ROLES.CMS_BTS_PNO_STAFF.equals(loginDTO.getRoleCode())
                        || Constant.BTS_ROLES.CMS_BTS_CN_STAFF.equals(loginDTO.getRoleCode())
                        || Constant.BTS_ROLES.CMS_BTS_TCCN_STAFF.equals(loginDTO.getRoleCode())
                        || Constant.BTS_ROLES.CMS_BTS_CND_STAFF.equals(loginDTO.getRoleCode())
                        || Constant.BTS_ROLES.CMS_BTS_NOC_STAFF.equals(loginDTO.getRoleCode())
                        || Constant.BTS_ROLES.CMS_BTS_GRAND_TC_STAFF.equals(loginDTO.getRoleCode())) {
                    if ("QLTBTS".equals(menuParent.getCode()) || "REPORT_BTS".equals(menuParent.getCode()) || "CONFIG_SYSTEM".equals(menuParent.getCode())) {
                        sortMenuParent.add(menuParent);
                    }
                }
            }
            loginDTO.setLstMenu(sortMenuParent);
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getAppCode())) {
                if ("GSCT".equals(commonInputDTO.getAppCode())) {
                    if (Constant.CMS_ROLES.CMS_CORP_STAFF.equals(loginDTO.getRoleCode()) || Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT.equals(loginDTO.getRoleCode())
                            || Constant.CMS_ROLES.CMS_PROV_INFA_STAFF.equals(loginDTO.getRoleCode()) || Constant.CMS_ROLES.CMS_PROV_TECH_STAFF.equals(loginDTO.getRoleCode())) {
                        System.out.println("Exep role gsct authentication unsuccessful");
                    } else {
                        System.out.println("loi quyen user");
                        loginDTO.setReasonFail("permission wrong");
                    }
                } else if ("IMT".equals(commonInputDTO.getAppCode())) {
                    if (Constant.BTS_ROLES.CMS_BTS_PNO_STAFF.equals(loginDTO.getRoleCode())
                            || Constant.BTS_ROLES.CMS_BTS_CN_STAFF.equals(loginDTO.getRoleCode())
                            || Constant.BTS_ROLES.CMS_BTS_TCCN_STAFF.equals(loginDTO.getRoleCode())
                            || Constant.BTS_ROLES.CMS_BTS_CND_STAFF.equals(loginDTO.getRoleCode())
                            || Constant.BTS_ROLES.CMS_BTS_NOC_STAFF.equals(loginDTO.getRoleCode())
                            || Constant.BTS_ROLES.CMS_BTS_GRAND_TC_STAFF.equals(loginDTO.getRoleCode())) {
                        System.out.println("Exep role imt authentication unsuccessful");
                    } else {
                        System.out.println("loi quyen user");
                        loginDTO.setReasonFail("permission wrong");
                    }
                }
            }
            System.out.println(userToken.getStaffCode());
            List<Staff> lstStaff = getStaffByStaffCode(userToken);
            if (lstStaff.size() == 0) {
                Staff staff = addNewStaff(userToken, loginDTO.getRoleCode());
                loginDTO.setStaff(staff);
            } else if (lstStaff.get(0).getStatus() == 0) {
                loginDTO.setReasonFail("STATUS_0");
            } else {
                System.out.println(lstStaff.get(0));
                if (!StringUtils.isStringNullOrEmpty(lstStaff.get(0).getProvinceCode())) {
                    System.out.println(lstStaff.get(0).getProvinceCode());
                    loginDTO.setProvinceCode(lstStaff.get(0).getProvinceCode());
                } else {
                    System.out.println("get Province FAIL");
                }
                if (!StringUtils.isStringNullOrEmpty(lstStaff.get(0).getProvinceName())) {
                    loginDTO.setProvinceName(lstStaff.get(0).getProvinceName());
                }
                loginDTO.setStaff(lstStaff.get(0));
                saveLoginStaff(userToken);
            }
            return loginDTO;
        } else {
            loginDTO.setReasonFail("u/p wrong");
            System.out.println("Exep end authentication unsuccessful");
            loginDTO.setReasonFail(vsaValidate.getReasonFail());
            return loginDTO;
        }
    }

    private void saveLoginStaff(UserToken userToken) {
        Staff oldStaff = getStaffByStaffCode(userToken).get(0);
//        Staff staff = new Staff();
//        staff.setStaffId(oldStaff.getStaffId());
//        staff.setStaffCode(userToken.getStaffCode());
//        staff.setStaffName(userToken.getUserName());
//        staff.setDeptId(userToken.getDeptId());
//        staff.setProvinceCode("");
//        staff.setProvinceName("");
//        staff.setNote("");
        oldStaff.setLastLoginDate(LocalDateTime.now());
//        staff.setStatus(userToken.getStatus());
//        if (!StringUtils.isStringNullOrEmpty(userToken.getTelephone()))
//            staff.setTelNumber(userToken.getTelephone());
//        else
//            staff.setTelNumber(oldStaff.getTelNumber());
//
//        if (!StringUtils.isStringNullOrEmpty(userToken.getEmail()))
//            staff.setEmail(userToken.getEmail());
//        else
//            staff.setEmail(oldStaff.getEmail());
//
//        if (!StringUtils.isStringNullOrEmpty(userToken.getRolesList()) &&
//                userToken.getRolesList().size() > 0)
//            staff.setRoleCode(userToken.getRolesList().get(0).getRoleCode());
//        else
//            staff.setRoleCode("");
        staffRepo.save(oldStaff);
    }

    @Override
    public String getUserRole(CommonInputDTO commonInputDTO) throws Exception {
        VSAValidate vsaValidate = new VSAValidate();
        LoginDTO loginDTO = new LoginDTO();
        PassTranformer.setInputKey(env.getProperty("key.token.secret"));
        String userName = commonInputDTO.getUserName().split("----")[0];
        String password = commonInputDTO.getUserName().split("----")[1];
        if (password != null) {
            PassTranformer.setInputKey(env.getProperty("key.enscrypt.security"));
            password = PassTranformer.decrypt(password);
            password = password.replace(env.getProperty("key.public.salt"), "");
        } else {
            return null;
        }
        vsaValidate.setUser(userName);
        vsaValidate.setPassword(password);
        vsaValidate.setDomainCode(env.getProperty("system.name"));
        vsaValidate.setCasValidateUrl(env.getProperty("vsa.url"));
        vsaValidate.setIp(env.getProperty("ip.local"));
        try {
            vsaValidate.validate();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        if (vsaValidate.isAuthenticationSuccesful()) {
            UserToken userToken = vsaValidate.getUserToken();
            if (!StringUtils.isStringNullOrEmpty(userToken.getRolesList()) &&
                    userToken.getRolesList().size() > 0) {
                if ("GSCT".equals(commonInputDTO.getAppCode())) {
                    for (RoleToken roleToken : userToken.getRolesList()) {
                        System.out.println("roleCode: " + roleToken.getRoleCode());
                        if (Constant.CMS_ROLES.CMS_CORP_STAFF.equals(roleToken.getRoleCode()) || Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT.equals(roleToken.getRoleCode())
                                || Constant.CMS_ROLES.CMS_PROV_INFA_STAFF.equals(roleToken.getRoleCode()) || Constant.CMS_ROLES.CMS_PROV_TECH_STAFF.equals(roleToken.getRoleCode())) {
                            loginDTO.setRoleCode(roleToken.getRoleCode());
                            loginDTO.setRoleName(roleToken.getRoleName());
                            loginDTO.setRoleDescription(roleToken.getDescription());
                            System.out.println("roleCode ra : " + loginDTO.getRoleCode());
                        }
                    }
                } else if ("IMT".equals(commonInputDTO.getAppCode())) {
                    for (RoleToken roleToken : userToken.getRolesList()) {
                        System.out.println("roleCode: " + roleToken.getRoleCode());
                        if (Constant.BTS_ROLES.CMS_BTS_PNO_STAFF.equals(roleToken.getRoleCode())
                                || Constant.BTS_ROLES.CMS_BTS_CN_STAFF.equals(roleToken.getRoleCode())
                                || Constant.BTS_ROLES.CMS_BTS_TCCN_STAFF.equals(roleToken.getRoleCode())
                                || Constant.BTS_ROLES.CMS_BTS_CND_STAFF.equals(roleToken.getRoleCode())
                                || Constant.BTS_ROLES.CMS_BTS_NOC_STAFF.equals(roleToken.getRoleCode())
                                || Constant.BTS_ROLES.CMS_BTS_GRAND_TC_STAFF.equals(roleToken.getRoleCode())) {
                            loginDTO.setRoleCode(roleToken.getRoleCode());
                            loginDTO.setRoleName(roleToken.getRoleName());
                            loginDTO.setRoleDescription(roleToken.getDescription());
                            System.out.println("roleCode ra : " + loginDTO.getRoleCode());
                        }
                    }
                } else {
                    loginDTO.setRoleCode(userToken.getRolesList().get(0).getRoleCode());
                    loginDTO.setRoleName(userToken.getRolesList().get(0).getRoleName());
                    loginDTO.setRoleDescription(userToken.getRolesList().get(0).getDescription());
                }
                return loginDTO.getRoleCode();
//                return userToken.getRolesList().get(0).getRoleCode();
            }
        } else {
            System.out.println("Excep get user role authentication unsuccessful");
            loginDTO.setReasonFail(vsaValidate.getReasonFail());
        }
        return null;

    }

    @Override
    public JwtResponse loadUserByUsername(String userNamePassword) throws Exception {
        try {
            VSAValidate vsaValidate = new VSAValidate();
            LoginDTO loginDTO = new LoginDTO();
            JwtResponse staffDTO = new JwtResponse();
            String password = null;
            String userName = userNamePassword.split("----")[0];
            String encodedPassword = userNamePassword.split("----")[1];

            if (encodedPassword != null) {
                PassTranformer.setInputKey(env.getProperty("key.enscrypt.security"));
                password = PassTranformer.decrypt(encodedPassword);
                password = password.replace(env.getProperty("key.public.salt"), "");
//                password = password + env.getProperty("key.public.salt");
            }

            vsaValidate.setUser(userName);
            vsaValidate.setPassword(password);
            vsaValidate.setDomainCode(env.getProperty("system.name"));
            vsaValidate.setCasValidateUrl(env.getProperty("vsa.url"));
            vsaValidate.setIp(env.getProperty("ip.local"));
            try {
                vsaValidate.validate();
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
            if (vsaValidate.isAuthenticationSuccesful()) {
                UserToken userToken = vsaValidate.getUserToken();
                if (userToken != null) {
                    staffDTO.setUserName(userToken.getUserName());
                    staffDTO.setPassword(encodedPassword);
                    return staffDTO;
                }
            } else {
                System.out.println("Excep load user name authentication unsuccessful");
                return null;
            }
            System.out.println("Excep load user authentication unsuccessful");
            return null;
        } catch (
                Exception e) {
            throw e;
        }

    }

    @Override
    public String getUserProvinceCode(String userName) throws Exception {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("select * from staff where staff_code = :staffName and status = :status");
            Query query = cms.createNativeQuery(sql.toString(), Staff.class);
            query.setParameter("staffName", userName);
            query.setParameter("status", Constant.STAFF_STATUS.ACTIVE);
            List<Staff> list = query.getResultList();
            return DataUtils.getString(list.get(0).getProvinceCode());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Staff getStaffByUserName(String userName) throws Exception {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("select * from staff where staff_code = :staffName and status = :status");
            Query query = cms.createNativeQuery(sql.toString(), Staff.class);
            query.setParameter("staffName", userName);
            query.setParameter("status", Constant.STAFF_STATUS.ACTIVE);
            List<Staff> list = query.getResultList();
            if (list.size() > 0)
                return list.get(0);
            else return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private Staff addNewStaff(UserToken userToken, String roleCode) {
        Staff staff = new Staff();
        Long staffId = DataUtils.getSequence(cms, "staff_seq");
        staff.setStaffId(staffId);
        staff.setStaffCode(userToken.getUserName());
        staff.setStaffName(userToken.getFullName());
        staff.setDeptId(userToken.getDeptId());
        staff.setProvinceCode("");
        staff.setProvinceName("");
        staff.setNote("");
        staff.setLastLoginDate(LocalDateTime.now());
        staff.setStatus(1L);
        staff.setTelNumber(userToken.getTelephone());
        staff.setEmail(userToken.getEmail());
        if (!StringUtils.isStringNullOrEmpty(roleCode))
            staff.setRoleCode(roleCode);
        else
            staff.setRoleCode("");
        staffRepo.save(staff);
        return staff;
    }

    private List<Staff> getStaffByStaffCode(UserToken userToken) {

        try {
            String sql = " SELECT  *  "
                    + "    FROM   staff s "
                    + "WHERE  s.staff_code =:staffCode  ";
//                    + "ORDER BY   create_datetime ASC";
            Query query = cms.createNativeQuery(sql, Staff.class);
            // con that
            query.setParameter("staffCode", userToken.getUserName());
            // con test
//            query.setParameter("staffCode", userToken.getStaffCode());
            List<Staff> lst = query.getResultList();
            return lst;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
