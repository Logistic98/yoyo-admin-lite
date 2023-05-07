package com.yoyo.admin.common.jpa;

import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 使用非托管的entityManager执行原生查询相关说明
 * 在JPA环境中执行原生查询（SELECT）的四种方式
 * 1：使用repository的@Query注解并设置其的nativeQuery等于true。（问题最少，请务必优先选择这种方式）
 * 2：使用托管的entityManager并unwrap为org.hibernate.SQLQuery。（问题是org.hibernate.SQLQuery已过期，将来的Hibernate版本不再可用，故不推荐）
 * 3：使用托管的entityManager并unwrap为NativeQueryImpl，需要在方法上加@Transactional(readOnly = true)注解。（如果不加readOnly = true，会有数据回写问题，使得临时赋值操作变得极度危险）
 * 4：使用非托管的entityManager并unwrap为NativeQueryImpl，这也是本类实现的方式。（如果非托管的entityManager没有正确关闭，会造成连接泄露，造成连接不可用，所以尽量使用本类提供的方法，不要自行创建非托管的entityManager）
 */
@Service
public class NativeQueryService {

    private EntityManagerFactory entityManagerFactory;

    @Autowired
    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> queryMapList(String sqlStr, Map<String, Object> params) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        List<?> list = queryList(sqlStr, params);
        for (Object o : list) {
            mapList.add((Map<String, Object>) o);
        }
        return mapList;
    }

    /**
     * 原生查询列表
     * @param sqlStr
     * @param params
     * @return
     */
    public List<?> queryList(String sqlStr, Map<String, Object> params) {
        List<?> list = new ArrayList<>();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            Query query = entityManager.createNativeQuery(sqlStr);
            query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            if (params != null && params.size() > 0) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            }
            list = query.getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            // 注意：一定要关闭entityManager
            entityManager.close();
        }
        return list;
    }

    /**
     * 原生查询带分页列表
     * @param sqlStr
     * @param params
     * @param page
     * @param pageSize
     * @return
     */
    public List<?> queryPage(String sqlStr, Map<String, Object> params, int page, int pageSize) {
        List<?> list = new ArrayList<>();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            Query query = entityManager.createNativeQuery(sqlStr);
            query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            if (params != null && params.size() > 0) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            }
            list = query.getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            // 注意：一定要关闭entityManager
            entityManager.close();
        }
        return list;
    }

    /**
     * 批量执行SQL
     * @param sqlList
     */
    public void batchExecute(List<String> sqlList) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            for (String sql : sqlList) {
                entityManager.createNativeQuery(sql).executeUpdate();
            }
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            // 注意：一定要关闭entityManager
            entityManager.close();
        }
    }
}
