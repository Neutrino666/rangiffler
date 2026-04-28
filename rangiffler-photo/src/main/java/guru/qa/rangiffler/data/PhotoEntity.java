package guru.qa.rangiffler.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

@Getter
@Setter
@Entity
@Table(name = "photo")
@ParametersAreNonnullByDefault
public class PhotoEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
  private UUID id;

  @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
  private UUID userId;

  @Column(nullable = false)
  private CountryValues country;

  @Column(unique = true)
  private String description;

  @Lob
  @Column(columnDefinition = "LONGBLOB")
  private byte[] photo;

  @Column(name = "created_date", columnDefinition = "DATE", nullable = false)
  private Date createdDate;

  @OneToMany(mappedBy = "photo", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
      fetch = FetchType.LAZY, orphanRemoval = true)
  private List<LikeEntity> likes = new ArrayList<>();

  public void updateLike(UUID userId) {
    if (!removeIfExist(userId)) {
      LikeEntity like = new LikeEntity();
      like.setUserId(userId);
      like.setCreatedDate(new Date());
      like.setPhoto(this);
      likes.add(like);
    }
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    Class<?> oEffectiveClass =
        o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
            : o.getClass();
    Class<?> thisEffectiveClass =
        this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
            : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) {
      return false;
    }
    PhotoEntity that = (PhotoEntity) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy
        ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }

  private boolean removeIfExist(UUID userId) {
    return likes.removeIf(l -> userId.equals(l.getUserId()));
  }
}
