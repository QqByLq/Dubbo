package com.test.elastaticsearch.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Created by LQ on 2019/8/6 19:41
 * 让该类对应一个索引库
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "books",type = "book")
public class Book {
    @Id
    private Integer id;
    @Field(type = FieldType.Auto)
    private String title;

    private String author;
}
