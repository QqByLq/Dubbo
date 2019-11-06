package com.test.elastaticsearch.service.Impl;

import com.test.elastaticsearch.domain.Book;
import com.test.elastaticsearch.repository.BookRepository;
import com.test.elastaticsearch.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by LQ on 2019/8/6 19:48
 */
@Service
@Slf4j
@Transactional(isolation = Isolation.DEFAULT,propagation = Propagation.REQUIRED)
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book findById(Integer id) {
        Optional<Book> optional = bookRepository.findById(id);

        boolean present = optional.isPresent();

        log.warn("present="+present);
        if (present){
            return optional.get();
        }

        return null;
    }

    @Override
    public Page<Book> findByTitle(String title,PageRequest pageRequest) {
        return bookRepository.findByTitle(title,pageRequest);
    }

    @Override
    public Page<Book> findByAuthor(String author,PageRequest pageRequest) {
        return bookRepository.findByAuthor(author,pageRequest);
    }

    @Override
    public List<Book> findALL() {
        return (List<Book>) bookRepository.findAll();
    }

    @Override
    public void deleteBookById(Integer id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<Book> findHighLight(String title, int pageNum, int pageSize) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("title",title))
                .should(QueryBuilders.matchQuery("author",title));

        //构建器模式，一般用于大量设置的时候
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                //设置查询条件
                .withQuery(queryBuilder)
                //设置要高亮的字段
                .withHighlightFields(new HighlightBuilder.Field("title"),new HighlightBuilder.Field("author"))
                //设置高亮语法
                .withHighlightBuilder(new HighlightBuilder().preTags("<span style='color:red'>").postTags("</span>"))
                .build();

        //分页并高亮查询
        AggregatedPage<Book> books = elasticsearchTemplate.queryForPage(searchQuery, Book.class, new SearchResultMapper() {
            //根据结果进行查询后的回调方法
            //response：里面包含了查询的结果
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
               List<Book> books = new ArrayList<>();
                //获取命中的结果--->hut猎人--->hit
                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits){
                    if (hits.getHits().length<=0){
                        return null;
                    }
                    //得到按照查询条件获取的查询结果
                    Map<String, Object> resultMap = hit.getSourceAsMap();
                    Integer id = (Integer) resultMap.getOrDefault("id", 0);

                    //从结果集上来看，是不包含span标签
                    String title = (String) resultMap.getOrDefault("title","");
                    String author = (String) resultMap.getOrDefault("author","");
                    Book book = new Book();
                    book.setId(id);

                    //注意：在此处考虑给属性添加带高亮标签的属性
                    HighlightField titleField = hit.getHighlightFields().get("title");

                    if (titleField==null){
                        book.setTitle(title);
                    }else {
                        book.setTitle(titleField.fragments()[0].toString());
                    }

                    HighlightField authorField = hit.getHighlightFields().get("author");
                    if (authorField==null){
                        book.setAuthor(author);

                    }else {
                        book.setAuthor(authorField.fragments()[0].toString());
                    }

                    books.add(book);
                }

                if (books.size()>0){
                    return new AggregatedPageImpl<T>((List<T>) books);

                }
                return null;
            }
        });
        //取出分页后的集合
        return books.getContent();
    }
}
