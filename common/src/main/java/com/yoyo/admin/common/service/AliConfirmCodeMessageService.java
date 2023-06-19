package com.yoyo.admin.common.service;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.yoyo.admin.common.config.AliMessageConfig;
import com.yoyo.admin.common.domain.ConfirmCodeMessage;
import com.yoyo.admin.common.domain.repository.ConfirmCodeMessagingRepository;
import com.yoyo.admin.common.logger.annotation.CreateLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

/**
 * 阿里大于（大鱼）短信验证码
 */
@Service
@Slf4j
public class AliConfirmCodeMessageService {
    private ConfirmCodeMessagingRepository confirmCodeMessagingRepository;
    private AliMessageConfig aliMessageConfig;

    @Autowired
    public void setConfirmCodeMessagingRepository(ConfirmCodeMessagingRepository confirmCodeMessagingRepository) {
        this.confirmCodeMessagingRepository = confirmCodeMessagingRepository;
    }

    @Autowired
    public void setAliMessageConfig(AliMessageConfig aliMessageConfig) {
        this.aliMessageConfig = aliMessageConfig;
    }


    public ConfirmCodeMessage getForResend(String phoneNumber) {
        Date maxDate = new Date(System.currentTimeMillis() - aliMessageConfig.getResendInterval() * 1000);
        return confirmCodeMessagingRepository.findFirstByPhoneNumberAndCreateTimeAfterAndIsUsedOrderByIdDesc(phoneNumber, maxDate, 0);
    }

    public ConfirmCodeMessage create(String phoneNumber, String template, Integer type) throws Exception {
        ConfirmCodeMessage confirmCodeMessage = getForResend(phoneNumber);
        if (confirmCodeMessage != null) {
            throw new Exception("可重发时间未到");
        }
        Random random = new Random();
        String code = String.format("%04d", random.nextInt(9999));
        // 短信平台发送的验证码如果开头是0, 会省略
        if ("0".equals(code.substring(0, 1))) {
            code = "6" + code.substring(1);
        }
        confirmCodeMessage = new ConfirmCodeMessage();
        confirmCodeMessage.setPhoneNumber(phoneNumber);
        confirmCodeMessage.setConfirmCode(code);
        confirmCodeMessage.setIsUsed(0);
        confirmCodeMessage.setType(type);
        confirmCodeMessage.setCreateTime(new Date());
        JSONObject response = JSONObject.parseObject(sendMessage(phoneNumber, code, template));
        if (response.get("Message") != null && "OK".equals(response.get("Code"))) {
            confirmCodeMessagingRepository.save(confirmCodeMessage);
            return confirmCodeMessage;
        } else {
            log.error(response.toJSONString());
            throw new Exception("短信发送失败");
        }
    }

    /**
     * 发送短信验证码
     * @param phone
     * @param code
     * @param template
     * @return
     * @throws Exception
     */
    private String sendMessage(String phone, String code, String template) throws Exception {
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", aliMessageConfig.getAccessKeyId(),
                aliMessageConfig.getAccessKeySecret());
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(aliMessageConfig.getDomain());
        request.setVersion(aliMessageConfig.getApiVersion());
        request.setAction("SendSms");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("TemplateCode", template);
        request.putQueryParameter("SignName", aliMessageConfig.getSignName());
        request.putQueryParameter("TemplateParam", "{\"code\":" + code + "}");
        CommonResponse response = null;
        try {
            response = client.getCommonResponse(request);
        } catch (ClientException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new Exception("短信发送失败");
        }
        if (response != null && response.getHttpResponse().isSuccess()) {
            return response.getData();
        } else {
            throw new Exception("短信发送失败");
        }
    }

    @CreateLog(target = "验证码", operation = "发送注册验证码")
    public ConfirmCodeMessage createForRegister(String phoneNumber) throws Exception {
        return create(phoneNumber, aliMessageConfig.getRegisterTemplate(), 1);
    }

    @CreateLog(target = "验证码", operation = "发送登录验证码")
    public ConfirmCodeMessage createForLogin(String phoneNumber) throws Exception {
        return create(phoneNumber, aliMessageConfig.getLoginTemplate(), 2);
    }

    @CreateLog(target = "验证码", operation = "发送修改密码验证码")
    public ConfirmCodeMessage createForChangePassword(String phoneNumber) throws Exception {
        return create(phoneNumber, aliMessageConfig.getChangePasswordTemplate(), 3);
    }

}
