package com.test.elastaticsearch.service;

import com.test.elastaticsearch.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * Created by LQ on 2019/8/6 19:48
 */
public interface BookService {

    Book saveBook(Book book);

    Book findById(Integer id);

    Page<Book> findByTitle(String title,PageRequest pageRequest);

    Page<Book> findByAuthor(String author,PageRequest pageRequest);

    List<Book> findALL();

    void deleteBookById(Integer id);

    List<Book> findHighLight(String title, int i, int i1);
}
