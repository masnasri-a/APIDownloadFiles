package com.index.isa.dependency.dao;

import com.index.isa.dependency.config.DownloadConfig;
import com.index.isa.dependency.config.ElasticConfig;
import com.index.isa.dependency.model.MessageModel;
import com.index.isa.dependency.util.IndexAuth;
import com.index.isa.dependency.util.RestClients;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class DAOServices {

    @Autowired
    IndexAuth indexAuth;
    @Autowired
    private ElasticConfig elasticConfig;
    @Autowired
    private DownloadConfig download;

    public MessageModel generateAuthor(String topic, String start, String end) throws IOException {
        MessageModel msg = new MessageModel();
        String fileName = "";
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Author Sheet");
        Row rows = sheet.createRow(0);
        Cell cells0 = rows.createCell(0);
        cells0.setCellValue("Media");
        Cell cells1 = rows.createCell(1);
        cells1.setCellValue("Author");
        Cell cells2 = rows.createCell(2);
        cells2.setCellValue("Count");
        String pattern = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//        String start = simpleDateFormat.format(new Date(1610083193000L));
//        String end = simpleDateFormat.format(new Date(1612772072000L));
        System.out.println(start);
        System.out.println(end);
        List datas = new ArrayList();
        int lenght = 0;
        try {
            final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
            RestClients client = new RestClients();
            SearchRequest request = new SearchRequest();
            request.indices("search-online-news-ima");
            request.scroll(scroll);
            SearchSourceBuilder builder = new SearchSourceBuilder();
            RangeQueryBuilder range = new RangeQueryBuilder("created_at").gte(start).lte(end);
            builder.query(range);
            builder.size(500);
            TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("authors").field("source.keyword").subAggregation(AggregationBuilders.terms("count").field("author"));
            builder.aggregation(aggregationBuilder);
            request.source(builder);
            SearchResponse response = client.createRestHighClient().search(request, RequestOptions.DEFAULT);
            String scrollId = response.getScrollId();
            Aggregations searchHits = response.getAggregations();
            Terms terms = searchHits.get("authors");
            SearchHit[] hits = response.getHits().getHits();
            while (hits.length !=0) {
                for (Terms.Bucket bucket : terms.getBuckets()) {
                    String key = bucket.getKey().toString();
                    Aggregations agg2 = bucket.getAggregations();
                    Terms term2 = agg2.get("count");

                    for (Terms.Bucket bucket2 : term2.getBuckets()) {
                        lenght += 1;
                        String name = bucket2.getKey().toString();
                        if (name.length() == 0) {
                            name = "no name";
                        }
                        int count = (int) bucket2.getDocCount();
                        Row row = sheet.createRow(lenght);
                        Cell cell0 = row.createCell(0);
                        cell0.setCellValue(key);
                        Cell cell1 = row.createCell(1);
                        cell1.setCellValue(name);
                        Cell cell2 = row.createCell(2);
                        cell2.setCellValue(count);
                        datas.add("media = " + key + ", author = " + name + ", count=" + count);
                    }
                }
                System.out.println(lenght);
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(scroll);
                response = client.createRestHighClient().scroll(scrollRequest, RequestOptions.DEFAULT);
                scrollId = response.getScrollId();
                hits = response.getHits().getHits();
            }
//            fileName = "online-news-ima-"+start.replace(" ","")+"-"+end.replace(" ","")+".xlsx".replace(" ","");
            fileName = "authorAPI.xlsx";

            ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(scrollId);
            ClearScrollResponse clearScrollResponse = client.createRestHighClient().clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            System.out.println("Full "+download.getPath_save()+"/"+fileName);
            FileOutputStream outputStream = new FileOutputStream(download.getPath_save()+"/"+fileName);
            workbook.write(outputStream);
            outputStream.close();
//            client.createRestHighClient().close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println(fileName);
        msg.setData(fileName);
        return msg;
    }

}
