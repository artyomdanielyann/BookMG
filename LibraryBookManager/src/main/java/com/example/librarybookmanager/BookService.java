package com.example.librarybookmanager;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {


    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<BookResponseDto> getAllBooks() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public BookResponseDto getBookById(Long id) {
        Book book = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        return mapToResponse(book);
    }

    public BookResponseDto createBook(BookRequestDto dto) {
        if (repository.existsByIsbn(dto.getIsbn())) {
            throw new DuplicateIsbnException(dto.getIsbn());
        }
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setPublishedYear(dto.getPublishedYear());
        book.setAvailable(dto.isAvailable());

        Book saved = repository.save(book);
        return mapToResponse(saved);
    }

    public BookResponseDto updateBook(Long id, BookRequestDto dto) {
        Book book = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        if (repository.existsByIsbnAndIdNot(dto.getIsbn(), id)) {
            throw new DuplicateIsbnException(dto.getIsbn());
        }

        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setPublishedYear(dto.getPublishedYear());
        book.setAvailable(dto.isAvailable());

        Book updated = repository.save(book);
        return mapToResponse(updated);
    }

    public void deleteBook(Long id) {
        if (!repository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private BookResponseDto mapToResponse(Book book) {
        return new BookResponseDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPublishedYear(),
                book.isAvailable()
        );
    }
}