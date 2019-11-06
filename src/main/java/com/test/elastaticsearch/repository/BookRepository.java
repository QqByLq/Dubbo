package com.test.elastaticsearch.repository;

import com.test.elastaticsearch.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by LQ on 2019/8/6 19:44
 */
public interface BookRepository extends ElasticsearchRepository<Book,Integer> {

        Page<Book> findByTitle(String title, Pageable pageable);
        Page<Book> findByAuthor(String author,Pageable pageable);

}
