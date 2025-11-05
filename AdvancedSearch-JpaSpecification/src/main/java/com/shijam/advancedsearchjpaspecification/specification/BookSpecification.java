package com.shijam.advancedsearchjpaspecification.specification;

import com.shijam.advancedsearchjpaspecification.dto.BookSearchCriteria;
import com.shijam.advancedsearchjpaspecification.entity.Book;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.SetJoin;
import lombok.Builder;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BookSpecification {

    static Specification<Book> hasTitle(String title) //// (partial, case-insensitive)
    {
        return (root, query, cb) ->
                (title==null || title.trim().isEmpty())? null:
                        cb.like(cb.lower(root.get("title")),"%"+title.toLowerCase() +"%");
    }
    static Specification<Book> hasGenre(String genre) //// (exact, case-insensitive)
    {
        return (root, query, cb) ->
                (genre==null || genre.trim().isEmpty())? null:
                        cb.equal(cb.lower(root.get("genre")),genre.toLowerCase());
    }
    static Specification<Book> hasAuthor(String author) //// (partial, case-insensitive)
    {
        return (root, query, cb) -> {
            if(author==null || author.trim().isEmpty()) return null;

            Join<Book, String> authorsJoin = root.joinSet("authors", JoinType.LEFT);
            query.distinct(true);
            return cb.like(cb.lower(authorsJoin),"%"+ author.toLowerCase()+"%");
        };
    }
    static Specification<Book> hasPublisher(String publisherName) //// (partial, case-insensitive)
    {
        return (root, query, cb) -> {
            if(publisherName==null || publisherName.trim().isEmpty()) return null;

            Join<Object, Object> publisherPart = root.join("publisher",JoinType.LEFT);
            query.distinct(true);
            return cb.like(cb.lower(publisherPart.get("name")),"%"+publisherName.toLowerCase()+"%");
        };
    }
    static Specification<Book> availabilityCheck(Boolean available)
    {
        return (root, query, cb) ->
                (available==null)? null:
                        cb.equal(root.get("availability"),available);
    }
    static Specification<Book> hasPublishYear(Integer year)
    {
        return (root, query, cb) ->
                (year==null)? null:
                        cb.equal(root.get("publishYear"),year);
    }
    static Specification<Book> checkPrice(BigDecimal min, BigDecimal max)
    {
        return (root, query, cb) -> {
            if (min == null && max == null)
                return null;
            else if (min != null && max != null)
                return cb.between(root.get("price"),min,max);
            else if (max != null)
                return cb.lessThanOrEqualTo(root.get("price"),max);
            return cb.greaterThanOrEqualTo(root.get("price"),min);
        };
    }

    public static Specification<Book> filterMaker(BookSearchCriteria filters)
    {
        List<Specification<Book>> specs= new ArrayList<>();
        specs.add(hasTitle(filters.getTitle()));
        specs.add(hasGenre(filters.getGenre()));
        specs.add(hasAuthor(filters.getAuthor()));
        specs.add(hasPublisher(filters.getPublisher()));
        specs.add(availabilityCheck(filters.getAvailability()));
        specs.add(hasPublishYear(filters.getPublishYear()));
        specs.add(checkPrice(filters.getMinPrice(),filters.getMaxPrice()));

        Specification<Book> result=null;
        for(Specification<Book> sp: specs)
        {
            if(sp==null) continue;

            if(result==null)
                result = Specification.where(sp);
            else if("OR".equalsIgnoreCase(filters.getOperator()))
                result = result.or(sp);
            else
                result = result.and(sp);
        }
        return result;

    }

}
