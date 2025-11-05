package com.shijam.advancedsearchjpaspecification.service;

import com.shijam.advancedsearchjpaspecification.dto.BookDTO;
import com.shijam.advancedsearchjpaspecification.dto.BookSearchCriteria;
import com.shijam.advancedsearchjpaspecification.entity.Book;
import com.shijam.advancedsearchjpaspecification.entity.BookPublisher;
import com.shijam.advancedsearchjpaspecification.mapper.BookMapper;
import com.shijam.advancedsearchjpaspecification.repository.BookPublisherRepository;
import com.shijam.advancedsearchjpaspecification.repository.BookRepository;
import com.shijam.advancedsearchjpaspecification.specification.BookSpecification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@AllArgsConstructor
public class BookService {

    final BookRepository bookRepository;
    final BookPublisherRepository bookPublisherRepository;

    public Page<BookDTO> findSearchResults(BookSearchCriteria filters, Pageable pageable)
    {
        Specification<Book> bookSpecification = BookSpecification.filterMaker(filters);
        Page<Book> result = bookRepository.findAll(bookSpecification, pageable);
        return result.map(BookMapper::toDTO);
    }

    public List<String> getAllPublishers()
    {
        return bookPublisherRepository.findAll()
                .stream()
                .map(BookPublisher::getName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    public List<String> getAllGenres()
    {
       return bookRepository.findAll()
                .stream()
                .map(Book::getGenre)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

}