package com.test.elastaticsearch.web;

import com.test.elastaticsearch.domain.Book;
import com.test.elastaticsearch.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by LQ on 2019/8/6 19:49
 */
@RestController
@RequestMapping("/book")
public class BookController {
    @Resource
    private BookService bookService;

    @PostMapping("/")//添加
    public Book saveBook(@RequestBody Book book){
        return bookService.saveBook(book);
    }
    @GetMapping("/id/{id}")//ID查询
    public Book getBook(@PathVariable("id") Integer id){
        return bookService.findById(id);
    }
    @GetMapping("/")//查询所有
    public List<Book> getBooks(){
        return bookService.findALL();
    }
    @GetMapping("/title/{title}")
    public Page<Book> getBookByTitle(@PathVariable("title") String title){
        PageRequest pageRequest = PageRequest.of(0,10,Sort.by(Sort.Direction.DESC,"id"));
        return bookService.findByTitle(title,pageRequest);
    }
    @GetMapping("/author/{author}")
    public Page<Book> getByAuthor(String author){
        PageRequest pageRequest = PageRequest.of(0,10,Sort.by(Sort.Direction.DESC,"id"));
        return bookService.findByAuthor(author,pageRequest);
    }
    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable("id") Integer id){
        bookService.deleteBookById(id);
        return "success";
    }

    @GetMapping("/h1/{title}")
    public List<Book> getHighLight(@PathVariable("title") String title){
        return bookService.findHighLight(title,0,10);
    }
}
