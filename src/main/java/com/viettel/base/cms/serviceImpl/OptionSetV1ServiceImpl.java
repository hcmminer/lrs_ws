package com.viettel.base.cms.serviceImpl;

import com.viettel.base.cms.dto.OptionSetV1DTO;
import com.viettel.base.cms.model.OptionSetV1;
import com.viettel.base.cms.repo.BTSStationRepo;
import com.viettel.base.cms.service.BTSStationService;
import com.viettel.base.cms.service.OptionSetService;
import com.viettel.base.cms.service.OptionSetV1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class OptionSetV1ServiceImpl implements OptionSetV1Service {
}
