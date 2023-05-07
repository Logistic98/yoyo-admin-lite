package com.yoyo.admin.common.logger.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoyo.admin.common.domain.Dept;
import com.yoyo.admin.common.domain.Role;
import com.yoyo.admin.common.domain.User;
import com.yoyo.admin.common.logger.annotation.DeleteLog;
import com.yoyo.admin.common.utils.PageData;
import com.yoyo.admin.common.logger.annotation.CreateLog;
import com.yoyo.admin.common.logger.annotation.UpdateLog;
import com.yoyo.admin.common.logger.domain.LogContentObject;
import com.yoyo.admin.common.logger.domain.OperationLog;
import com.yoyo.admin.common.logger.domain.OperationLogRepository;
import com.yoyo.admin.common.logger.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.io.File;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 操作日志
 */
@Service
@Slf4j
public class OperationLogService {

    private OperationLogRepository operationLogRepository;
    private TerminalService terminalService;
    private final ObjectMapper _objectMapper = new ObjectMapper();
    private final SimpleDateFormat _simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public void setOperationLogRepository(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    @Autowired
    public void setTerminalService(TerminalService terminalService) {
        this.terminalService = terminalService;
    }

    /**
     * 按id获取操作日志
     * @param id
     * @return
     */
    public OperationLog get(Long id) {
        return operationLogRepository.findFirstById(id);
    }

    /**
     * 操作日志查询
     * @param operationType
     * @param terminalNames
     * @param longUserId
     * @param stringUserId
     * @param userName
     * @param ipAddress
     * @param target
     * @param operation
     * @param beginTime
     * @param endTime
     * @param remark
     * @param page
     * @param pageSize
     * @return
     */
    public PageData<OperationLog> list(OperationType operationType, List<String> terminalNames, Long longUserId, String stringUserId, String userName,
                                       String ipAddress, String target, String operation, Date beginTime, Date endTime, String remark, int page, int pageSize) {
        Specification<OperationLog> specification = (Specification<OperationLog>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (operationType != null) {
                predicateList.add(criteriaBuilder.isNotNull(root.get("operationType")));
                predicateList.add(criteriaBuilder.equal(root.get("operationType"), operationType));
            }
            if (terminalNames != null && terminalNames.size() > 0) {
                predicateList.add(criteriaBuilder.isNotNull(root.get("terminalName")));
                CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("terminalName"));
                terminalNames.forEach(in::value);
                predicateList.add(in);
            }
            if (longUserId != null) {
                predicateList.add(criteriaBuilder.isNotNull(root.get("longUserId")));
                predicateList.add(criteriaBuilder.equal(root.get("longUserId"), longUserId));
            }
            if (stringUserId != null && !stringUserId.isEmpty()) {
                predicateList.add(criteriaBuilder.isNotNull(root.get("stringUserId")));
                predicateList.add(criteriaBuilder.equal(root.get("stringUserId"), stringUserId));
            }
            if (userName != null && !userName.isEmpty()) {
                predicateList.add(criteriaBuilder.isNotNull(root.get("userName")));
                predicateList.add(criteriaBuilder.like(root.get("userName"), "%" + userName + "%"));
            }
            if (ipAddress != null && !ipAddress.isEmpty()) {
                predicateList.add(criteriaBuilder.isNotNull(root.get("ipAddress")));
                predicateList.add(criteriaBuilder.like(root.get("ipAddress"), "%" + ipAddress + "%"));
            }
            if (target != null && !target.isEmpty()) {
                predicateList.add(criteriaBuilder.isNotNull(root.get("target")));
                predicateList.add(criteriaBuilder.equal(root.get("target"), target));
            }
            if (operation != null && !operation.isEmpty()) {
                predicateList.add(criteriaBuilder.isNotNull(root.get("operation")));
                predicateList.add(criteriaBuilder.like(root.get("operation"), "%" + operation + "%"));
            }
            if (beginTime != null) {
                predicateList.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), beginTime));
            }
            if (endTime != null) {
                predicateList.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), endTime));
            }
            if (remark != null && !remark.isEmpty()) {
                predicateList.add(criteriaBuilder.isNotNull(root.get("remark")));
                predicateList.add(criteriaBuilder.like(root.get("remark"), "%" + remark + "%"));
            }
            Predicate[] predicates = new Predicate[predicateList.size()];
            return criteriaBuilder.and(predicateList.toArray(predicates));
        };
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Page<OperationLog> specialities = operationLogRepository.findAll(specification, PageRequest.of(page - 1, pageSize, sort));
        return new PageData<>(specialities, page, pageSize);
    }

    /**
     * 新增操作日志
     * @param joinPoint
     * @param webUserId
     * @param wechatUserId
     * @param userName
     * @param ipAddress
     */
    public void create(JoinPoint joinPoint, Long webUserId, String wechatUserId, String userName, String ipAddress) {
        Method method = getMethodByName(joinPoint.getTarget(), joinPoint.getSignature().getName());
        if (method == null) {
            return;
        }
        Annotation annotation = getOperationLogAnnotationFromMethod(method);
        OperationType operationType = null;
        String target = null;
        String operation = null;
        StringBuilder remark = new StringBuilder();
        if (annotation != null) {
            operationType = getOperationTypeByAnnotation(annotation);
            target = getTargetFromAnnotation(annotation);
            operation = getOperationFromAnnotation(annotation);
        }
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationType(operationType);
        operationLog.setTerminalName(terminalService.getTerminalName());
        operationLog.setWebUserId(webUserId);
        operationLog.setWechatUserId(wechatUserId);
        operationLog.setUserName(userName);
        operationLog.setIpAddress(ipAddress);
        operationLog.setOperation(operation);
        operationLog.setContent(packageParamToJson(method, joinPoint.getArgs(), remark));
        operationLog.setTarget(target);
        operationLog.setRemark(remark.toString());
        operationLogRepository.save(operationLog);
    }

    /**
     * 获取已存在的所有target
     * @param terminalNames
     * @return
     */
    public List<String> listTarget(List<String> terminalNames) {
        return operationLogRepository.findAllTargets(terminalNames);
    }

    /**
     * @param object     对象
     * @param methodName 需要的方法的方法名
     * @return 方法
     */
    public Method getMethodByName(Object object, String methodName) {
        Class<?> objectClass = object.getClass();
        Method[] methods = objectClass.getDeclaredMethods();
        for (int i = 0; i <= methods.length - 1; i++) {
            Method method = methods[i];
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    /**
     * 根据方法获取操作日志注解
     * @param method
     * @return
     */
    private Annotation getOperationLogAnnotationFromMethod(Method method) {
        Annotation[] annotations = method.getDeclaredAnnotations();
        for (int i = 0; i <= annotations.length - 1; i++) {
            Annotation annotation = annotations[i];
            Class<?> clazz = annotation.annotationType();
            if (clazz.equals(CreateLog.class) || clazz.equals(UpdateLog.class) || clazz.equals(DeleteLog.class)) {
                return annotation;
            }
        }
        return null;
    }

    /**
     * 根据操作日志注解判断操作类型
     * @param annotation
     * @return
     */
    private OperationType getOperationTypeByAnnotation(Annotation annotation) {
        if (annotation.annotationType().equals(CreateLog.class)) {
            return OperationType.Create;
        }
        if (annotation.annotationType().equals(UpdateLog.class)) {
            return OperationType.Update;
        }
        if (annotation.annotationType().equals(DeleteLog.class)) {
            return OperationType.Delete;
        }
        return null;
    }

    /**
     * 从注解中获取target
     * @param annotation
     * @return
     */
    private String getTargetFromAnnotation(Annotation annotation) {
        if (annotation.annotationType().equals(CreateLog.class)) {
            CreateLog createLog = (CreateLog) annotation;
            return createLog.target();
        }
        if (annotation.annotationType().equals(UpdateLog.class)) {
            UpdateLog updateLog = (UpdateLog) annotation;
            return updateLog.target();
        }
        if (annotation.annotationType().equals(DeleteLog.class)) {
            DeleteLog deleteLog = (DeleteLog) annotation;
            return deleteLog.target();
        }
        return null;
    }

    /**
     * 从注解中获取operation
     * @param annotation
     * @return
     */
    private String getOperationFromAnnotation(Annotation annotation) {
        if (annotation.annotationType().equals(CreateLog.class)) {
            CreateLog createLog = (CreateLog) annotation;
            return createLog.operation();
        }
        if (annotation.annotationType().equals(UpdateLog.class)) {
            UpdateLog updateLog = (UpdateLog) annotation;
            return updateLog.operation();
        }
        if (annotation.annotationType().equals(DeleteLog.class)) {
            DeleteLog deleteLog = (DeleteLog) annotation;
            return deleteLog.operation();
        }
        return null;
    }

    /**
     * 将指定方法的参数打包成json
     * @param method
     * @param args
     * @param remark
     * @return
     */
    private String packageParamToJson(Method method, Object[] args, StringBuilder remark) {
        Map<String, Object> map = new HashMap<>();
        Parameter[] parameters = method.getParameters();
        if (parameters.length == args.length) {
            for (int i = 0; i <= parameters.length - 1; i++) {
                LogContentObject contentObject = simplifyParameter(parameters[i].getName(), args[i], 0, remark);
                if (contentObject != null && contentObject.getName() != null) {
                    map.put(contentObject.getName(), contentObject.getData());
                }
            }
        }
        try {
            return _objectMapper.writeValueAsString(map);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private LogContentObject simplifyParameter(String paramName, Object data, Integer deep, StringBuilder remark) {
        if (data == null || deep > 3) {
            return null;
        }
        LogContentObject contentObject = new LogContentObject();
        contentObject.setName(paramName);
        //如果是基本类型，直接返回
        if (isBaseType(data)) {
            contentObject.setData(data);
            return contentObject;
        }
        //如果是枚举类型，返回枚举名
        if (data.getClass().isEnum()) {
            contentObject.setData(((Enum<?>) data).name());
            return contentObject;
        }
        //如果是列表，处理其中的每一个元素
        if (data instanceof List) {
            List<Object> list = new ArrayList<>();
            List<?> dataList = (List<?>) data;
            String name = paramName;
            for (int i = 0; i <= dataList.size() - 1; i++) {
                LogContentObject listLogContentObject = simplifyParameter(null, dataList.get(i), deep + 1, remark);
                if (listLogContentObject == null) {
                    continue;
                }
                if (listLogContentObject.getName() != null) {
                    name = listLogContentObject.getName();
                }
                list.add(listLogContentObject.getData());
            }
            contentObject.setName(name);
            contentObject.setData(list);
            return contentObject;
        }
        //如果是文件，直接返回字符串"文件"
        if (data instanceof InputStream || data instanceof File) {
            contentObject.setData("文件");
            return contentObject;
        }
        if (data instanceof Role) {
            contentObject.setName("角色");
            contentObject.setData(simplifyRole((Role) data, remark));
            return contentObject;
        }
        if (data instanceof User) {
            contentObject.setName("用户");
            contentObject.setData(simplifyUser((User) data, remark));
            return contentObject;
        }
        if (data instanceof Dept) {
            contentObject.setName("部门");
            contentObject.setData(simplifyDept((Dept) data));
            return contentObject;
        }
        //如果是其他类型，返回数据为一个Map，内部保存字段名和字段值
        Map<String, Object> map = new HashMap<>();
        Field[] fields = data.getClass().getDeclaredFields();
        for (int i = 0; i <= fields.length - 1; i++) {
            LogContentObject filedLogContentObject = null;
            Field field = fields[i];
            field.setAccessible(true);
            try {
                filedLogContentObject = simplifyParameter(field.getName(), field.get(data), deep + 1, remark);
            } catch (IllegalAccessException ex) {
                log.error(ex.getMessage());
            }
            if (filedLogContentObject != null) {
                map.put(filedLogContentObject.getName(), filedLogContentObject.getData());
            }
        }
        contentObject.setData(map);
        return contentObject;
    }

    private Map<String, Object> simplifyRole(Role role, StringBuilder remark) {
        Map<String, Object> map = new HashMap<>();
        if (role != null) {
            map.put("id", role.getId());
            map.put("部门类型编号", role.getCode());
            map.put("部门类型名称", role.getName());
            remark.append(role.getName());
        }
        return map;
    }

    private Map<String, Object> simplifyUser(User user, StringBuilder remark) {
        Map<String, Object> map = new HashMap<>();
        if (user != null) {
            map.put("用户id", user.getId());
            map.put("用户姓名", user.getName());
            map.put("部门id", user.getDept() != null ? user.getDept().getId() : null);
            remark.append(user.getName()).append("(").append(user.getUsername()).append(")").append(" ");
        }
        return map;
    }

    private Map<String, Object> simplifyDept(Dept dept) {
        Map<String, Object> map = new HashMap<>();
        if (dept != null) {
            map.put("id", dept.getId());
            map.put("名称", dept.getName());
            map.put("上级部门", dept.getParentDept() != null ? dept.getParentDept().getName() : null);
        }
        return map;
    }

    private static boolean isBaseType(Object object) {
        if (object instanceof Boolean) {
            return true;
        }
        if (object instanceof Character) {
            return true;
        }
        if (object instanceof Byte) {
            return true;
        }
        if (object instanceof Short) {
            return true;
        }
        if (object instanceof Integer) {
            return true;
        }
        if (object instanceof Long) {
            return true;
        }
        if (object instanceof Float) {
            return true;
        }
        if (object instanceof Double) {
            return true;
        }
        if (object instanceof Date) {
            return true;
        }
        return object instanceof String;
    }

}
