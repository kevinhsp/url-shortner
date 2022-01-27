package theBigShortners.urlService;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class UrlMap {

    private @Id @GeneratedValue Long id;
    private final String longUrl;
    private String key;

    public UrlMap(String longUrl, String key) {
        this.longUrl = longUrl;
        this.key = key;
    }

    public Long getId() {
        return id;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String customizedKey) {
        this.key = customizedKey;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UrlMap)) {
            return false;
        }
        UrlMap map = (UrlMap) o;
        return Objects.equals(this.id, map.id) && Objects.equals(this.longUrl, map.longUrl) && Objects.equals(this.key, map.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.longUrl, this.key);
    }

    @Override
    public String toString() {
        return "UrlMap{ " + "id=" + this.id + ", key=" + this.key + ", longUrl=" + this.longUrl + " }";
    }
}
