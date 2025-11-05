package com.shijam.advancedsearchjpaspecification.controller;

import com.shijam.advancedsearchjpaspecification.dto.BookDTO;
import com.shijam.advancedsearchjpaspecification.dto.BookSearchCriteria;
import com.shijam.advancedsearchjpaspecification.service.BookService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public String search(@Valid @ModelAttribute(name = "search") BookSearchCriteria search,
                                BindingResult bindingResult,
                                @PageableDefault(size = 10,sort = "title") Pageable pageable,
                                Model model)
    {

        if (bindingResult.hasErrors()) {
            model.addAttribute("page", Page.empty());
            return "books";
        }

        Page<BookDTO> books = bookService.findSearchResults(search, pageable);
        model.addAttribute("page",books);

        String sortProperty = pageable.getSort().stream()
                .findFirst()
                .map(Sort.Order::getProperty)
                .orElse("title");
        String sortDir = pageable.getSort().stream()
                .findFirst()
                .map(order -> order.getDirection().name().toLowerCase())
                .orElse("asc");
        model.addAttribute("sortBy", sortProperty);
        model.addAttribute("sortDir", sortDir);

        model.addAttribute("publishers", bookService.getAllPublishers());
        model.addAttribute("genres",bookService.getAllGenres());

        int totalPages = books.getTotalPages();
        if (totalPages > 0) {
            int currentPage = books.getNumber() + 1;
            int startPage = Math.max(1, currentPage - 2);
            int endPage = Math.min(totalPages, currentPage + 2);

            List<Integer> pageRange = new ArrayList<>();
            for (int i = startPage; i <= endPage; i++) {
                pageRange.add(i);
            }

            model.addAttribute("pageRange", pageRange);
        }
        return "books";
    }

}
