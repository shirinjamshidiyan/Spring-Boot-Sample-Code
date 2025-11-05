package com.shijam.advancedsearchjpaspecification.mapper;

import com.shijam.advancedsearchjpaspecification.dto.BookDTO;
import com.shijam.advancedsearchjpaspecification.entity.Book;

public class BookMapper {
    public static BookDTO toDTO(Book book)
    {
        if (book == null) return null;
        return BookDTO.builder()
                .title(book.getTitle())
                .genre(book.getGenre())
                .authors(book.getAuthors())
                .publisherName(book.getPublisher() != null ?
                        book.getPublisher().getName() : null)
                .price(book.getPrice())
                .availability(book.getAvailability())
                .publishYear(book.getPublishYear())
                .build();
    }
}
