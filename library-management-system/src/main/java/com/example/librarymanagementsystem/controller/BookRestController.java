package com.example.librarymanagementsystem.controller;

import com.example.librarymanagementsystem.entity.Book;
import com.example.librarymanagementsystem.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
public class BookRestController {

  private final BookService bookService;

  public BookRestController(BookService bookService) {
    this.bookService = bookService;
  }

  @GetMapping("/all")
  public ResponseEntity<List<Book>> getAllBooks(){
    List<Book> books = bookService.findAllBooks();
    return new ResponseEntity<>(books, HttpStatus.OK);
  }

  @PostMapping("/add")
  public ResponseEntity<Long> addNewBook(@RequestBody Book book){
    Book insertedtBook = bookService.createBook(book);

    if(insertedtBook == null){
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity(insertedtBook.getId(),HttpStatus.OK);
  }

  @PutMapping("/update")
  public ResponseEntity updateBook(@RequestBody Book book){
      bookService.updateBook(book);
      return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity deleteBook(@PathVariable("id") Long id){
    bookService.deleteBook(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
