package com.yoyo.admin.es_common.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScriptScoreQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 通用ElasticSearch工具接口
 */
@Slf4j
@Service
public class ElasticSearchService  {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 共用方法：ElasticSearch通用查询
     * @param searchSourceBuilder
     * @param index
     * @return
     */
    private SearchResponse pageQuerySearchResponse(SearchSourceBuilder searchSourceBuilder, String index) {
        SearchRequest searchRequest = new SearchRequest()
                .source(searchSourceBuilder)
                .indices(index);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResponse;
    }

    /**
     * 共用方法：ElasticSearch批量操作（新增、删除）数据
     *
     * @param bulkRequest
     */
    private void esBatch(BulkRequest bulkRequest) {
        try {
            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (bulkResponse.hasFailures()) {
                log.error("bulk错误信息：{}", bulkResponse.buildFailureMessage());
            }
        } catch (Exception e) {
            log.error("批量操作es数据错误", e);
        }
    }

    /**
     * 查询指定条件的数据
     * @param indexName 索引名称
     * @param sortField 排序字段
     * @param sortType 排序类别
     * @param page 页码
     * @param rows 每页大小
     * @param boolQueryBuilder 查询条件
     * @param includeFields 返回字段
     * @param excludeFields 排除字段
     * @return
     */
    public SearchResponse search(String indexName, String sortField, String sortType, Integer page, Integer rows, BoolQueryBuilder boolQueryBuilder,
                                 String[] includeFields, String[] excludeFields) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .from((page - 1) * rows)
                .size(rows)
                .fetchSource(includeFields, excludeFields)
                .trackTotalHits(true);

        SortOrder so = SortOrder.DESC;
        if (ObjectUtil.isNotEmpty(sortType) && "asc".equals(sortType)) {
            so = SortOrder.ASC;
        }
        searchSourceBuilder.sort(sortField, so);
        searchSourceBuilder.query(boolQueryBuilder);

        return pageQuerySearchResponse(searchSourceBuilder, indexName);
    }

    /**
     * 查询单条数据
     * @param indexName 索引名称
     * @param boolQueryBuilder 查询条件
     * @return
     */
    public Map<String, Object> searchOne(String indexName, BoolQueryBuilder boolQueryBuilder) {
        Map<String, Object> resultMap = new HashMap();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().from(0).size(1);
        searchSourceBuilder.query(boolQueryBuilder);
        SearchResponse searchResponse = pageQuerySearchResponse(searchSourceBuilder, indexName);
        if (searchResponse.getHits().getTotalHits().value > 0) {
            SearchHit searchHit = searchResponse.getHits().getHits()[0];
            resultMap = searchHit.getSourceAsMap();
        }
        return resultMap;
    }

    /**
     * 获取指定索引下的数据量
     *
     * @param indexName
     * @return
     */
    public Long getCountByIndex(String indexName) {
        Long totalHites = 0L;
        if (!StrUtil.isEmpty(indexName)) {
            try {
                CountRequest countRequest = new CountRequest(indexName);
                totalHites = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT).getCount();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return totalHites;
    }

    /**
     * 查询聚合数据
     * @param indexName 索引名称
     * @param boolQueryBuilder 查询条件
     * @param aggregationBuilder 聚合条件
     * @param includeFields 返回字段
     * @param excludeFields 排除字段
     * @return
     */
    public SearchResponse aggsSearch(String indexName, BoolQueryBuilder boolQueryBuilder, AggregationBuilder aggregationBuilder,
                                     String[] includeFields, String[] excludeFields) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .size(0)
                .fetchSource(includeFields, excludeFields)
                .trackTotalHits(true);
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
        return pageQuerySearchResponse(searchSourceBuilder, indexName);
    }

    /**
     * 根据图片特征向量实现以图搜图（字段类型需为dense_vector）
     * @param indexName
     * @param scriptScoreQueryBuilder
     * @param score
     * @param page
     * @param rows
     * @param includeFields
     * @param excludeFields
     * @return
     */
    public SearchResponse imageSearch(String indexName, ScriptScoreQueryBuilder scriptScoreQueryBuilder, Float score, Integer page, Integer rows, String[] includeFields, String[] excludeFields) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().trackTotalHits(true);
        sourceBuilder.query(scriptScoreQueryBuilder);
        sourceBuilder.from((page - 1) * rows);
        sourceBuilder.size(rows);
        sourceBuilder.minScore(score);
        sourceBuilder.timeout(new TimeValue(120, TimeUnit.SECONDS));
        sourceBuilder.fetchSource(includeFields, excludeFields);
        return pageQuerySearchResponse(sourceBuilder, indexName);
    }

    /**
     * 批量添加数据（自动生成id）
     * @param indexName
     * @param dataList
     */
    public void addBatchDataAutoId(String indexName, List<Map> dataList) {
        BulkRequest request = new BulkRequest();
        try {
            int count = 0;
            for (Map map : dataList) {
                request.add(new IndexRequest(indexName).source(map));
                count++;
                if (count == 1000) {
                    esBatch(request);
                    count = 0;
                }
            }
            if (count != 0) {
                esBatch(request);
            }
        } catch (Exception e) {
            log.error("生成BulkRequest错误", e);
        }
    }

    /**
     * 批量添加数据（主动指定id）
     * @param indexName
     * @param dataList
     */
    public void addBatchData(String indexName, List<Map> dataList) {
        BulkRequest request = new BulkRequest();
        try {
            int count = 0;
            for (Map map : dataList) {
                String id = map.get("id").toString();
                request.add(new IndexRequest(indexName).id(id).source(map));
                count++;
                if (count == 1000) {
                    esBatch(request);
                    count = 0;
                }
            }
            if (count != 0) {
                esBatch(request);
            }
        } catch (Exception e) {
            log.error("生成BulkRequest错误", e);
        }
    }

    /**
     * 批量删除数据
     * @param indexName
     * @param idList
     */
    public void deleteBatchData(String indexName, List<String> idList) {
        BulkRequest request = new BulkRequest();
        try {
            int count = 0;
            for (String id : idList) {
                request.add(new DeleteRequest().index(indexName).id(id));
                count++;
                if (count == 1000) {
                    esBatch(request);
                    count = 0;
                }
            }
            if (count != 0) {
                esBatch(request);
            }
        } catch (Exception e) {
            log.error("生成BulkRequest错误", e);
        }
    }

    /**
     * 修改数据
     * @param indexName
     * @param dataMap
     */
    public void update(String indexName, Map dataMap) {
        String contentId = dataMap.get("id").toString();
        UpdateRequest updateRequest = new UpdateRequest(indexName, "_doc", contentId);
        updateRequest.doc(JSONUtil.parse(dataMap.get("data")).toString(), XContentType.JSON);
        try {
            restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
