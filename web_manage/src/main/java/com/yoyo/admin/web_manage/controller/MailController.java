package com.yoyo.admin.web_manage.controller;

import com.yoyo.admin.common.service.MailService;
import com.yoyo.admin.common.utils.ResultDataUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Api(tags = "Mail消息通知")
@RestController
@RequestMapping(value = "/api/mail")
public class MailController {

    private MailService mailService;

    @Autowired
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    @ApiOperation("发送普通邮件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "to", value = "收件人", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "subject", value = "主题", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "content", value = "内容", dataType = "String", required = true, paramType = "query"),
    })
    @PostMapping("/sendSimpleMailMessge")
    public ResponseEntity<?> sendSimpleMailMessge(@RequestParam(defaultValue = "") String to,
                                                  @RequestParam(defaultValue = "") String subject,
                                                  @RequestParam(defaultValue = "") String content) {
        try {
            mailService.sendSimpleMailMessge(to, subject, content);
            return ResultDataUtils.success();
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("发送html邮件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "to", value = "收件人", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "subject", value = "主题", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "content", value = "内容（html）", dataType = "String", required = true, paramType = "query"),
    })
    @PostMapping("/sendHtmlMessge")
    public ResponseEntity<?> sendHtmlMessge(@RequestParam(defaultValue = "") String to,
                                            @RequestParam(defaultValue = "") String subject,
                                            @RequestParam(defaultValue = "") String content) {
        try {
            mailService.sendHtmlMessge(to, subject, content);
            return ResultDataUtils.success();
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("发送带附件的邮件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "to", value = "收件人", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "subject", value = "主题", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "content", value = "内容", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "filePath", value = "附件路径", dataType = "String", required = true, paramType = "query")
    })
    @PostMapping("/sendAttachmentMessge")
    public ResponseEntity<?> sendAttachmentMessge(@RequestParam(defaultValue = "") String to,
                                                  @RequestParam(defaultValue = "") String subject,
                                                  @RequestParam(defaultValue = "") String content,
                                                  @RequestParam(defaultValue = "") String filePath) {
        try {
            mailService.sendAttachmentMessge(to, subject, content, filePath);
            return ResultDataUtils.success();
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }


}
