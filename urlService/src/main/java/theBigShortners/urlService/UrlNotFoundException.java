package theBigShortners.urlService;

public class UrlNotFoundException extends RuntimeException {

    UrlNotFoundException(Long id) {
        super("Could not find url map " + id);
    }

}
